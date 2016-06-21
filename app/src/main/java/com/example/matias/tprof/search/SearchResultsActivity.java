package com.example.matias.tprof.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.matias.tprof.R;
import com.example.matias.tprof.StockQuotesAdapter;
import com.example.matias.tprof.data.QuotesContract;
import com.example.matias.tprof.detail.DetailActivity;
import com.example.matias.tprof.task.FetchHistoricalQuotesTask;
import com.example.matias.tprof.task.FetchNewsTask;

public class SearchResultsActivity extends AppCompatActivity {

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
            QuotesContract.SearchResultsEntry.COLUMN_QUOTE_ID,
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
    static final int COL_QUOTE_ID = 9;

    private SearchResultsAdapter mResultsAdapter;
    private static final int SEARCH_RESULTS_LOADER = 0;
    private String Query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Some Changes. Some more Changes // Changes as
        getMenuInflater().inflate(R.menu.menu_main, menu);



        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem mMenuItem = menu.findItem(R.id.search);
        SearchView searchView =(SearchView)  mMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        MenuItemCompat.expandActionView(mMenuItem);
        searchView.setQuery(this.Query, false);
        searchView.clearFocus();

        return true;
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            this.Query = query;
            query = "%" + query + "%";
            ListView listView = (ListView) findViewById(R.id.listview_search_results);
            String sortOrder = QuotesContract.SearchResultsEntry.COLUMN_FULLNAME + " ASC";
            Uri resultsUri = QuotesContract.SearchResultsEntry.CONTENT_URI;


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        if (cursor.getString(COL_TYPE).equals("Stock")) {
                            Uri stockUri = QuotesContract.StockQuotesEntry.buildStockQuoteUri(cursor.getInt(COL_QUOTE_ID));
                            int stockId = cursor.getInt(COL_ID);
                            String symbol = cursor.getString(COL_SYMBOL);

                            Intent stockQuoteIntent = new Intent(SearchResultsActivity.this, DetailActivity.class)
                                    .setData(stockUri)
                                    .putExtra(Intent.EXTRA_TEXT, "ACCION")
                                    .putExtra("com.example.matias.tprof.Identifier", stockId)
                                    .putExtra("com.example.matias.tprof.Symbol", symbol);
                            FetchHistoricalQuotesTask getHistoricalTask = new FetchHistoricalQuotesTask(SearchResultsActivity.this);
                            getHistoricalTask.execute(symbol);

                            startActivity(stockQuoteIntent);
                        }
                    }

                    if (cursor.getString(COL_TYPE).equals("Bond")) {
                        Uri bondUri = QuotesContract.BondQuotesEntry.buildBondQuoteUri(cursor.getInt(COL_QUOTE_ID));
                        int bondId = cursor.getInt(COL_ID);
                        String symbol = cursor.getString(COL_SYMBOL);

                        Intent bondQuoteIntent = new Intent(SearchResultsActivity.this, DetailActivity.class)
                                .setData(bondUri)
                                .putExtra(Intent.EXTRA_TEXT, "BONO")
                                .putExtra("com.example.matias.tprof.Identifier",bondId)
                                .putExtra("com.example.matias.tprof.Symbol", symbol);
                        startActivity(bondQuoteIntent);
                    }
                }
            });

            Cursor cur = getContentResolver().query(resultsUri,
                    SEARCH_RESULT_COLUMNS,
                    QuotesContract.SearchResultsEntry.VIEW_NAME +
                            "." + QuotesContract.SearchResultsEntry.COLUMN_FULLNAME + " like ?",
                    new String[]{query},
                    sortOrder);

            mResultsAdapter = new SearchResultsAdapter(this, cur, 0);

            listView.setAdapter(mResultsAdapter);
        }

        if(Intent.ACTION_VIEW.equals(intent.getAction())){
            String data = intent.getDataString();
            String[] dataArray = data.split("-");
            String type = dataArray[0];
            String id = dataArray[2];
            String quoteId = dataArray[1];
            String symbol = dataArray[3];

            if(type.equals("Stock")){
                Uri stockUri = QuotesContract.StockQuotesEntry.buildStockQuoteUri(Integer.parseInt(quoteId));
                int stockId = Integer.parseInt(id);

                Intent stockQuoteIntent = new Intent(this, DetailActivity.class)
                        .setData(stockUri)
                        .putExtra(Intent.EXTRA_TEXT, "ACCION")
                        .putExtra("com.example.matias.tprof.Identifier",stockId)
                        .putExtra("com.example.matias.tprof.Symbol", symbol);
                FetchHistoricalQuotesTask getHistoricalTask = new FetchHistoricalQuotesTask(this);
                //FetchNewsTask getNewsTask = new FetchNewsTask(this);
                getHistoricalTask.execute(symbol);
                //getNewsTask.execute(symbol);
                startActivity(stockQuoteIntent);

            }

            if(type.equals("Bond")){
                Uri stockUri = QuotesContract.BondQuotesEntry.buildBondQuoteUri(Integer.parseInt(quoteId));
                int bondId = Integer.parseInt(id);

                Intent bondQuoteIntent = new Intent(this, DetailActivity.class)
                        .setData(stockUri)
                        .putExtra(Intent.EXTRA_TEXT, "BONO")
                        .putExtra("com.example.matias.tprof.Identifier",bondId)
                        .putExtra("com.example.matias.tprof.Symbol", symbol);
                //FetchNewsTask getNewsTask = new FetchNewsTask(this);
                //getNewsTask.execute(symbol);
                startActivity(bondQuoteIntent);
            }
        }
    }
}
