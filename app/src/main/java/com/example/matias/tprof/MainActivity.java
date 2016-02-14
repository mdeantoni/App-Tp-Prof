package com.example.matias.tprof;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.matias.tprof.detail.DetailActivity;
import com.example.matias.tprof.sync.AppSyncAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BondQuotesFragment.OnBondQuoteSelectedListener,
                                                               StockQuotesFragment.OnStockQuoteSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static boolean appIsSynced = false; //ESTO ESTA MAL Y HAY QUE CAMBIARLO SI O SI!!!!!

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBondQuoteSelected(Uri bondDetailURi) {
        Intent bondQuoteIntent = new Intent(this, DetailActivity.class)
                .setData(bondDetailURi)
                .putExtra(Intent.EXTRA_TEXT,"BONO");
        startActivity(bondQuoteIntent);
    }

    @Override
    public void onStockQuoteSelected(Uri stockDetailUri) {
        Intent stockQuoteIntent = new Intent(this, DetailActivity.class)
                .setData(stockDetailUri)
                .putExtra(Intent.EXTRA_TEXT,"ACCION");
        startActivity(stockQuoteIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!appIsSynced) {
            AppSyncAdapter.syncImmediately(this);
            appIsSynced = true;
        }
    }
}
