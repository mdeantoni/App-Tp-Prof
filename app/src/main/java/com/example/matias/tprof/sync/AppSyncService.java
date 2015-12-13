package com.example.matias.tprof.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Mati on 12/13/2015.
 */
public class AppSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static AppSyncAdapter appSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("AppSyncService", "onCreate - AppSyncService");
        synchronized (sSyncAdapterLock) {
            if (appSyncAdapter == null) {
                appSyncAdapter = new AppSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return appSyncAdapter.getSyncAdapterBinder();
    }
}