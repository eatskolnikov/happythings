package com.moosader.happythings.com.moosader.happythings.services;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by eatskolnikov on 4/8/15.
 */
public class FetchHappyThingsTask extends AsyncTask<Void, Void, String>
{
    private ArrayAdapter<String> mHappyThingsAdapter;
    public FetchHappyThingsTask(ArrayAdapter<String> adapter){
        this.mHappyThingsAdapter = adapter;
    }
    @Override
    protected String doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;
        try {
            URL url = new URL("http://happythings.moosader.com/content/items.json");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) { return null; }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return jsonStr;
    }
    protected void onPostExecute(String result) {
        try {
            List<String> happyThings = new ArrayList<String>();
            JSONObject happyThingsJsonObject = new JSONObject(result);

            Iterator<?> keys = happyThingsJsonObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( happyThingsJsonObject.get(key) instanceof JSONObject ) {
                    JSONObject happyThing = happyThingsJsonObject.getJSONObject(key);
                    happyThings.add(happyThing.getString("thing") +"\n\r- "+happyThing.getString("user") );
                }
            }
            mHappyThingsAdapter.clear();
            mHappyThingsAdapter.addAll(happyThings);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}