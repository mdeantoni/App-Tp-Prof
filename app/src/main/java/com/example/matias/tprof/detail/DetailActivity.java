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
            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
                if(intent.getStringExtra(Intent.EXTRA_TEXT).equals("ACCION")){
                    fragment = new StockDetailFragment();
                }
                else{
                    fragment = new BondDetailFragment();
                }
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, fragment)
                    .commit();
        }
    }

}
