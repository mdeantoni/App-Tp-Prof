package com.example.matias.tprof.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
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

import okhttp3.MediaType;

/**
 * Created by Mati on 7/31/2016.
 */
public class RefreshCommentsTask extends AsyncTask<String, Void, Void> {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public final String LOG_TAG = RefreshCommentsTask.class.getSimpleName();

    private final Context mContext;
    private SwipeRefreshLayout mRefreshLayout;
    String responseText;
    int oldestIdentifier;

    public RefreshCommentsTask(Context context , SwipeRefreshLayout refreshLayout) {
        this.mRefreshLayout = refreshLayout;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.d(LOG_TAG, "Starting Task");

        if (params.length == 0) {
            return null;
        }

        String tickerSymbol = params[0];

        Cursor commentsCursor = mContext.getContentResolver().query(
                QuotesContract.CommentsEntry.CONTENT_URI,
                new String[] {QuotesContract.CommentsEntry.COLUMN_IDENTIFIER},
                QuotesContract.CommentsEntry.COLUMN_SYMBOL + " = ?",
                new String[]{tickerSymbol},
                QuotesContract.CommentsEntry.COLUMN_DATE + " DESC"
        );

        if(commentsCursor.moveToFirst()){
            oldestIdentifier = commentsCursor.getInt(0);
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String testData = null;

        try {
            final String MESSAGES_URL = mContext.getString(R.string.connection_string) + "messages";
            final String QUERY_PARAM_TICKER = "tickerSymbol";
            final String QUERY_PARAM_ID = "id";


            Uri builtUri = Uri.parse(MESSAGES_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM_TICKER, tickerSymbol)
                    .appendQueryParameter(QUERY_PARAM_ID, Integer.toString(oldestIdentifier))
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
            getCommentsFromJson  (testData);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error running task ", e);
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

        Log.d(LOG_TAG, "Finished executing task.");
        return null;
    }

    public void getCommentsFromJson(String syncData) throws JSONException {
        final String Id = "Id";
        final String Date = "Date";
        final String TickerSymbol = "TickerSymbol";
        final String MessageText = "MessageText";
        final String Username = "Username";

        JSONArray newsArray = new JSONArray(syncData);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(newsArray.length());

        for (int newsIndex = 0; newsIndex < newsArray.length(); newsIndex++) {

            JSONObject commentObject = newsArray.getJSONObject(newsIndex);

            ContentValues newsQuoteValue = new ContentValues();
            newsQuoteValue.put(QuotesContract.CommentsEntry.COLUMN_IDENTIFIER, commentObject.getString(Id));
            newsQuoteValue.put(QuotesContract.CommentsEntry.COLUMN_DATE, commentObject.getString(Date));
            newsQuoteValue.put(QuotesContract.CommentsEntry.COLUMN_SYMBOL, commentObject.getString(TickerSymbol));
            newsQuoteValue.put(QuotesContract.CommentsEntry.COLUMN_COMMENT, commentObject.getString(MessageText));
            newsQuoteValue.put(QuotesContract.CommentsEntry.COLUMN_USERNAME, commentObject.getString(Username));

            cVVector.add(newsQuoteValue);
        }

        int insertedComments = 0;
        if (cVVector.size() > 0) {
            //Insert the new ones.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            insertedComments = mContext.getContentResolver().bulkInsert(QuotesContract.CommentsEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "Insertion of data done." + insertedComments + " new entries inserted");
    }
}
