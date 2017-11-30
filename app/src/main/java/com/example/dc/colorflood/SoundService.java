package com.example.dc.colorflood;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class SoundService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(getClass().getSimpleName(), "starting");
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.music1);
        mp.start();
        this.stopSelf();
    }

    public SoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
