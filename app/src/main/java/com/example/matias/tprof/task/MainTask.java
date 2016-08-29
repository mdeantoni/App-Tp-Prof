package com.example.matias.tprof.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
 * Created by Mati on 8/7/2016.
 */
public class MainTask extends AsyncTask<Void, Void, Void>  {

    private final OnDataLoadedActivity mDataLoadedActivity;

    public interface OnDataLoadedActivity {
        public void dataSyncFinished();
    }


    public final String LOG_TAG = MainTask.class.getSimpleName();

    private final Context mContext;

    public MainTask(Context context, MainTask.OnDataLoadedActivity dataLoadedActivity) {
        mContext = context;
        mDataLoadedActivity = dataLoadedActivity;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mDataLoadedActivity.dataSyncFinished();
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(LOG_TAG, "Starting Task");
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String testData = null;


        try {

            final String BASE_URL = mContext.getString(R.string.connection_string) + "sync";


            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
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
            getQuotesFromJson(testData);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error running task ", e);
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
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }

        Log.d(LOG_TAG, "Finished execution of task.");
        return null;
    }

    public void getQuotesFromJson(String syncData) throws JSONException {
        JSONObject syncDataObject = new JSONObject(syncData);
        JSONObject dataObject = syncDataObject.getJSONObject("Data");
        JSONObject quotesObject = dataObject.getJSONObject("Quotes");
        JSONArray newsArray = dataObject.getJSONArray("News");
        JSONArray stocksArray = quotesObject.getJSONArray("Stocks");
        JSONArray bondsArray = quotesObject.getJSONArray("Bonds");
        this.getNewsFromJson(newsArray);
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
        final String DAYS_LOW ="DaysLow";
        final String DAYS_HIGH = "DaysHigh";
        final String YEAR_LOW= "YearLow";
        final String YEAR_HIGH = "YearHigh";
        final String PREVIOUS_CLOSE = "PreviousClose";
        final String OPEN = "Open";
        final String VOLUME = "Volume";
        final String AVG_VOLUME = "AverageDailyVolume";
        final String MKT_CAP= "MarketCapitalization";
        final String PRICE_SALES = "PriceOverSales";
        final String PRICE_BOOK = "PriceOverBookValue";
        final String PRICE_EARNINGS=  "PriceEarningsRatio";


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

            Cursor stockCursor = mContext.getContentResolver().query(
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

                Uri insertedUri = mContext.getContentResolver().insert(
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
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_DAYS_HIGH, lastQuote.getDouble(DAYS_HIGH));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_DAYS_LOW, lastQuote.getDouble(DAYS_LOW));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_YEAR_HIGH, lastQuote.getDouble(YEAR_HIGH));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_YEAR_LOW, lastQuote.getDouble(YEAR_LOW));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_OPEN, lastQuote.getDouble(OPEN));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_PREVIOUS_CLOSE, lastQuote.getDouble(PREVIOUS_CLOSE));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_VOLUME, lastQuote.getInt(VOLUME));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_AVG_VOLUME, lastQuote.getString(AVG_VOLUME));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_MKT_CAP, lastQuote.getString(MKT_CAP));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_PRICE_EARNINGS, lastQuote.getString(PRICE_EARNINGS));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_PRICE_SALES, lastQuote.getString(PRICE_SALES));
            stockQuoteValues.put(QuotesContract.StockQuotesEntry.COLUMN_PRICE_BOOK, lastQuote.getString(PRICE_BOOK));
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
                mContext.getContentResolver().delete(QuotesContract.StockIntradayPriceEntry.CONTENT_URI,
                        QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID + " = ?",
                        new String[] { Long.toString(stockId) });

                //Insert the new ones.
                ContentValues[] cvArray = new ContentValues[intPricevector.size()];
                intPricevector.toArray(cvArray);
                insertedIntraday = mContext.getContentResolver().bulkInsert(QuotesContract.StockIntradayPriceEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Intraday prices insertion for stock " + stockId  + " done. " +insertedIntraday + " Inserted");

        }

        int inserted = 0;
        if (cVVector.size() > 0) {
            //Delete old quotes, we just keep the fresh ones
            mContext.getContentResolver().delete(QuotesContract.StockQuotesEntry.CONTENT_URI,
                    null,
                    null);

            //Insert the new ones.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(QuotesContract.StockQuotesEntry.CONTENT_URI, cvArray);
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
        final String DAYS_LOW ="DaysLow";
        final String DAYS_HIGH = "DaysHigh";
        final String YEAR_LOW= "YearLow";
        final String YEAR_HIGH = "YearHigh";
        final String PREVIOUS_CLOSE = "PreviousClose";
        final String OPEN = "Open";
        final String VOLUME = "Volume";
        final String IRR = "IRR";
        final String PARITY = "Parity";
        final String AVG_VOLUME = "AverageDailyVolume";

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

            Cursor bondCursor = mContext.getContentResolver().query(
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

                Uri insertedUri = mContext.getContentResolver().insert(
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
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_DAYS_HIGH, lastQuote.getDouble(DAYS_HIGH));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_DAYS_LOW, lastQuote.getDouble(DAYS_LOW));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_YEAR_HIGH, lastQuote.getDouble(YEAR_HIGH));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_YEAR_LOW, lastQuote.getDouble(YEAR_LOW));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_OPEN, lastQuote.getDouble(OPEN));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_PREVIOUS_CLOSE, lastQuote.getDouble(PREVIOUS_CLOSE));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_VOLUME, lastQuote.getInt(VOLUME));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_AVG_VOLUME, lastQuote.getString(AVG_VOLUME));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_IIR, lastQuote.getString(IRR));
            bondQuoteValues.put(QuotesContract.BondQuotesEntry.COLUMN_PARITY, lastQuote.getString(PARITY));
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
                mContext.getContentResolver().delete(QuotesContract.BondIntradayPriceEntry.CONTENT_URI,
                        QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID + " = ?",
                        new String[] { Long.toString(bondId) });

                //Insert the new ones.
                ContentValues[] cvArray = new ContentValues[intPricevector.size()];
                intPricevector.toArray(cvArray);
                insertedIntraday = mContext.getContentResolver().bulkInsert(QuotesContract.BondIntradayPriceEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Intraday prices insertion for bond " + bondId  + " done. " +insertedIntraday + " Inserted");
        }

        int inserted = 0;
        if (cVVector.size() > 0) {
            //Delete old quotes, we just keep the fresh ones
            mContext.getContentResolver().delete(QuotesContract.BondQuotesEntry.CONTENT_URI,
                    null,
                    null);

            //Insert the new ones.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(QuotesContract.BondQuotesEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchBondDataTask Complete. " + inserted + " Inserted");
    }


    public void getNewsFromJson(JSONArray newsArray) throws JSONException {
        final String URL = "URL";
        final String HEADLINE = "Headline";
        final String SOURCE = "Source";
        final String DATE = "Date";
        final String TAGSSTRING = "TagsAsString";

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
                    null,
                    null);

            //Insert the new ones.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            insertedNews = mContext.getContentResolver().bulkInsert(QuotesContract.NewsEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "NEWS insertion  done. " + insertedNews + "  News Inserted");

    }

}
