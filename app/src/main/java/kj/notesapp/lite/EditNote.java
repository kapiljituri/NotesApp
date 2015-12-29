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


public class EditNote extends Activity {

    String _id, _title, _note;
    EditText etTitle, etNote;
    LinearLayout ll;

    RelativeLayout rlTitle;

    ImageButton ibNewTitle, ibNewNote;

    SharedPreferences prefs;

    final int STT_TITLE = 1;
    final int STT_NOTE = 2;

    String SP_UI_FLOW = "notesapp.ui";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(StaticData.DARK_THEME){
            setTheme(R.style.MyEditTheme);
        }
        setContentView(R.layout.activity_edit_note);

        Intent myIntent = getIntent();
        _id = myIntent.getStringExtra("id");
        _title = myIntent.getStringExtra("title");
        _note = myIntent.getStringExtra("note");

        etTitle = (EditText) findViewById(R.id.etTitle);
        etNote = (EditText) findViewById(R.id.etNote);
        ll = (LinearLayout) findViewById(R.id.llEditNote);
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);

        if(StaticData.DARK_THEME){
            rlTitle.setBackgroundColor(Color.parseColor(getResources().getString(R.string.dtcolorprimary)));
            etTitle.setTextColor(Color.parseColor("#eeeeee"));
            etTitle.setHintTextColor(Color.parseColor("#888888"));

            etNote.setTextColor(Color.parseColor("#aaaaaa"));
            etNote.setHintTextColor(Color.parseColor("#777777"));
        }

        ibNewTitle = (ImageButton) findViewById(R.id.ibNewTitle);
        ibNewNote = (ImageButton) findViewById(R.id.ibNewNote);

        // Applying font
        if(StaticData.FONT.equals("1")) {
            setFonts();
        }

        etTitle.setText(_title);
        etNote.setText(_note);

        prefs = this.getSharedPreferences(
                SP_UI_FLOW, Context.MODE_PRIVATE);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etNote.requestFocus();
            }
        });

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

        etNote.setSelection(etNote.getText().length());
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


    @Override
    public void onBackPressed() {

        if(!((_title.equals(etTitle.getText().toString())) && (_note.equals(etNote.getText().toString())))) {

            if (StaticData.AUTO_SAVE) {

                StaticData.UPDATE_LIST = true;
                StaticData.UPDATE_VIEW_NOTE = true;
                saveNote();
                delayFinish();
            } else {

                new AlertDialog.Builder(this)
                        //.setTitle("Discard changes?")
                        .setMessage("Discard changes?")
                        .setPositiveButton("Yes, Discard", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        }).show();

            }
        } else {
            finish();
        }

    }

    private void saveNote() {

        updateNote(_id, etTitle.getText().toString(), etNote.getText().toString());
        delayFinish();

    }


    private void setFonts() {

        // Font path
        String regFontPath = "fonts/RobotoSlab-Regular.ttf";

        // Loading Font Face
        Typeface rTf = Typeface.createFromAsset(getAssets(), regFontPath);

        // Applying font
        etTitle.setTypeface(rTf);
        etNote.setTypeface(rTf);
        etTitle.setTextSize(18);
        etNote.setTextSize(16);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_save){

            StaticData.UPDATE_LIST = true;
            StaticData.UPDATE_VIEW_NOTE = true;
            updateNote(_id, etTitle.getText().toString(), etNote.getText().toString());
            delayFinish();
        }
        else if (id == R.id.action_discard) {
            delayFinish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNote(String id, String title, String note) {

        DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());
        dbh.open();
        dbh.updateEntry(id, title, note);
        dbh.close();
    }
}
