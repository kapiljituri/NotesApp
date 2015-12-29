package kj.notesapp.lite;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class AboutActivity extends Activity {

    Button btShare, btChangelog, btFeedback, btRate, btCredits;

    ImageButton ibFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(StaticData.DARK_THEME){
            setTheme(R.style.MyEditTheme);
        }
        setContentView(R.layout.activity_about_new);

        btShare = (Button) findViewById(R.id.button);
        btChangelog = (Button) findViewById(R.id.button5);
        btFeedback = (Button) findViewById(R.id.button4);
        btRate = (Button) findViewById(R.id.button2);
        btCredits = (Button) findViewById(R.id.btCredits);

        if(StaticData.DARK_THEME){
            btShare.setTextColor(Color.parseColor("#eeeeee"));
            btChangelog.setTextColor(Color.parseColor("#eeeeee"));
            btFeedback.setTextColor(Color.parseColor("#eeeeee"));
            btRate.setTextColor(Color.parseColor("#eeeeee"));
            btCredits.setTextColor(Color.parseColor("#eeeeee"));
        }

        ibFacebook = (ImageButton) findViewById(R.id.ibFacebook);

        // Font path
        String fontPath = "fonts/RobotoSlab-Regular.ttf";

        // Loading Font Face
        Typeface tF = Typeface.createFromAsset(getAssets(), fontPath);

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setTypeface(tF);

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Hey, do check out this cool Notes App specially designed for Android 5.0+" +
                        "\nClick on the " +
                        "below link and choose 'Play Store'." +
                        "\nhttps://play.google.com/store/apps/details?id=kj.notesapp.lite&hl=en";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Do check this");
                i.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(i);

            }
        });

        btChangelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create custom dialog object
                final Dialog dialog = new Dialog(AboutActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.changelog_dialog);
                // Set dialog title
                dialog.setTitle("Changelog");
                dialog.show();
            }
        });

        btCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create custom dialog object
                final Dialog dialog = new Dialog(AboutActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.credits_dialog);
                // Set dialog title
                dialog.setTitle("Credit");
                dialog.show();
            }
        });

        btFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = getString(R.string.app_dev_email);
                String emailAddresses[] = { emailAddress };
                String emailSubject = "Notes App - Feedback";

                Intent emailIntent = new Intent(
                        Intent.ACTION_SEND);
                emailIntent.putExtra(
                        Intent.EXTRA_EMAIL,
                        emailAddresses);
                emailIntent.putExtra(
                        Intent.EXTRA_SUBJECT,
                        emailSubject);
                emailIntent.setType("plain/text");
                startActivity(emailIntent);

            }
        });

        btRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "market://details?id=kj.notesapp.lite";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        ibFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        launchFacebook();
                    }

                }, 400);

            }
        });
    }

    public final void launchFacebook() {
        String pageid = "954507607892620";
        final String urlFb = "fb://page/"+pageid;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        // If a Facebook app is installed, use it. Otherwise, launch
        // a browser
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://www.facebook.com/pages/"+pageid;
            intent.setData(Uri.parse(urlBrowser));
        }

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.about, menu);
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
