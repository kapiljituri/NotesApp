package kj.notesapp.lite;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kj.notesapp.lite.datamodel.DatabaseHandler;


public class MyActivity extends Activity {

    ListView listView;
    List<ListViewItem> items, searchItems;
    SharedPreferences prefs, prefs2;
    RelativeLayout rlWelcomeLayout, rlMain, rlNoSearch;
    static FloatingActionButton fabButton;

    boolean currentTheme;

    String SP_UI_FLOW = "notesapp.ui";
    String sp_key_locked = "locked";

    public static final String PREFS_NAME = "kj.notesapp.lite_preferences.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StaticData.FONT = prefs.getString("setting_font", "1");
        StaticData.DARK_THEME = prefs.getBoolean("setting_darktheme", false);
        StaticData.ANIMATIONS = prefs.getBoolean("setting_animations", true);
        currentTheme = StaticData.DARK_THEME;

        if(StaticData.DARK_THEME){
            setTheme(R.style.MyEditTheme);
        }

        setContentView(R.layout.activity_my);

        initialize();

        // Font path
        String fontPath = "fonts/RobotoSlab-Regular.ttf";
        // Loading Font Face
        Typeface tF = Typeface.createFromAsset(getApplicationContext().getAssets(), fontPath);
        TextView tv = (TextView) findViewById(R.id.tvNoNoteMessage);
        TextView tv2 = (TextView) findViewById(R.id.tvNoSearch);

        if(StaticData.FONT.equals("1")){
            tv.setTypeface(tF);
            tv2.setTypeface(tF);
        }


        if(!StaticData.IS_UNLOCKED) {
            passwordPass();
        }

        setupFab();

        loadListView();

        setListeners();

        prefs2 = PreferenceManager.getDefaultSharedPreferences(this);

        StaticData.AUTO_SAVE = prefs2.getBoolean("setting_autosave", false);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupFab() {

        //String color = "#eee84e40";
        String color = getResources().getString(R.string.ltcolorprimary);
        Drawable drw = getDrawable(R.drawable.ic_action_content_add);
        if(StaticData.DARK_THEME){
            color = getResources().getString(R.string.dtcolorprimary);
            drw = getDrawable(R.drawable.ic_blue_action_content_add);
        }
        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(drw)

                .withButtonColor(Color.parseColor(color))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){

            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private boolean passwordPass() {

        int isLocked = prefs.getInt(sp_key_locked, 0);
        if(isLocked == 1){
            Intent i = new Intent(this, PasswordActivity.class);
            startActivityForResult(i, 1);
        } else {
            StaticData.IS_UNLOCKED = true;
            return true;
        }


        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            StaticData.IS_UNLOCKED = false;
        }
    }


    private void setListeners() {

        fabButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                fabButton.hideFloatingActionButton();

                final Intent myIntent = new Intent(MyActivity.this, NewNote.class);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MyActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                startActivity(myIntent);
                            }
                        });
                    }
                }, 200);
            }
        });

    }


    private void initialize() {

        listView = (ListView) findViewById(R.id.listView);
        items = new ArrayList<ListViewItem>();

        prefs = this.getSharedPreferences(
                SP_UI_FLOW, Context.MODE_PRIVATE);

        rlWelcomeLayout = (RelativeLayout) findViewById(R.id.rlWelcome);
        rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        rlNoSearch = (RelativeLayout) findViewById(R.id.rlNoSearch);

    }

    public void loadListView() {


        DatabaseHandler dbh = new DatabaseHandler(this);
        dbh.open();
        items = dbh.readDatabase();
        dbh.close();

        CustomListViewAdapter adapter = new CustomListViewAdapter(this, items);
        listView.setAdapter(adapter);

        if(items.size() == 0){
            rlWelcomeLayout.setVisibility(View.VISIBLE);
        } else {
            rlWelcomeLayout.setVisibility(View.INVISIBLE);
        }

    }

    public void searchResults(String text){

        ListViewItem lvi;
        searchItems = cloneList(items);
        List<ListViewItem> found = new ArrayList<>();

        if(text.equals("")){
            CustomListViewAdapter adapter = new CustomListViewAdapter(this, items);
            listView.setAdapter(adapter);
            rlNoSearch.setVisibility(View.INVISIBLE);
        } else {

            for(int i=0; i<searchItems.size(); i++){

                lvi = searchItems.get(i);

                if( (!lvi.note.toLowerCase().contains(text.toLowerCase())) && (!lvi.title.toLowerCase().contains(text.toLowerCase())) ){
                    found.add(lvi);
                }
            }

            if(!found.isEmpty()){
                searchItems.removeAll(found);
            }

            CustomListViewAdapter adapter = new CustomListViewAdapter(this, searchItems);
            listView.setAdapter(adapter);

            if(searchItems.size() == 0){
                rlNoSearch.setVisibility(View.VISIBLE);
            } else {
                rlNoSearch.setVisibility(View.INVISIBLE);
            }
        }




    }

    public static List<ListViewItem> cloneList(List<ListViewItem> list) {
        List<ListViewItem> clone = new ArrayList<>(list.size());
        for(ListViewItem item: list) clone.add(new ListViewItem(item));
        return clone;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(StaticData.DARK_THEME != currentTheme){
            finish();
            startActivity(getIntent());
        }

        new Timer().schedule(new TimerTask(){
            public void run() {
                MyActivity.this.runOnUiThread(new Runnable() {
                    public void run() {


                        //int upd_lv = prefs.getInt(sp_key_update_list, 0);

                        if(StaticData.UPDATE_LIST){
                            loadListView();
                            listView.invalidate();
                            //Toast.makeText(getApplicationContext(), "Updated List", Toast.LENGTH_SHORT).show();
                            //prefs.edit().putInt(sp_key_update_list, 0).apply();
                            StaticData.UPDATE_LIST = false;
                            //Toast.makeText(getApplicationContext(), "Updated List", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        }, 0);

        if(fabButton.isHidden()){

            new Timer().schedule(new TimerTask(){
                public void run() {
                    MyActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            fabButton.showFloatingActionButton();
                        }
                    });
                }
            }, 500);


        }

        StaticData.AUTO_SAVE = prefs2.getBoolean("setting_autosave", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(StaticData.DARK_THEME){
            getMenuInflater().inflate(R.menu.blue_my, menu);
        } else {
            getMenuInflater().inflate(R.menu.my, menu);
        }

        SearchView searchView=(SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_SHORT).show();
                searchResults(newText);
                return false;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent myIntent = new Intent(MyActivity.this, SettingsActivity.class);
            startActivity(myIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
