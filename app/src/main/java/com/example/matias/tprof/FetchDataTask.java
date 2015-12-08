package com.example.matias.tprof;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mati on 11/28/2015.
 */
public class FetchDataTask extends AsyncTask<String, Void, String[]> {

    private ArrayAdapter<String> mQuotesAdapter;

    private final String LOG_TAG = FetchDataTask.class.getSimpleName();

    public FetchDataTask(ArrayAdapter<String> adapter) {
        this.mQuotesAdapter = adapter;
    }

    @Override
    protected String[] doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String testData = null;


        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BASE_URL =
                    "http://10.0.2.2:50914/api/quotes";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            int code = urlConnection.getResponseCode();
            String resp = urlConnection.getResponseMessage();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            testData = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getQuotesFromJson(testData);
        } catch (JSONException e) {
            Log.e("Some log", e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private String[] getQuotesFromJson(String quotesJson) throws  JSONException{
        final String QUOTE_NAME = "FullName";

        JSONArray quotesArray = new JSONArray(quotesJson);
        String[] resultStrs = new String[quotesArray.length()];

        for(int i = 0; i < quotesArray.length(); i++) {
            JSONObject quote = quotesArray.getJSONObject(i);
            resultStrs[i] = quote.getString(QUOTE_NAME);
        }

        return resultStrs;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            mQuotesAdapter.clear();
            for(String quoteStr : result) {
                mQuotesAdapter.add(quoteStr);
            }
            // New data is back from the server.  Hooray!
        }
    }
}
