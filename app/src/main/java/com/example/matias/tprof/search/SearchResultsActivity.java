package com.example.matias.tprof.search;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.StockQuotesAdapter;
import com.example.matias.tprof.data.QuotesContract;

public class SearchResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] SEARCH_RESULT_COLUMNS = {
            QuotesContract.SearchResultsEntry.COLUMN_Id,
            QuotesContract.SearchResultsEntry.COLUMN_TYPE,
            QuotesContract.SearchResultsEntry.COLUMN_FULLNAME,
            QuotesContract.SearchResultsEntry.COLUMN_SYMBOL,
            QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_PRICE,
            QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_DATE,
            QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE,
            QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE_PERCENTAGE,
            QuotesContract.SearchResultsEntry.COLUMN_CURRENCY,
            "rowid _id" //Hack para que funcione un cursor sin identificador explicito
    };

    static final int COL_ID = 0;
    static final int COL_TYPE = 1;
    static final int COL_FULLNAME = 2;
    static final int COL_SYMBOL = 3;
    static final int COL_LAST_PRICE = 4;
    static final int COL_LAST_TRADE_DATE = 5;
    static final int COL_LAST_CHANGE = 6;
    static final int COL_LAST_CHANGE_PERCENTAGE = 7;
    static final int COL_CURRENCY = 8;

    private SearchResultsAdapter mResultsAdapter;
    private static final int SEARCH_RESULTS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportLoaderManager().initLoader(SEARCH_RESULTS_LOADER, null, this);

        ListView listView = (ListView) findViewById(R.id.listview_search_results);


        String sortOrder = QuotesContract.SearchResultsEntry.COLUMN_FULLNAME + " ASC";
        Uri resultsUri = QuotesContract.SearchResultsEntry.CONTENT_URI;

        Cursor cur = getContentResolver().query(resultsUri,
                SEARCH_RESULT_COLUMNS, null, null, sortOrder);

        mResultsAdapter = new SearchResultsAdapter(this, cur, 0);

        listView.setAdapter(mResultsAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = QuotesContract.SearchResultsEntry.COLUMN_FULLNAME + " ASC";
        Uri resultsUri = QuotesContract.SearchResultsEntry.CONTENT_URI;

        return new CursorLoader(this,
                resultsUri,
                SEARCH_RESULT_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mResultsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            mResultsAdapter.swapCursor(null);
    }
}
