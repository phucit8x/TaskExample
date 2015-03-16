package com.vinhphuc.chotot.tasksexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.TaskModel;
import Model.Weather;


public class MainActivity extends ActionBarActivity  {

    private ListView mainListView;
    private TaskModel[] tasks;
    private ArrayAdapter<TaskModel> listAdapter;
    int clickCounter = 0;
    final Context context = this;
    public static String SHARED_PREFS_FILE = "SHARED_PREFS_FILE_";//temporary use this one until the API is ready
    public static final String BASE_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D1252431&format=json";
    private ArrayList<TaskModel> taskList;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

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

        taskList = new ArrayList<TaskModel>();

        prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit();
        String value = prefs.getString(SHARED_PREFS_FILE, "");
        tasks = (TaskModel[]) getLastNonConfigurationInstance();

        if (value.equals("") == true) {

            if (tasks == null) {
                tasks = new TaskModel[]{
                        new TaskModel("Nguyen Phuc", true), new TaskModel("Nguyen Phuc 2", true), new TaskModel("Earth", false)

                };
            }


            taskList.addAll(Arrays.asList(tasks));

            editor.putString(SHARED_PREFS_FILE, toJson(taskList));
            editor.commit();
        } else {

            // Reading from SharedPreferences
            String jsonText = prefs.getString(SHARED_PREFS_FILE, "");
            // Log.i("========222==========", jsonText);

            taskList = (ArrayList<TaskModel>) fromJson(jsonText,
                    new TypeToken<ArrayList<TaskModel>>() {
                    }.getType());

        }

        //taskList.addAll(Arrays.asList(tasks));
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
        final View dialogView = inflater.inflate(R.layout.new_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add your task please");

        final EditText editText = (EditText) dialogView.findViewById(R.id.textTaskName);
        final CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.checkBox);

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
                Editable taskName = editText.getText();

                //Toast.makeText(getApplicationContext(), taskName.toString(),
                //  Toast.LENGTH_LONG).show();
                //Log.i("=---------------", taskName.toString());

                boolean chek = checkBox.isChecked() ? true : false;


                addTask(taskName.toString(), chek);


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

    public void addTask(String taskName, boolean check) {

        taskList.add(new TaskModel(taskName, check));
        listAdapter = new TaskArrayAdapter(this, taskList);
        mainListView.setAdapter(listAdapter);
        // save to db
        editor.putString(SHARED_PREFS_FILE, toJson(taskList));
        editor.commit();
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

                       // Log.i(">>>>>>>>>>>>>>>>>", cb.getTag().toString());

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


//    public void editTask(TaskModel task,boolean check) {
//        for (TaskModel taak : taskList);
//        {
//            if(taak.getName()==task.getName())
//            {
//
//                taak.setChecked(check);
//            }
//
//        }
//
//    }

    public class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();

                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
                try {
                    JSONObject json = new JSONObject(result);

                    JSONArray array = json.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast");

                    Log.d("Forecast", array.toString());

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Weather>>(){}.getType();
                    List<Weather> myModelList = gson.fromJson(array.toString(), listType);



                } catch (JSONException e) {
                    e.printStackTrace();
                }



        }


            //Do anything with response..
        }



}
