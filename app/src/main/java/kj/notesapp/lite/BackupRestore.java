package kj.notesapp.lite;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kj.notesapp.lite.datamodel.DatabaseHandler;
import kj.notesapp.lite.datamodel.SDBackupModel;


public class BackupRestore extends Activity {

    Button btBackup, btRestore;
    TextView tvInfo;

    ListView lvBackup;

    String backupFileName;

    List<BackupListViewItem> mItems;

    public static final String DATABASE_NAME = "NotesApp";
    public static final String DATABASE_TABLE = "NotesList";
    public static final String PACKAGE_NAME = "kj.notesapp.lite";
    public static final String NOTES_APP_BACKUP_FOLDER = "/NotesApp/backup/";
    public String BACKUP_FILE_TEMPLATE = "notes_app_backup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        btBackup = (Button) findViewById(R.id.btBackup);
        btRestore = (Button) findViewById(R.id.btRestore);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        lvBackup = (ListView) findViewById(R.id.lvBackup);

        mItems = new ArrayList<BackupListViewItem>();
        
        loadBackupListView();
        
        

        btBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInfo.setText("Backup");
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();

                    Calendar c = Calendar.getInstance();
                    String mmonth = "_";

                    switch(c.get(Calendar.MONTH)){
                        case 0: mmonth = "Jan";
                            break;
                        case 1: mmonth = "Feb";
                            break;
                        case 2: mmonth = "Mar";
                            break;
                        case 3: mmonth = "Apr";
                            break;
                        case 4: mmonth = "May";
                            break;
                        case 5: mmonth = "Jun";
                            break;
                        case 6: mmonth = "Jul";
                            break;
                        case 7: mmonth = "Aug";
                            break;
                        case 8: mmonth = "Spt";
                            break;
                        case 9: mmonth = "Oct";
                            break;
                        case 10: mmonth = "Nov";
                            break;
                        case 11: mmonth = "Dec";
                            break;
                    }



                    backupFileName = c.get(Calendar.DAY_OF_MONTH) + " " + mmonth +", "+ c.get(Calendar.YEAR);

                    BACKUP_FILE_TEMPLATE = c.get(Calendar.DAY_OF_MONTH) + " " + mmonth +", "+ c.get(Calendar.YEAR);




                    File testFile1 = new File(sd.getPath() + NOTES_APP_BACKUP_FOLDER + BACKUP_FILE_TEMPLATE);

                    if(testFile1.exists()) {

                        for (int i = 1; i < 32; i++) {

                            /*File testFile = new File(sd.getPath() + NOTES_APP_BACKUP_FOLDER + BACKUP_FILE_TEMPLATE + "(" + i + ")");
                            if (!testFile.exists()) {
                                backupFileName = backupFileName + "(" + i + ")";
                                break;
                            }*/
                            Log.d("NA", backupFileName);

                        }
                    }

                    if (sd.canWrite()) {
                        String currentDBPath = "//data//" + PACKAGE_NAME
                                + "//databases//" + DATABASE_NAME;
                        //Toast.makeText(getBaseContext(), sd.getPath() + NOTES_APP_BACKUP_FOLDER + backupFileName, Toast.LENGTH_LONG).show();
                        String backupDBPath = NOTES_APP_BACKUP_FOLDER + backupFileName;

                        backupDBPath = backupDBPath + ".napp";

                        File currentDB = new File(data, currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();



                        Toast.makeText(getBaseContext(), "Backup Successful!",
                                Toast.LENGTH_SHORT).show();

                    }


                } catch (Exception e) {

                    Toast.makeText(getBaseContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                            .show();

                }

            }
        });

        btRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    tvInfo.setText("Restore");
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();
                    if (sd.canWrite()) {
                        String currentDBPath = "//data//" + PACKAGE_NAME
                                + "//databases//" + DATABASE_NAME;
                        String backupDBPath = "//NotesApp//backup.db"; // From SD directory.
                        File backupDB = new File(data, currentDBPath);
                        File currentDB = new File(sd, backupDBPath);

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Toast.makeText(getBaseContext(), "Restore Successful!",
                                Toast.LENGTH_SHORT).show();


                    }
                } catch (Exception e) {

                    Toast.makeText(getBaseContext(), "Restore Failed!", Toast.LENGTH_SHORT)
                            .show();

                }

            }
        });


    }

    private void loadBackupListView() {

        SDBackupModel sdBackup = new SDBackupModel();
        //dbh.open();
        mItems = sdBackup.readBackups();
        //dbh.close();

        BackupListViewAdapter adapter = new BackupListViewAdapter(this, mItems);
        lvBackup.setAdapter(adapter);

        if(mItems.size() == 0){
            //rlWelcomeLayout.setVisibility(View.VISIBLE);
        } else {
            //rlWelcomeLayout.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.backup_restore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_backup) {

            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                Calendar c = Calendar.getInstance();
                String mmonth = "_";

                switch(c.get(Calendar.MONTH)){
                    case 0: mmonth = "Jan";
                        break;
                    case 1: mmonth = "Feb";
                        break;
                    case 2: mmonth = "Mar";
                        break;
                    case 3: mmonth = "Apr";
                        break;
                    case 4: mmonth = "May";
                        break;
                    case 5: mmonth = "Jun";
                        break;
                    case 6: mmonth = "Jul";
                        break;
                    case 7: mmonth = "Aug";
                        break;
                    case 8: mmonth = "Spt";
                        break;
                    case 9: mmonth = "Oct";
                        break;
                    case 10: mmonth = "Nov";
                        break;
                    case 11: mmonth = "Dec";
                        break;
                }


                BACKUP_FILE_TEMPLATE = c.get(Calendar.DAY_OF_MONTH) + " " + mmonth +", "+ c.get(Calendar.YEAR);

                backupFileName = BACKUP_FILE_TEMPLATE + ".napp";


                File testFile1 = new File(sd.getPath() + NOTES_APP_BACKUP_FOLDER + backupFileName);

                Toast.makeText(getBaseContext(), "Template: "+backupFileName, Toast.LENGTH_SHORT).show();

                if(testFile1.exists()) {

                    for (int i = 1; i < 32; i++) {

                        File testFile = new File("." + NOTES_APP_BACKUP_FOLDER + BACKUP_FILE_TEMPLATE
                                + "(" + i + ")" + ".napp");
                        Log.d("NAPP", "=====================================================\n"+testFile.getAbsolutePath() +">>" +testFile.exists());//BACKUP_FILE_TEMPLATE
                                //+ "(" + i + ")" + ".napp");
                        if (!testFile.canRead()) {
                            backupFileName = BACKUP_FILE_TEMPLATE + "(" + i + ")" + ".napp" ;
                            Log.d("NAPP NF", BACKUP_FILE_TEMPLATE
                                    + "(" + i + ")" + ".napp");
                            break;
                        }

                    }

                }

                if (sd.canWrite()) {
                    String currentDBPath = "//data//" + PACKAGE_NAME
                            + "//databases//" + DATABASE_NAME;
                    //Toast.makeText(getBaseContext(), sd.getPath() + NOTES_APP_BACKUP_FOLDER + backupFileName, Toast.LENGTH_LONG).show();
                    String backupDBPath = NOTES_APP_BACKUP_FOLDER + backupFileName;

                    backupDBPath = backupDBPath + ".napp";

                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();



                    Toast.makeText(getBaseContext(), "Backup Successful!",
                            Toast.LENGTH_SHORT).show();

                }


            } catch (Exception e) {

                Toast.makeText(getBaseContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                        .show();

            }

            loadBackupListView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
