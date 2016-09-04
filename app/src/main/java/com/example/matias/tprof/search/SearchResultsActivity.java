package com.example.matias.tprof.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.example.matias.tprof.R;
import com.example.matias.tprof.StockQuotesAdapter;
import com.example.matias.tprof.data.QuotesContract;
import com.example.matias.tprof.detail.DetailActivity;
import com.example.matias.tprof.portfolio.PortfolioActivity;
import com.example.matias.tprof.settings.SettingsActivity;
import com.example.matias.tprof.task.FetchHistoricalQuotesTask;

public class SearchResultsActivity  extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

    private boolean hasHoldings;
    private static final int TOTAL_HOLDINGS_SEARCH_LOADER = 1;

    private static final String[] TOTAL_HOLDINGS_COLUMNS = {
            " COUNT(*) "
    };
    static final int HOLDING_TOTAL = 0;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        if (id == R.id.action_portfolio) {
            if(hasHoldings) {
                Intent portfolioIntent = new Intent(this, PortfolioActivity.class);
                startActivity(portfolioIntent);
            }else{
                Toast toast = Toast.makeText(this, "La cartera se encuentra vacía.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        return super.onOptionsItemSelected(item);
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
                getHistoricalTask.execute(symbol);
                startActivity(stockQuoteIntent);
                finish();

            }

            if(type.equals("Bond")){
                Uri stockUri = QuotesContract.BondQuotesEntry.buildBondQuoteUri(Integer.parseInt(quoteId));
                int bondId = Integer.parseInt(id);

                Intent bondQuoteIntent = new Intent(this, DetailActivity.class)
                        .setData(stockUri)
                        .putExtra(Intent.EXTRA_TEXT, "BONO")
                        .putExtra("com.example.matias.tprof.Identifier",bondId)
                        .putExtra("com.example.matias.tprof.Symbol", symbol);
                startActivity(bondQuoteIntent);
                finish();
            }
        }
    }

    @Override
    protected  void onResume(){
        getLoaderManager().restartLoader(TOTAL_HOLDINGS_SEARCH_LOADER, null, this);
        super.onResume();
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.content.CursorLoader(
                this,
                QuotesContract.HoldingsViewEntry.CONTENT_URI,
                TOTAL_HOLDINGS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            if(cursor.getInt(HOLDING_TOTAL) > 0)
                hasHoldings = true;
            else
                hasHoldings = false;
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}
