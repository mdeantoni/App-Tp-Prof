package com.example.matias.tprof.task;

import android.content.ContentUris;
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
 * Created by Mati on 5/5/2016.
 */
public class FetchHistoricalQuotesTask extends AsyncTask<String,Void,Void> {

    public final String LOG_TAG = FetchHistoricalQuotesTask.class.getSimpleName();

    private final Context mContext;

    public FetchHistoricalQuotesTask(Context context) {
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
            final String HISTORICAL_QUOTES_URL =
                    "http://10.0.2.2:50914/api/historicalquotes?";
            final String QUERY_PARAM = "tickerSymbol";

            Uri builtUri = Uri.parse(HISTORICAL_QUOTES_URL).buildUpon()
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
            getHistoricalQuotesFromJson(testData, tickerSymbol);
        } catch (JSONException e) {
            Log.e("Some log", e.getMessage(), e);
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "Finished querying historical data");
        return null;
    }

    public void getHistoricalQuotesFromJson(String syncData, String tickerSymbol) throws JSONException {
        JSONObject syncDataObject = new JSONObject(syncData);
        JSONObject dataObject = syncDataObject.getJSONObject("Data");
        JSONArray historicalQuotesArray = dataObject.getJSONArray("HistoricalQuotes");
        this.getHistoricalQuotesFromJson(historicalQuotesArray, tickerSymbol);
    }

    private void getHistoricalQuotesFromJson(JSONArray quotesArray, String tickerSymbol) throws JSONException {

        final String HISTORICAL_QUOTE_OPEN = "Open";
        final String HISTORICAL_QUOTE_SYMBOL = "Symbol";
        final String HISTORICAL_QUOTE_DATE = "Date";
        final String HISTORICAL_QUOTE_CLOSE_PRICE = "Close";
        final String HISTORICAL_QUOTE_HIGH_PRICE = "High";
        final String HISTORICAL_QUOTE_LOW_PRICE = "Low";
        final String HISTORICAL_QUOTE_VOLUME = "Volume";
        final String HISTORICAL_QUOTE_TICKER_SYMBOL = "TickerSymbol";

        Vector<ContentValues> cVVector = new Vector<ContentValues>(quotesArray.length());

        for (int historicalQuoteIndex = 0; historicalQuoteIndex < quotesArray.length(); historicalQuoteIndex++) {

            JSONObject quoteObject = quotesArray.getJSONObject(historicalQuoteIndex);

            ContentValues historicalQuoteValue = new ContentValues();
            historicalQuoteValue.put(QuotesContract.HistoricalQuoteEntry.COLUMN_TICKER_SYMBOL, quoteObject.getString(HISTORICAL_QUOTE_TICKER_SYMBOL));
            historicalQuoteValue.put(QuotesContract.HistoricalQuoteEntry.COLUMN_DATE, quoteObject.getString(HISTORICAL_QUOTE_DATE));
            historicalQuoteValue.put(QuotesContract.HistoricalQuoteEntry.COLUMN_CLOSE_PRICE, quoteObject.getDouble(HISTORICAL_QUOTE_CLOSE_PRICE));
            historicalQuoteValue.put(QuotesContract.HistoricalQuoteEntry.COLUMN_VOLUME, quoteObject.getInt(HISTORICAL_QUOTE_VOLUME));

            cVVector.add(historicalQuoteValue);
        }

        int insertedIntraday = 0;
        if (cVVector.size() > 0) {
            //Delete old intraday, we just keep the fresh ones
            mContext.getContentResolver().delete(QuotesContract.HistoricalQuoteEntry.CONTENT_URI,
                    QuotesContract.HistoricalQuoteEntry.COLUMN_TICKER_SYMBOL + " = ?",
                    new String[] { tickerSymbol });

            //Insert the new ones.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            insertedIntraday = mContext.getContentResolver().bulkInsert(QuotesContract.HistoricalQuoteEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "Historical prices insertion for " + tickerSymbol  + " done. " +insertedIntraday + " Inserted");
    }
}
