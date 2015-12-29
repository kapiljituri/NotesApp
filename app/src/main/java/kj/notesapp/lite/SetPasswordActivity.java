package kj.notesapp.lite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
public class SetPasswordActivity extends Activity {

    Button btSave;
    EditText etPass, etCPass;
    TextView tvError;

    String SP_UI_FLOW = "notesapp.ui";
    String sp_key_locked = "locked";
    String sp_key_password = "password";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(StaticData.DARK_THEME){
            setTheme(R.style.MyEditTheme);
        }
        setContentView(R.layout.activity_set_password);

        btSave = (Button) findViewById(R.id.btSave);
        etPass = (EditText) findViewById(R.id.etPass);
        etCPass = (EditText) findViewById(R.id.etCPass);
        tvError = (TextView) findViewById(R.id.tvError);
        prefs = this.getSharedPreferences(
                SP_UI_FLOW, Context.MODE_PRIVATE);

        if(StaticData.DARK_THEME){
            btSave.setTextColor(Color.parseColor("#eeeeee"));
        }

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPass.getText().toString().length() < 4){
                    tvError.setText("Password should be at least 4 characters long.");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            tvError.setText("");
                        }
                    }, 2500);
                } else if(!(etPass.getText().toString().equals(etCPass.getText().toString()))){
                    tvError.setText("Passwords do not match.");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            tvError.setText("");
                        }
                    }, 1500);
                } else {
                    tvError.setText("");
                    prefs.edit().putInt(sp_key_locked, 1).apply();
                    prefs.edit().putString(sp_key_password, etCPass.getText().toString()).apply();

                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.set_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
