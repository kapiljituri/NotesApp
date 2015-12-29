package kj.notesapp.lite;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.regex.Pattern;
import kj.notesapp.lite.datamodel.DatabaseHandler;


public class ViewNote extends Activity {

    TextView tvTitle, tvNote;

    String _id, _title, _note;

    SharedPreferences prefs;

    RelativeLayout rlTitle;

    LinearLayout llReveal;

    String SP_UI_FLOW = "notesapp.ui";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(StaticData.DARK_THEME){
            setTheme(R.style.MyEditTheme);
        }
        setContentView(R.layout.activity_view_note_new);

        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        tvTitle = (TextView) findViewById(R.id.tvViewTitle);
        tvNote = (TextView) findViewById(R.id.tvViewNote);
        llReveal = (LinearLayout) findViewById(R.id.llReveal);

        if(StaticData.DARK_THEME){
            rlTitle.setBackgroundColor(Color.parseColor(getResources().getString(R.string.dtcolorprimary)));
            tvTitle.setTextColor(Color.parseColor("#eeeeee"));
            tvTitle.setHintTextColor(Color.parseColor("#888888"));
            tvNote.setTextColor(Color.parseColor("#aaaaaa"));
            tvNote.setHintTextColor(Color.parseColor("#777777"));
        }
        // Applying font
        if(StaticData.FONT.equals("1")) {
            setFonts();
        }


        Intent intent = getIntent();
        _id = intent.getStringExtra("id");
        _title = intent.getStringExtra("title");
        _note = intent.getStringExtra("note");

        //Toast.makeText(getApplicationContext(), _id, Toast.LENGTH_SHORT).show();

        tvTitle.setText(_title);
        tvNote.setText(_note);

        prefs = this.getSharedPreferences(
                SP_UI_FLOW, Context.MODE_PRIVATE);


        llReveal.post( new Runnable(){
                               @Override
                               public void run() {
                                   if(StaticData.ANIMATIONS){
                                       revealLayout();
                                   } else {
                                       llReveal.setVisibility(View.VISIBLE);
                                   }

                               }
                           }
        );

        StaticData.ONE_CLICK = false;

    }


    private void hideLayout(){

        int initialRadius = llReveal.getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(llReveal, 0, 0, initialRadius, 0);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                llReveal.setVisibility(View.INVISIBLE);
                finish();
                overridePendingTransition(0, R.anim.fade_out);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        anim.setDuration(500);
        anim.start();

    }

    private void revealLayout(){

        int endRadius = Math.max(llReveal.getWidth(), llReveal.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(llReveal, StaticData.CX, StaticData.CY, 0, endRadius);

        anim.setDuration(StaticData.VIEW_NOTE_ANIM_DURATION);

        llReveal.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void setFonts() {

        // Font path
        String regFontPath = "fonts/RobotoSlab-Regular.ttf";

        // Loading Font Face
        Typeface rTf = Typeface.createFromAsset(getAssets(), regFontPath);

        // Applying font
        tvTitle.setTypeface(rTf);
        tvNote.setTypeface(rTf);
        tvTitle.setTextSize(18);
        tvNote.setTextSize(16);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(StaticData.DARK_THEME){
            getMenuInflater().inflate(R.menu.blue_view_note, menu);
        } else {
            getMenuInflater().inflate(R.menu.view_note, menu);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(StaticData.UPDATE_VIEW_NOTE){
            DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());
            dbh.open();
            tvTitle.setText(dbh.getTitle(_id));
            tvNote.setText(dbh.getNote(_id));
            dbh.close();
        }

    }



    public void print(WebView webView) {
        //PrintManager
        String PRINT_SERVICE = null;
        try {
            PRINT_SERVICE = (String) Context.class.getDeclaredField("PRINT_SERVICE").get(null);

            Object printManager = getSystemService(PRINT_SERVICE);

            //PrintDocumentAdapter
            Class<?> printDocumentAdapterClass = Class.forName("android.print.PrintDocumentAdapter");

            Method createPrintDocumentAdapterMethod = webView.getClass().getMethod("createPrintDocumentAdapter");
            Object printAdapter = createPrintDocumentAdapterMethod.invoke(webView);

            //PrintAttributes
            Class<?> printAttributesBuilderClass = Class.forName("android.print.PrintAttributes$Builder");
            Constructor<?> ctor = printAttributesBuilderClass.getConstructor();
            Object printAttributes = ctor.newInstance(new Object[] {});
            Method buildMethod = printAttributes.getClass().getMethod("build");
            Object printAttributesBuild = buildMethod.invoke(printAttributes);

            //PrintJob
            String jobName = "My Document";
            Method printMethod = printManager.getClass().getMethod("print", String.class, printDocumentAdapterClass, printAttributesBuild.getClass());
            Object printJob = printMethod.invoke(printManager, jobName, printAdapter, printAttributesBuild);

            // Save the job object for later status checking
            //mPrintJobs.add(printJob);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){

            case R.id.action_edit:
                Intent myIntent = new Intent(ViewNote.this, EditNote.class);
                myIntent.putExtra("id", _id); //Optional parameters
                myIntent.putExtra("title", tvTitle.getText().toString());
                myIntent.putExtra("note", tvNote.getText().toString());
                startActivity(myIntent);

                return true;

            case R.id.action_copy:
                Intent intent2 = getIntent();
                copyToClipboard(getApplicationContext(),intent2.getStringExtra("note"));
                Toast.makeText(getApplicationContext(), "Note has been copied", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.action_share:

                String sharedText;

                Intent intent3 = new Intent();
                intent3.setAction(Intent.ACTION_SEND);
                intent3.setType("text/plain");

                if(_title.equals("") || _title.equals(null)){
                    sharedText = _note;
                } else if(_note.equals("") || _note.equals(null)){
                    sharedText = _title;
                } else {
                    sharedText = _title + "\n" + _note;
                }

                intent3.putExtra(Intent.EXTRA_TEXT, sharedText);
                startActivity(Intent.createChooser(intent3, "Share via"));

                return true;

            case R.id.action_print:

                WebView myWeb = new WebView(getApplicationContext());
                String summary = "<html><body><h2>"+tvTitle.getText()+"</h2><hr>"+tvNote.getText()+"<br><hr>Printed via&nbsp;" +
                        "<a href=\"https://play.google.com/store/apps/details?id=kj.notesapp.lite&hl=en\">Notes app</a>&nbsp;for Android</body></html>";
                summary = summary.replace("\n","<br>");
                myWeb.loadData(summary, "text/html", null);

                print(myWeb);
                return true;

            case R.id.action_delete:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);


                // set dialog message
                alertDialogBuilder
                        .setMessage("Delete this note?")
                                //.setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());
                                dbh.open();
                                dbh.delete(Integer.parseInt(_id));
                                dbh.close();
                                StaticData.UPDATE_LIST = true;
                                //prefs.edit().putInt(sp_key_update_list, 1).apply();
                                finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.dismiss();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText(
                                "This is some message", text);
                clipboard.setPrimaryClip(clip);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
