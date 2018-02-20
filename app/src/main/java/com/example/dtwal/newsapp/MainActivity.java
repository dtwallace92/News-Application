package com.example.dtwal.newsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Source> sourceList;
    ProgressDialog pd;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourceList = new ArrayList<Source>();
        scrollView = findViewById(R.id.scrollView2);

        for (int i = 0; i < sourceList.size(); i++) {
            Source currentSource = sourceList.get(i);
            //scrollView.addView(currentSource.getName());
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Looking for Sources");
        pd.show();

        // Check for Internet Connection
        if (isConnected()) {
            new GetDataAsync().execute("https://newsapi.org/v1/sources");

        } else {
            // Notify user if no connection
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

        public void sendData(ArrayList<Source> source) {

        sourceList = source;
        pd.dismiss();

    }


    // Internet Connection Check method
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private class GetDataAsync extends AsyncTask<String, Void, ArrayList<Source>>{
        @Override
        protected ArrayList<Source> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<Source> result = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Begin Parsing JSON
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                   String json = IOUtils.toString(connection.getInputStream(), "UTF-8");

                   JSONObject root = new JSONObject(json);
                   JSONArray sources = root.getJSONArray("sources");

                   // Iterate through each news source and add them to a list
                   for (int i = 0; i < sources.length(); i++) {
                       JSONObject sourceJson = sources.getJSONObject(i);
                       Source source = new Source();
                       source.setId(sourceJson.getString("id"));
                       source.setName(sourceJson.getString("name"));

                       result.add(source);

                    }


                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Source> result) {
            if (result != null) {
                Log.d("demo", result.toString());
            } else {
                Log.d("demo", "null result");
            }

            sendData(result);
        }
    }
} //End MainActivity




