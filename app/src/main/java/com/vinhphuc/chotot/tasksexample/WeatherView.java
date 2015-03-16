package com.vinhphuc.chotot.tasksexample;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Date;
import java.util.List;

import Model.Weather;


public class WeatherView extends ActionBarActivity {
    public static final String BASE_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D1252431&format=json";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_view);
        new RequestTask().execute(BASE_URL);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

   public void closeClick(View v)
    {
        finish();

    }

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
                List<Weather>  myModelList = gson.fromJson(array.toString(), listType);
                int itemCount = myModelList.size();

                if (itemCount>0)
                {
                    Weather w=myModelList.get(0);
                    setContentView(R.layout.activity_weather_view);
                    TextView textCode = (TextView) findViewById(R.id.txtCode);
                    TextView textDate = (TextView) findViewById(R.id.txtDate);
                    TextView textDay = (TextView) findViewById(R.id.txtDay);
                    TextView textHight = (TextView) findViewById(R.id.txtHigh);
                    TextView textLow = (TextView) findViewById(R.id.txtLow);
                    TextView textText = (TextView) findViewById(R.id.txtText);



                    textCode.setText("Location Code: "+new Integer(w.getCode()).toString());
                    textDate.setText("Date: "+ w.getDate().toString());
                    textDay.setText("Day :"+w.getDay().toString());
                    textHight.setText("Hight : "+new Integer(w.getHight()).toString());
                    textLow.setText("Low : "+new Integer(w.getLow()).toString());
                    textText.setText(w.getText().toString());


                }




            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


        //Do anything with response..
    }


}
