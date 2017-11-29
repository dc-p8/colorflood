package com.example.dc.colorflood;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();

    public MusicService() {
    }

    public class LocalBinder extends Binder {
        MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(getClass().getSimpleName(), "created");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "destroyed");

    }
}
