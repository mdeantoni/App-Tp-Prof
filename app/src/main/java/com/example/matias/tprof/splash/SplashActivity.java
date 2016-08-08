package com.example.matias.tprof.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.matias.tprof.MainActivity;
import com.example.matias.tprof.R;
import com.example.matias.tprof.task.MainTask;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity implements MainTask.OnDataLoadedActivity {

    private static final long SPLASH_SCREEN_DELAY = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainTask dataTask = new MainTask(this, this);
        dataTask.execute();
    }

    @Override
    public void dataSyncFinished() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
