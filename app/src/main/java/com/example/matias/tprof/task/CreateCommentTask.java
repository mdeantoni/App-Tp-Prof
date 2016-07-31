package com.example.matias.tprof.task;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.example.matias.tprof.data.QuotesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mati on 7/30/2016.
 */
public class CreateCommentTask extends AsyncTask<String, Void, Void> {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public final String LOG_TAG = FetchNewsTask.class.getSimpleName();

    private final Context mContext;
    private SwipeRefreshLayout mRefreshLayout;
    String responseText;

    public CreateCommentTask(Context context, SwipeRefreshLayout refreshLayout) {
        mContext = context;
        this.mRefreshLayout = refreshLayout;
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
        if (params.length == 0) {
            return null;
        }

        String tickerSymbol = params[0];
        String message = params[1];
        String username = params[2];
        String date = params[3];
        String lastIdentifier = params[4];


        OkHttpClient httpClient = new OkHttpClient();

        final String NEWS_URL =  "http://10.0.2.2:50914/api/messages";

        RequestBody formBody = new FormBody.Builder()
                .add("Username", username)
                .add("Date", date)
                .add("TickerSymbol", tickerSymbol)
                .add("MessageText", message)
                .add("Id", lastIdentifier)
                .build();

        Request request = new Request.Builder()
                .url(NEWS_URL)
                .post(formBody)
                .build();

        Response response = null;

        try {
            response = httpClient.newCall(request)
                                 .execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            responseText = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            getCommentsFromJson(responseText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        Log.d(LOG_TAG, "Comment insertion done." + insertedComments + " Inserted");
    }
}
