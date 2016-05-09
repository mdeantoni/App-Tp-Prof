package com.example.matias.tprof.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.matias.tprof.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, fragment)
                    .commit();
        }
    }

}
