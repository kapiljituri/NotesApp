package kj.notesapp.lite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import kj.notesapp.lite.datamodel.DatabaseHandler;

public class NewNote extends Activity {

    EditText etTitle, etNote;

    ImageButton ibNewTitle, ibNewNote;

    RelativeLayout rlTitle;

    SharedPreferences prefs;
    LinearLayout ll;

    final int STT_TITLE = 1;
    final int STT_NOTE = 2;
    String SP_UI_FLOW = "notesapp.ui";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(StaticData.DARK_THEME){
            setTheme(R.style.MyEditTheme);
        }

        setContentView(R.layout.activity_new_note);

        initialize();

        if(StaticData.DARK_THEME){
            rlTitle.setBackgroundColor(Color.parseColor(getResources().getString(R.string.dtcolorprimary)));
            etTitle.setTextColor(Color.parseColor("#eeeeee"));
            etTitle.setHintTextColor(Color.parseColor("#888888"));

            etNote.setTextColor(Color.parseColor("#aaaaaa"));
            etNote.setHintTextColor(Color.parseColor("#777777"));
        }

        setFonts();

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etNote.requestFocus();
            }
        });

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }

        ibNewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(STT_TITLE);
            }
        });
        ibNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(STT_NOTE);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("setting_autosave", false)){
            StaticData.AUTO_SAVE = true;
        }

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput(int REQUEST_CODE) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Failed to load Android Speech to Text engine",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String myResult;

        switch (requestCode) {
            case STT_TITLE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    myResult = result.get(0);
                    myResult = myResult.substring(0, 1).toUpperCase() + myResult.substring(1);
                    String text = etTitle.getText().toString();
                    if(text.equals("")){
                        etTitle.setText(myResult);
                    } else {
                        etTitle.setText(text + " " + myResult);
                    }

                }
                break;
            }

            case STT_NOTE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    myResult = result.get(0);
                    myResult = myResult.substring(0, 1).toUpperCase() + myResult.substring(1);
                    String text = etNote.getText().toString();
                    if(text.equals("")){
                        etNote.setText(myResult);
                    } else {
                        etNote.setText(text + " " + myResult);
                    }
                }
                break;
            }

        }
    }

    private void setFonts() {

        // Font path
        String regFontPath = "fonts/RobotoSlab-Regular.ttf";

        // Loading Font Face
        Typeface rTf = Typeface.createFromAsset(getAssets(), regFontPath);

        // Applying font
        if(StaticData.FONT.equals("1")) {
            etTitle.setTypeface(rTf);
            etNote.setTypeface(rTf);
            etTitle.setTextSize(18);
            etNote.setTextSize(16);
        }


    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            etNote.setText(sharedText);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(!(etTitle.getText().toString().equals("") && etNote.getText().toString().equals(""))) {


            if (StaticData.AUTO_SAVE) {
                //prefs.edit().putInt(sp_key_update_list, 1).apply();
                StaticData.UPDATE_LIST = true;
                saveNote();
                delayFinish();
            } else {

                new AlertDialog.Builder(this)
                        .setMessage("What to do with this note?")
                                //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        }).show();
            }

        }
        else {
            finish();
        }

    }

    private void initialize() {

        etTitle = (EditText) findViewById(R.id.etTitle);
        etNote = (EditText) findViewById(R.id.etNote);
        ll = (LinearLayout) findViewById(R.id.llNewNote);

        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);

        ibNewTitle = (ImageButton) findViewById(R.id.ibNewTitle);
        ibNewNote = (ImageButton) findViewById(R.id.ibNewNote);

        prefs = this.getSharedPreferences(
                SP_UI_FLOW, Context.MODE_PRIVATE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if(etTitle.getText().toString().equals("") && etNote.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_SHORT).show();
            } else {
                //prefs.edit().putInt(sp_key_update_list, 1).apply();
                StaticData.UPDATE_LIST = true;
                saveNote();
                delayFinish();
                return true;
            }
        } else if (id == R.id.action_cancel) {

            if(!(etTitle.getText().toString().equals("") && etNote.getText().toString().equals(""))){

                new AlertDialog.Builder(this)
                        .setMessage("Sure to discard?")
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Cancel", null)
                        .setNegativeButton("Yes, Discard", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }}).show();
            } else {
                delayFinish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {

        DatabaseHandler dbh = new DatabaseHandler(NewNote.this);
        dbh.open();
        dbh.createEntry(etTitle.getText().toString(), etNote.getText().toString(), "Created");
        dbh.close();

    }

    void delayFinish(){

        //delay in ms
        int DELAY = 250;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, DELAY);

    }
}
