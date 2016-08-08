package com.example.matias.tprof;

import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.matias.tprof.data.QuotesContract;
import com.example.matias.tprof.detail.DetailActivity;
import com.example.matias.tprof.portfolio.PortfolioActivity;
import com.example.matias.tprof.settings.SettingsActivity;
import com.example.matias.tprof.sync.AppSyncAdapter;
import com.example.matias.tprof.task.FetchHistoricalQuotesTask;

public class MainActivity extends AppCompatActivity implements BondQuotesFragment.OnBondQuoteSelectedListener,
                                                               StockQuotesFragment.OnStockQuoteSelectedListener,
                                                               android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static boolean appIsSynced = false; //ESTO ESTA MAL Y HAY QUE CAMBIARLO SI O SI!!!!!
    private boolean hasHoldings;
    private static final int TOTAL_HOLDINGS_LOADER = 1;

    private static final String[] TOTAL_HOLDINGS_COLUMNS = {
        " COUNT(*) "
    };

    static final int HOLDING_TOTAL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StockQuotesFragment(), "Acciones");
        adapter.addFragment(new BondQuotesFragment(), "Bonos");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Some Changes. Some more Changes // Changes as
        getMenuInflater().inflate(R.menu.menu_main, menu);



        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem mMenuItem =  menu.findItem(R.id.search);
        SearchView searchView = (SearchView)mMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


       //MenuItemCompat.collapseActionView(mMenuItem);
        //searchView.clearFocus();

        return true;
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
    public void onBondQuoteSelected(Uri bondDetailURi,int bondId, String symbol) {
        Intent bondQuoteIntent = new Intent(this, DetailActivity.class)
                .setData(bondDetailURi)
                .putExtra(Intent.EXTRA_TEXT, "BONO")
                .putExtra("com.example.matias.tprof.Identifier",bondId)
                .putExtra("com.example.matias.tprof.Symbol", symbol);
        //FetchNewsTask getNewsTask = new FetchNewsTask(this);
        //getNewsTask.execute(symbol);
        startActivity(bondQuoteIntent);
    }

    @Override
         public void onStockQuoteSelected(Uri stockDetailUri,int stockId, String symbol) {
        Intent stockQuoteIntent = new Intent(this, DetailActivity.class)
                .setData(stockDetailUri)
                .putExtra(Intent.EXTRA_TEXT, "ACCION")
                .putExtra("com.example.matias.tprof.Identifier",stockId)
                .putExtra("com.example.matias.tprof.Symbol", symbol);
        FetchHistoricalQuotesTask getHistoricalTask = new FetchHistoricalQuotesTask(this);
        //FetchNewsTask getNewsTask = new FetchNewsTask(this);
        getHistoricalTask.execute(symbol);
        //getNewsTask.execute(symbol);
        startActivity(stockQuoteIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
       // if(!appIsSynced) {
        //    AppSyncAdapter.syncImmediately(this);
        //    appIsSynced = true;
       // }
    }

    @Override
    protected  void onResume(){
        getLoaderManager().restartLoader(TOTAL_HOLDINGS_LOADER, null, this);
        super.onResume();
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
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
