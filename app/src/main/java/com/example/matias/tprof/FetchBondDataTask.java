package com.example.matias.tprof;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.matias.tprof.data.QuotesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Mati on 12/8/2015.
 */
public class FetchBondDataTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    private final String LOG_TAG = FetchBondDataTask.class.getSimpleName();

    public FetchBondDataTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
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
                    "http://10.0.2.2:50914/api/bondquotes";
            // "http://192.168.0.17:50914/api/bondquotes";

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

        try { //OJO CON ESTO QUE EN EL EJEMPLO ESTA AL REVES EL ORDEN CON EL CIERRE DE CONNECTION
            getQuotesFromJson(testData);
        } catch (JSONException e) {
            Log.e("Some log", e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private void getQuotesFromJson(String quotesJson) throws  JSONException{
        final String QUOTE_NAME = "FullName";
        final String SYMBOL_NAME = "TickerSymbol";

        JSONArray quotesArray = new JSONArray(quotesJson);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(quotesArray.length());

        for(int i = 0; i < quotesArray.length(); i++) {
            String symbol;
            String fullName;

            JSONObject quote = quotesArray.getJSONObject(i);
            fullName = quote.getString(QUOTE_NAME);
            symbol = quote.getString(SYMBOL_NAME);

            ContentValues quoteValues = new ContentValues();

            quoteValues.put(QuotesContract.BondEntry.COLUMN_SYMBOL, symbol);
            quoteValues.put(QuotesContract.BondEntry.COLUMN_FULLNAME, fullName);
            cVVector.add(quoteValues);
        }

        Cursor bondCursor = mContext.getContentResolver().query(
                QuotesContract.BondEntry.CONTENT_URI,
                new String[]{QuotesContract.BondEntry._ID},
                null,
                null,
                null);

        if (!bondCursor.moveToFirst()) {
            int inserted = 0;
            // add to database if there werent any stocks!
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(QuotesContract.BondEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchBondDataTask Complete. " + inserted + " Inserted");
        }
    }
}