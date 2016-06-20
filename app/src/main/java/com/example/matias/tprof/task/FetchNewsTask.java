package com.example.matias.tprof.task;

import android.content.ContentValues;
import android.content.Context;
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
 * Created by Mati on 6/20/2016.
 */
public class FetchNewsTask extends AsyncTask<String,Void,Void> {


    public final String LOG_TAG = FetchNewsTask.class.getSimpleName();

    private final Context mContext;

    public FetchNewsTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String tickerSymbol = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String testData = null;


        try {
            //"http://10.0.2.2:50914/api/sync";
            //"http://192.168.0.17:50914/api/sync";
            final String NEWS_URL =
                    "http://10.0.2.2:50914/api/news";
            //"http://192.168.0.17:50914/api/historicalquotes?";
            final String QUERY_PARAM = "tickerSymbol";

            Uri builtUri = Uri.parse(NEWS_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, tickerSymbol)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
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
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
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
            getNewsFromJson(testData, tickerSymbol);
        } catch (JSONException e) {
            Log.e("Some log", e.getMessage(), e);
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "Finished querying news data");
        return null;
    }

    public void getNewsFromJson(String syncData, String tickerSymbol) throws JSONException {
        final String URL = "URL";
        final String HEADLINE = "Headline";
        final String SOURCE = "Source";
        final String DATE = "Date";
        final String TAGSSTRING = "TagsAsString";

        JSONArray newsArray = new JSONArray(syncData);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(newsArray.length());

        for (int newsIndex = 0; newsIndex < newsArray.length(); newsIndex++) {

            JSONObject quoteObject = newsArray.getJSONObject(newsIndex);

            ContentValues newsQuoteValue = new ContentValues();
            newsQuoteValue.put(QuotesContract.NewsEntry.COLUMN_HEADLINE, quoteObject.getString(HEADLINE));
            newsQuoteValue.put(QuotesContract.NewsEntry.COLUMN_DATE, quoteObject.getString(DATE));
            newsQuoteValue.put(QuotesContract.NewsEntry.COLUMN_TAGS, quoteObject.getString(TAGSSTRING));
            newsQuoteValue.put(QuotesContract.NewsEntry.COLUMN_SOURCE, quoteObject.getString(SOURCE));
            newsQuoteValue.put(QuotesContract.NewsEntry.COLUMN_URL, quoteObject.getString(URL));

            cVVector.add(newsQuoteValue);
        }

        int insertedNews = 0;
        if (cVVector.size() > 0) {
            //Delete old news, we just keep the fresh ones
            mContext.getContentResolver().delete(QuotesContract.NewsEntry.CONTENT_URI,
                    QuotesContract.NewsEntry.COLUMN_TAGS + " LIKE ?",
                    new String[] { "%" + tickerSymbol + "%" });

            //Insert the new ones.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            insertedNews = mContext.getContentResolver().bulkInsert(QuotesContract.NewsEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "NEWS insertion for " + tickerSymbol + " done. " + insertedNews + " Inserted");

    }
}
