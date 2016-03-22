package com.example.matias.tprof.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
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
                    "http://10.0.2.2:50914/api/sync";
             //"http://192.168.0.17:50914/api/sync";

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
        final String INTRADAY_PRICES = "IntradayPrices";
        final String INTRADAY_PRICE_DATE = "DateTime";
        final String INTRADAY_PRICE_PRICE = "Price";
        final String LAST_QUOTE = "LastQuote";
        final String LAST_TRADED_PRICE = "LastTradedPrice";
        final String CURRENCY = "Currency";
        final String LAST_CHANGE_IN_PRICE_PERCENTAGE = "LastChangeInPricePercentage";
        final String LAST_CHANGE_IN_PRICE = "LastChangeInPrice";
        final String LAST_TRADE_DATE = "LastTradeDate";


        Vector<ContentValues> cVVector = new Vector<ContentValues>(quotesArray.length());

        for (int i = 0; i < quotesArray.length(); i++) {
            String symbol;
            String fullName;
            long stockId;
            JSONObject lastQuote;
            JSONArray intradayPrices;

            JSONObject quote = quotesArray.getJSONObject(i);
            intradayPrices = quote.getJSONArray(INTRADAY_PRICES);
            fullName = quote.getString(QUOTE_NAME);
            symbol = quote.getString(SYMBOL_NAME);
            lastQuote = quote.getJSONObject(LAST_QUOTE);

            Cursor stockCursor = getContext().getContentResolver().query(
                    QuotesContract.StockEntry.CONTENT_URI,
                    new String[]{QuotesContract.StockEntry._ID},
                    QuotesContract.StockEntry.COLUMN_SYMBOL + " = ?",
                    new String[]{ symbol },
                    null);

            if (stockCursor.moveToFirst()) {
                int stockIdIndex = stockCursor.getColumnIndex(QuotesContract.StockEntry._ID);
                stockId = stockCursor.getInt(stockIdIndex);
            }
            else{
                ContentValues stockValues = new ContentValues();
                stockValues.put(QuotesContract.StockEntry.COLUMN_SYMBOL, symbol);
                stockValues.put(QuotesContract.StockEntry.COLUMN_FULLNAME, fullName);

                Uri insertedUri = getContext().getContentResolver().insert(
                        QuotesContract.StockEntry.CONTENT_URI,
                        stockValues
                );

                stockId = ContentUris.parseId(insertedUri);
            }

            ContentValues stockQuoteValues = new ContentValues();
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_STOCK_ID, stockId);
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_CURRENCY, lastQuote.getString(CURRENCY));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE, lastQuote.getDouble(LAST_CHANGE_IN_PRICE));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE, lastQuote.getDouble(LAST_CHANGE_IN_PRICE_PERCENTAGE));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_DATE, lastQuote.getString(LAST_TRADE_DATE));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_PRICE, lastQuote.getDouble(LAST_TRADED_PRICE));

            cVVector.add(stockQuoteValues);

            Vector<ContentValues> intPricevector = new Vector<ContentValues>(intradayPrices.length());
            for (int priceIndex = 0; priceIndex < intradayPrices.length(); priceIndex++) {
                String time;
                Double price;

                JSONObject priceObject = intradayPrices.getJSONObject(priceIndex);
                time = priceObject.getString(INTRADAY_PRICE_DATE);
                price = priceObject.getDouble(INTRADAY_PRICE_PRICE);

                ContentValues intradayPriceValue = new ContentValues();
                intradayPriceValue.put(QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID, stockId);
                intradayPriceValue.put(QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_PRICE, price);
                intradayPriceValue.put(QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_TIME, time);

                intPricevector.add(intradayPriceValue);
            }

            int insertedIntraday = 0;
            if (intPricevector.size() > 0) {
                //Delete old intraday, we just keep the fresh ones
                getContext().getContentResolver().delete(QuotesContract.StockIntradayPriceEntry.CONTENT_URI,
                        QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID + " = ?",
                        new String[] { Long.toString(stockId) });

                //Insert the new ones.
                ContentValues[] cvArray = new ContentValues[intPricevector.size()];
                intPricevector.toArray(cvArray);
                insertedIntraday = getContext().getContentResolver().bulkInsert(QuotesContract.StockIntradayPriceEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Intraday prices insertion for stock " + stockId  + " done. " +insertedIntraday + " Inserted");

        }

            int inserted = 0;
            if (cVVector.size() > 0) {
                //Delete old quotes, we just keep the fresh ones
                getContext().getContentResolver().delete(QuotesContract.StockQuotesEntry.CONTENT_URI,
                        null,
                        null);

                //Insert the new ones.
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(QuotesContract.StockQuotesEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchStockDataTask Complete. " + inserted + " Inserted");

    }

    private void getBondQuotesFromJson(JSONArray quotesArray) throws JSONException {
        final String QUOTE_NAME = "FullName";
        final String SYMBOL_NAME = "TickerSymbol";

        final String LAST_QUOTE = "LastQuote";
        final String LAST_TRADED_PRICE = "LastTradedPrice";
        final String CURRENCY = "Currency";
        final String LAST_CHANGE_IN_PRICE_PERCENTAGE = "LastChangeInPricePercentage";
        final String LAST_CHANGE_IN_PRICE = "LastChangeInPrice";
        final String LAST_TRADE_DATE = "LastTradeDate";
        final String INTRADAY_PRICES = "IntradayPrices";
        final String INTRADAY_PRICE_DATE = "DateTime";
        final String INTRADAY_PRICE_PRICE = "Price";

        Vector<ContentValues> cVVector = new Vector<ContentValues>(quotesArray.length());

        for (int i = 0; i < quotesArray.length(); i++) {
            String symbol;
            String fullName;
            long bondId;
            JSONObject lastQuote;
            JSONArray intradayPrices;

            JSONObject quote = quotesArray.getJSONObject(i);
            intradayPrices = quote.getJSONArray(INTRADAY_PRICES);
            fullName = quote.getString(QUOTE_NAME);
            symbol = quote.getString(SYMBOL_NAME);
            lastQuote = quote.getJSONObject(LAST_QUOTE);

            Cursor bondCursor = getContext().getContentResolver().query(
                    QuotesContract.BondEntry.CONTENT_URI,
                    new String[]{QuotesContract.BondEntry._ID},
                    QuotesContract.BondEntry.COLUMN_SYMBOL + " = ?",
                    new String[]{ symbol },
                    null);

            if (bondCursor.moveToFirst()) {
                int bondIdIndex = bondCursor.getColumnIndex(QuotesContract.BondEntry._ID);
                bondId = bondCursor.getInt(bondIdIndex);
            }
            else{
                ContentValues bondValues = new ContentValues();
                bondValues.put(QuotesContract.BondEntry.COLUMN_SYMBOL, symbol);
                bondValues.put(QuotesContract.BondEntry.COLUMN_FULLNAME, fullName);

                Uri insertedUri = getContext().getContentResolver().insert(
                        QuotesContract.BondEntry.CONTENT_URI,
                        bondValues
                );

                bondId = ContentUris.parseId(insertedUri);
            }

            ContentValues bondQuoteValues = new ContentValues();
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_BOND_ID, bondId);
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_CURRENCY, lastQuote.getString(CURRENCY));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE, lastQuote.getDouble(LAST_CHANGE_IN_PRICE));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE, lastQuote.getDouble(LAST_CHANGE_IN_PRICE_PERCENTAGE));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_DATE, lastQuote.getString(LAST_TRADE_DATE));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_PRICE, lastQuote.getDouble(LAST_TRADED_PRICE));

            cVVector.add(bondQuoteValues);

            Vector<ContentValues> intPricevector = new Vector<ContentValues>(intradayPrices.length());
            for (int priceIndex = 0; priceIndex < intradayPrices.length(); priceIndex++) {
                String time;
                Double price;

                JSONObject priceObject = intradayPrices.getJSONObject(priceIndex);
                time = priceObject.getString(INTRADAY_PRICE_DATE);
                price = priceObject.getDouble(INTRADAY_PRICE_PRICE);

                ContentValues intradayPriceValue = new ContentValues();
                intradayPriceValue.put(QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID, bondId);
                intradayPriceValue.put(QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_PRICE, price);
                intradayPriceValue.put(QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_TIME, time);

                intPricevector.add(intradayPriceValue);
            }

            int insertedIntraday = 0;
            if (intPricevector.size() > 0) {
                //Delete old intraday, we just keep the fresh ones
                getContext().getContentResolver().delete(QuotesContract.BondIntradayPriceEntry.CONTENT_URI,
                        QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID + " = ?",
                        new String[] { Long.toString(bondId) });

                //Insert the new ones.
                ContentValues[] cvArray = new ContentValues[intPricevector.size()];
                intPricevector.toArray(cvArray);
                insertedIntraday = getContext().getContentResolver().bulkInsert(QuotesContract.BondIntradayPriceEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Intraday prices insertion for bond " + bondId  + " done. " +insertedIntraday + " Inserted");
        }

        int inserted = 0;
        if (cVVector.size() > 0) {
            //Delete old quotes, we just keep the fresh ones
            getContext().getContentResolver().delete(QuotesContract.BondQuotesEntry.CONTENT_URI,
                    null,
                    null);

            //Insert the new ones.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getContext().getContentResolver().bulkInsert(QuotesContract.BondQuotesEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchBondDataTask Complete. " + inserted + " Inserted");
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
