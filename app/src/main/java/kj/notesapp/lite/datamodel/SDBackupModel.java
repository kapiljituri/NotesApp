package kj.notesapp.lite.datamodel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import kj.notesapp.lite.BackupListViewItem;

/**
 * Created by Kapil on 1/6/2015.
 */
public class SDBackupModel {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_NOTE = "note";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_FLAG = "flag";

    public static final String DATABASE_NAME = "NotesApp";
    public static final String DATABASE_TABLE = "NotesList";
    public static final int DATABASE_VERSION = 1;


    public List<BackupListViewItem> readBackups() {

        List<BackupListViewItem> items2 = new ArrayList<BackupListViewItem>();

        String path = Environment.getExternalStorageDirectory().toString()+"//NotesApp//backup";
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            items2.add(new BackupListViewItem(file[i].getName().replace(".napp", ""), 0));
        }

//        items2.add(new BackupListViewItem("Hello", 0));
//        items2.add(new BackupListViewItem("Awesome", 0));
//        items2.add(new BackupListViewItem("Great", 0));
//        items2.add(new BackupListViewItem("Outstanding", 0));
//        items2.add(new BackupListViewItem("Astonishing", 0));

        return items2;

    }
}
