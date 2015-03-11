package com.vinhphuc.chotot.tasksexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Model.TaskModel;
import Networking.RequestTask;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;


public class MainActivity extends ActionBarActivity {

    private ListView mainListView;
    private TaskModel[] tasks;
    private ArrayAdapter<TaskModel> listAdapter;
    int clickCounter = 0;
    final Context context = this;
    public static String SHARED_PREFS_FILE = "SHARED_PREFS_FILE_";//temporary use this one until the API is ready
    public static final String BASE_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D1252431&format=json";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        // Find the ListView resource.
        mainListView = (ListView) findViewById(R.id.mainListView);

        // When item is tapped, toggle checked properties of CheckBox and Task.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                TaskModel task = listAdapter.getItem(position);
                task.toggleChecked();
                TaskViewHolder viewHolder = (TaskViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(task.isChecked());
            }
        });


        tasks = (TaskModel[]) getLastNonConfigurationInstance();
        if (tasks == null) {
            tasks = new TaskModel[]{
                    new TaskModel("Nguyen Phuc", true), new TaskModel("Nguyen Phuc 2", true), new TaskModel("Earth", false)

            };
        }
        ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();

       /*
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String jsonText = gson.toJson(taskList);
        editor.putString("key",jsonText);
        editor.commit();

        // get

        prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String gsonString = prefs.getString("key","");
    */


        taskList.addAll(Arrays.asList(tasks));

        String gsonString = toJson(taskList);

        taskList = (ArrayList<TaskModel>) fromJson(gsonString,
                new TypeToken<ArrayList<TaskModel>>() {
                }.getType());

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new TaskArrayAdapter(this, taskList);
        mainListView.setAdapter(listAdapter);

        new RequestTask().execute(BASE_URL);



    }


    public static String toJson(ArrayList jsonObject) {
        return new Gson().toJson(jsonObject);
    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }


    public void addItems(View v) {
        Log.i("Appname", "Clicked : " + String.valueOf(clickCounter++));
        //listAdapter.notifyDataSetChanged();

        // custom dialog

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add your task please");

        EditText editText = (EditText) dialogView.findViewById(R.id.textTaskName);

        dialogBuilder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
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


    /**
     * Holds child views for one row.
     */
    private static class TaskViewHolder {
        private CheckBox checkBox;
        private TextView textView;

        public TaskViewHolder() {
        }

        public TaskViewHolder(TextView textView, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.textView = textView;
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

    /**
     * Custom adapter for displaying an array of Task objects.
     */
    private static class TaskArrayAdapter extends ArrayAdapter<TaskModel> {

        private LayoutInflater inflater;

        public TaskArrayAdapter(Context context, List<TaskModel> taskList) {
            super(context, R.layout.row, R.id.textView1, taskList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Task to display
            TaskModel task = (TaskModel) this.getItem(position);
            // The child views in each row.
            CheckBox checkBox;
            TextView textView;

            // Create a new row view
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById(R.id.textView1);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag(new TaskViewHolder(textView, checkBox));

                // If CheckBox is toggled, update the task it is tagged with.
                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        TaskModel task = (TaskModel) cb.getTag();
                        task.setChecked(cb.isChecked());
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                TaskViewHolder viewHolder = (TaskViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
            }

            // Tag the CheckBox with the Task it is displaying, so that we can
            // access the task in onClick() when the CheckBox is toggled.
            checkBox.setTag(task);

            // Display task data
            checkBox.setChecked(task.isChecked());
            textView.setText(task.getName());

            return convertView;
        }

    }


    // networking

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isConnected()) {

            } else {
                //mLoadingText.setText(context.getText(R.string.loading));
                Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();

            }
        }
    };


    // TODO(benp) move to utils class
    private boolean isConnected() {
        ConnectivityManager conMngr = (ConnectivityManager) this
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = conMngr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = conMngr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (null != wifi && wifi.isConnected())
                || (null != mobile && mobile.isConnected());
    }


    Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();

    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("https://query.yahooapis.com/v1/public/")
            .setConverter(new GsonConverter(gson))
            .build();

    GitHubService service = restAdapter.create(GitHubService.class);


    public interface GitHubService {
        @GET("yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D1252431&format=json")
        void getFeed(Callback<Object> response);


    }





}
