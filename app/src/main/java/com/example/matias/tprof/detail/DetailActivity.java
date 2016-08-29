package com.example.matias.tprof.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.example.matias.tprof.BondQuotesFragment;
import com.example.matias.tprof.R;
import com.example.matias.tprof.StockQuotesFragment;
import com.example.matias.tprof.ViewPagerAdapter;

public class DetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager_detail);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(savedInstanceState == null){
            Intent intent = getIntent();
            Fragment fragment = null;
            Bundle arguments = new Bundle();
            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
                if(intent.getStringExtra(Intent.EXTRA_TEXT).equals("ACCION")){
                    arguments.putParcelable(StockDetailFragment.DETAIL_URI, getIntent().getData());
                    arguments.putInt(StockDetailFragment.STOCK_ID, intent.getIntExtra("com.example.matias.tprof.Identifier", 0));
                    arguments.putString(StockDetailFragment.SYMBOL, intent.getStringExtra("com.example.matias.tprof.Symbol"));
                    fragment = new StockDetailFragment();
                }
                else{
                    arguments.putParcelable(BondDetailFragment.DETAIL_URI, getIntent().getData());
                    arguments.putInt(BondDetailFragment.BOND_ID, intent.getIntExtra("com.example.matias.tprof.Identifier", 0));
                    arguments.putString(BondDetailFragment.SYMBOL, intent.getStringExtra("com.example.matias.tprof.Symbol"));
                    fragment = new BondDetailFragment();
                }
            }

            fragment.setArguments(arguments);
            Fragment newsFragment = new NewsFragment();
            newsFragment.setArguments(arguments);
            Fragment commentsFragment = new CommentsFragment();
            commentsFragment.setArguments(arguments);

          //  getSupportFragmentManager().beginTransaction()
           //         .add(R.id.detail_fragment_container, fragment)
            //        .commit();

            adapter.addFragment(fragment, "Detalle");
            adapter.addFragment(newsFragment, "Noticias");
            adapter.addFragment(commentsFragment, "Comentarios");
            viewPager.setAdapter(adapter);

            tabLayout = (TabLayout) findViewById(R.id.detail_tabs);
            tabLayout.setupWithViewPager(viewPager);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
