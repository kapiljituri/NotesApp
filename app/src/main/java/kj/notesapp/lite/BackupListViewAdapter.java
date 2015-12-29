package kj.notesapp.lite;

/**
 * Created by Kapil on 9/23/2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import kj.notesapp.lite.datamodel.DatabaseHandler;

public class BackupListViewAdapter extends BaseAdapter
{

    LayoutInflater inflater;
    List<BackupListViewItem> items;
    Context ourContext;

    public BackupListViewAdapter(Activity context, List<BackupListViewItem> items) {
        super();

        ourContext = context;
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final BackupListViewItem item = items.get(position);
        View vi=convertView;

        if(convertView==null)
            vi = inflater.inflate(R.layout.item_row_backup, null);

       // final LinearLayout llBackupMain = (LinearLayout) vi.findViewById(R.id.llBackupMain);

        TextView tvTitle = (TextView) vi.findViewById(R.id.tvBackupTitle);

        tvTitle.setText(item.title);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ourContext, "This is Restore", Toast.LENGTH_SHORT).show();

            }
        });




        return vi;
    }
}
