package com.vinhphuc.chotot.tasksexample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.TaskModel;


public class MainActivity extends ActionBarActivity {

    private ListView mainListView ;
    private TaskModel[] tasks ;
    private ArrayAdapter<TaskModel> listAdapter ;
    int clickCounter = 0;
    final Context context = this;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );

        // When item is tapped, toggle checked properties of CheckBox and Planet.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View item,
                                     int position, long id) {
                TaskModel planet = listAdapter.getItem( position );
                planet.toggleChecked();
                PlanetViewHolder viewHolder = (PlanetViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked( planet.isChecked() );
            }
        });


        tasks = (TaskModel[]) getLastNonConfigurationInstance() ;
        if ( tasks == null ) {
            tasks = new TaskModel[] {
                    new TaskModel("Nguyen Phuc"), new TaskModel("Nguyen Phuc 2"), new TaskModel("Earth")

            };
        }
        ArrayList<TaskModel> planetList = new ArrayList<TaskModel>();
        planetList.addAll( Arrays.asList(tasks) );

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new PlanetArrayAdapter(this, planetList);
        mainListView.setAdapter( listAdapter );





    }

    public void addItems(View v) {
        Log.i("Appname","Clicked : " + String.valueOf(clickCounter++));
        //listAdapter.notifyDataSetChanged();

        // custom dialog

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add your task please");

        EditText editText = (EditText) dialogView.findViewById(R.id.textTaskName);

        dialogBuilder.setNegativeButton("Hủy",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("TaskExample", "Vừa nhấn nút hủy");
            }
        });

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("TaskExample", "Vừa nhấn nút OK");
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

//        final Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.new_dialog);
//        dialog.setTitle("Add new Item");
//
//        // set the custom dialog components - text, image and button
//        TextView text = (TextView) dialog.findViewById(R.id.textTaskName);
////        text.setText("Android custom dialog example!");
//
//
//        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
//        // if button is clicked, close the custom dialog
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
    }


    /** Holds child views for one row. */
    private static class PlanetViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public PlanetViewHolder() {}
        public PlanetViewHolder( TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /** Custom adapter for displaying an array of Planet objects. */
    private static class PlanetArrayAdapter extends ArrayAdapter<TaskModel> {

        private LayoutInflater inflater;

        public PlanetArrayAdapter( Context context, List<TaskModel> planetList ) {
            super( context, R.layout.row, R.id.textView1, planetList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Planet to display
            TaskModel planet = (TaskModel) this.getItem( position );

            // The child views in each row.
            CheckBox checkBox ;
            TextView textView ;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.row, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById( R.id.textView1);
                checkBox = (CheckBox) convertView.findViewById( R.id.checkBox1 );

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new PlanetViewHolder(textView,checkBox) );

                // If CheckBox is toggled, update the planet it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        TaskModel planet = (TaskModel) cb.getTag();
                        planet.setChecked( cb.isChecked() );
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the Planet it is displaying, so that we can
            // access the planet in onClick() when the CheckBox is toggled.
            checkBox.setTag( planet );

            // Display planet data
            checkBox.setChecked( planet.isChecked() );
            textView.setText( planet.getName() );

            return convertView;
        }

    }

}
