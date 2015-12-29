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
public class PasswordActivity extends Activity {

    EditText etPass;
    TextView tvErrorMsg;
    Button btUnlock;

    String SP_UI_FLOW = "notesapp.ui";
    String sp_key_update_list = "update.list";
    String sp_key_password = "password";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(StaticData.DARK_THEME){
            setTheme(R.style.MyEditTheme);
        }
        setContentView(R.layout.activity_password);

        prefs = this.getSharedPreferences(
                SP_UI_FLOW, Context.MODE_PRIVATE);

        etPass = (EditText) findViewById(R.id.etPass);
        tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
        btUnlock = (Button) findViewById(R.id.btUnlock);

        if(StaticData.DARK_THEME){
            btUnlock.setTextColor(Color.parseColor("#eeeeee"));
        }

        btUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prefs.getString(sp_key_password, "n").toString().equals(etPass.getText().toString())){
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    StaticData.IS_UNLOCKED = true;
                    finish();
                } else {
                    tvErrorMsg.setText("Incorrect password");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            tvErrorMsg.setText("");
                        }
                    }, 1500);

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.password, menu);
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
