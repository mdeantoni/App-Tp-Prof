package com.example.matias.tprof.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.matias.tprof.R;
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
 * Created by Mati on 12/13/2015.
 */
public class AppSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = AppSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public AppSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting Sync");
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
                 //   "http://10.0.2.2:50914/api/sync";
             "http://192.168.0.17:50914/api/sync";

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
                return;
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
                return;
            }
            testData = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
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
        Log.d(LOG_TAG, "Finished Sync");
        return;
    }

    public void getQuotesFromJson(String syncData) throws JSONException {
        JSONObject syncDataObject = new JSONObject(syncData);
        JSONObject dataObject = syncDataObject.getJSONObject("Data");
        JSONArray stocksArray = dataObject.getJSONArray("Stocks");
        JSONArray bondsArray = dataObject.getJSONArray("Bonds");
        this.getStockQuotesFromJson(stocksArray);
        this.getBondQuotesFromJson(bondsArray);
    }

    private void getStockQuotesFromJson(JSONArray quotesArray) throws JSONException {
        final String QUOTE_NAME = "FullName";
        final String SYMBOL_NAME = "TickerSymbol";

        Vector<ContentValues> cVVector = new Vector<ContentValues>(quotesArray.length());

        for (int i = 0; i < quotesArray.length(); i++) {
            String symbol;
            String fullName;

            JSONObject quote = quotesArray.getJSONObject(i);
            fullName = quote.getString(QUOTE_NAME);
            symbol = quote.getString(SYMBOL_NAME);

            ContentValues weatherValues = new ContentValues();

            weatherValues.put(QuotesContract.StockEntry.COLUMN_SYMBOL, symbol);
            weatherValues.put(QuotesContract.StockEntry.COLUMN_FULLNAME, fullName);
            cVVector.add(weatherValues);
        }

        Cursor stockCursor = getContext().getContentResolver().query(
                QuotesContract.StockEntry.CONTENT_URI,
                new String[]{QuotesContract.StockEntry._ID},
                null,
                null,
                null);

        if (!stockCursor.moveToFirst()) {
            int inserted = 0;
            // add to database if there werent any stocks!
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(QuotesContract.StockEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchStockDataTask Complete. " + inserted + " Inserted");
        }
    }

    private void getBondQuotesFromJson(JSONArray quotesArray) throws JSONException {
        final String QUOTE_NAME = "FullName";
        final String SYMBOL_NAME = "TickerSymbol";

        Vector<ContentValues> cVVector = new Vector<ContentValues>(quotesArray.length());

        for (int i = 0; i < quotesArray.length(); i++) {
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

        Cursor bondCursor = getContext().getContentResolver().query(
                QuotesContract.BondEntry.CONTENT_URI,
                new String[]{QuotesContract.BondEntry._ID},
                null,
                null,
                null);

        if (!bondCursor.moveToFirst()) {
            int inserted = 0;
            // add to database if there werent any stocks!
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(QuotesContract.BondEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchBondDataTask Complete. " + inserted + " Inserted");
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        // AppSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        //ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
