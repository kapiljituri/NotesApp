package kj.notesapp.lite;

/**
 * Created by Kapil on 9/28/2014.
 */
public class ListViewItem{
    public String title;
    public String note;
    public int id;
    public String time;
    public String day;

    public ListViewItem(String title, String note, String time, String day, int id){
        this.title = title;
        this.note = note;
        this.time = time;
        this.day = day;
        this.id = id;
    }

    public ListViewItem(ListViewItem item) {
        this.title = item.title;
        this.note = item.note;
        this.time = item.time;
        this.day = item.day;
        this.id = item.id;
    }
}