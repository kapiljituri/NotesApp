package kj.notesapp.lite.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kj.notesapp.lite.ListViewItem;

/**
 * Created by Kapil on 6/23/14.
 */
public class DatabaseHandler {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_NOTE = "note";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_FLAG = "flag";

    public static final String DATABASE_NAME = "NotesApp";
    public static final String DATABASE_TABLE = "NotesList";
    public static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private Context ourContext;
    private SQLiteDatabase ourDatabase;

    public DatabaseHandler(Context c) {
        ourContext = c;
    }

    public List<ListViewItem> readDatabase() {

        List<ListViewItem> items  = new ArrayList<ListViewItem>();

        String[] columns = new String[]{KEY_TITLE, KEY_NOTE, KEY_TIMESTAMP, KEY_FLAG, KEY_ROWID};

        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_TIMESTAMP + " DESC");

        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iNote = c.getColumnIndex(KEY_NOTE);
        int iTimestamp = c.getColumnIndex(KEY_TIMESTAMP);
        int iID = c.getColumnIndex(KEY_ROWID);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

            items.add(new ListViewItem(c.getString(iTitle), c.getString(iNote), getTime(Long.parseLong(c.getString(iTimestamp))), getDay(Long.parseLong(c.getString(iTimestamp))), c.getInt(iID)));
        }

        return items;
    }

    public String getDay(long timestamp){

        Timestamp stamp = new Timestamp(timestamp);
        Date date = new Date(stamp.getTime());

        Date today = new Date();
        String day = "";
        if ((today.getDate() == date.getDate()) && (today.getMonth() == date.getMonth()) && (today.getYear() == date.getYear())) {
            day = "Today";
        } else if ((today.getDate() == (date.getDate() + 1)) && (today.getMonth() == date.getMonth()) && (today.getYear() == date.getYear())) {
            day = "Yesterday";
        } else {
            day = date.getDate()+" ";
            switch(date.getMonth()){
                case 0: day = day + "Jan";
                    break;
                case 1: day = day + "Feb";
                    break;
                case 2: day = day + "Mar";
                    break;
                case 3: day = day + "Apr";
                    break;
                case 4: day = day + "May";
                    break;
                case 5: day = day + "Jun";
                    break;
                case 6: day = day + "Jul";
                    break;
                case 7: day = day + "Aug";
                    break;
                case 8: day = day + "Spt";
                    break;
                case 9: day = day + "Oct";
                    break;
                case 10: day = day + "Nov";
                    break;
                case 11: day = day + "Dec";
                    break;
            }
        }


        return day;
    }

    public String getTime(long timestamp){

        Timestamp stamp = new Timestamp(timestamp);
        Date date = new Date(stamp.getTime());

        int hours;
        String min;
        String ampm;
        if (date.getHours() > 12) {
            hours = (date.getHours() - 12);
            ampm = "pm";
        } else if (date.getHours() == 12) {
            hours = date.getHours();
            ampm = "pm";
        }else {
            hours = date.getHours();
            ampm = "am";
        }

        if (date.getMinutes() < 10) {
            min = "0" + date.getMinutes();

        } else {
            min = "" + date.getMinutes();

        }

        String mytime = hours + ":" + min + " " + ampm;


        return mytime;
    }

    public void delete(int id) {

        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" +id, null);

    }

    public void updateEntry(String id, String title, String note) {
        ContentValues cvUpdate = new ContentValues();

        Calendar c = Calendar.getInstance();
        String timestamp = c.getTimeInMillis() + "";

        cvUpdate.put(KEY_TITLE, title);
        cvUpdate.put(KEY_NOTE, note);
        cvUpdate.put(KEY_TIMESTAMP, timestamp);

        ourDatabase.update(DATABASE_TABLE, cvUpdate, KEY_ROWID + " = " + id, null);

    }

    public String getTitle(String id) {

        String[] columns = new String[]{KEY_TITLE, KEY_ROWID};

        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + id, null, null, null, null);

        int iTitle = c.getColumnIndex(KEY_TITLE);

        c.moveToFirst();

        return c.getString(iTitle);

    }


    public String getNote(String id) {

        String[] columns = new String[]{KEY_NOTE, KEY_ROWID};

        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + id, null, null, null, null);

        int iNote = c.getColumnIndex(KEY_NOTE);

        c.moveToFirst();

        return c.getString(iNote);
    }


    private static class DbHelper extends SQLiteOpenHelper{


        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+ DATABASE_TABLE + " (" + KEY_ROWID +
                     " INTEGER PRIMARY KEY AUTOINCREMENT, "+ KEY_TITLE + " TEXT NOT NULL, "
                    + KEY_NOTE + " TEXT NOT NULL, "+ KEY_TIMESTAMP + " TEXT NOT NULL, "
                    + KEY_FLAG + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }

    public long createEntry(String title, String note, String flag){
        ContentValues cv = new ContentValues();

        Calendar c = Calendar.getInstance();
        String timestamp = c.getTimeInMillis() + "";

        cv.put(KEY_TITLE, title);
        cv.put(KEY_NOTE, note);
        cv.put(KEY_TIMESTAMP, timestamp);
        cv.put(KEY_FLAG, flag);

        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public DatabaseHandler open(){
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        ourHelper.close();
    }

}
