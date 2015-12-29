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
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import kj.notesapp.lite.datamodel.DatabaseHandler;

public class CustomListViewAdapter extends BaseAdapter
{

    LayoutInflater inflater;
    List<ListViewItem> items;
    Context ourContext;
    Typeface tF;

    public CustomListViewAdapter(Activity context, List<ListViewItem> items) {
        super();

        ourContext = context;
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Font path
        String fontPath = "fonts/RobotoSlab-Regular.ttf";

        // Loading Font Face
        tF = Typeface.createFromAsset(ourContext.getAssets(), fontPath);
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        final ListViewItem item = items.get(position);
        View vi=convertView;

        if(convertView==null)
            vi = inflater.inflate(R.layout.item_row, null);

        final LinearLayout llMain = (LinearLayout) vi.findViewById(R.id.llMain);



        TextView tvTitle = (TextView) vi.findViewById(R.id.textView4);
        TextView tvNote = (TextView) vi.findViewById(R.id.textView6);
        TextView tvDay = (TextView) vi.findViewById(R.id.textView7);
        TextView tvTime = (TextView) vi.findViewById(R.id.textView8);

        // Applying font
        if(StaticData.FONT.equals("1")) {
            tvTitle.setTypeface(tF);
            tvNote.setTypeface(tF);
            tvDay.setTypeface(tF);
            tvTime.setTypeface(tF);
        }

        tvTitle.setText(item.title);
        tvNote.setText(item.note);
        tvTime.setText(item.time);
        tvDay.setText(item.day);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!StaticData.ONE_CLICK) {
                    StaticData.ONE_CLICK = true;

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            //start your activity here
                            Intent myIntent = new Intent(ourContext, ViewNote.class);
                            myIntent.putExtra("id", "" + item.id); //Optional parameters
                            myIntent.putExtra("title", item.title);
                            myIntent.putExtra("note", item.note);
                            ourContext.startActivity(myIntent);

                        }

                    }, 100);

                }

            }

        });


        vi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                //view.setElevation(10);
                final Dialog dialog = new Dialog(ourContext);
                dialog.setContentView(R.layout.folder_long_click);
                dialog.setTitle("Options");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                final LinearLayout llRename = (LinearLayout) dialog
                        .findViewById(R.id.llEdit);
                llRename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();

                        /////////////////////////////////////////////////////////////////
                        Intent myIntent = new Intent(ourContext, EditNote.class);
                        myIntent.putExtra("id", "" + item.id); //Optional parameters
                        myIntent.putExtra("title", item.title);
                        myIntent.putExtra("note", item.note);
                        ourContext.startActivity(myIntent);


                        /////////////////////////////////////////////////////////////////

                    }
                });
/*
                llRename.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int action = motionEvent.getActionMasked();

                        switch (action) {

                            case MotionEvent.ACTION_DOWN:
                                llRename.setBackgroundColor(Color
                                        .parseColor("#EEEEEE"));
                                break;

                            case MotionEvent.ACTION_UP:
                                llRename.setBackgroundColor(Color
                                        .parseColor("#FFFFFF"));
                                break;

                        }
                        return false;
                    }
                });
*/

                final LinearLayout llDiscard = (LinearLayout) dialog
                        .findViewById(R.id.llDiscard);
                llDiscard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        dialog.dismiss();


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                ourContext);

                        // set title
                        //alertDialogBuilder.setTitle("Delete ?");
                        //alertDialogBuilder.setCancelable(true);


                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Delete this note?")
                                        //.setCancelable(false)
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        /////////////////////////////////////////////////
                                        DatabaseHandler dbh = new DatabaseHandler(ourContext);
                                        dbh.open();
                                        dbh.delete(item.id);
                                        dbh.close();
                                        items.remove(position);
                                        notifyDataSetChanged();
                                        /////////////////////////////////////////////////
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        dialog.dismiss();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    }
                });
/*
                llDiscard.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int action = motionEvent.getActionMasked();

                        switch (action) {

                            case MotionEvent.ACTION_DOWN:
                                llDiscard.setBackgroundColor(Color
                                        .parseColor("#EEEEEE"));
                                break;

                            case MotionEvent.ACTION_UP:
                                llDiscard.setBackgroundColor(Color
                                        .parseColor("#FFFFFF"));
                                break;

                        }
                        return false;
                    }
                });
*/
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(new Dialog.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //llMain.setElevation(10);
                    }
                });
                dialog.show();


/*
                llRename.setBackgroundColor(Color
                        .parseColor("#FFFFFF"));
                llDiscard.setBackgroundColor(Color
                        .parseColor("#FFFFFF"));*/

                return true;
            }
        });


        vi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        StaticData.CX = (int) motionEvent.getX();
                        StaticData.CY = (int) motionEvent.getY();
                        break;
                }
                return false;
            }
        });

        return vi;
    }
}
