package com.example.dc.colorflood;

import android.arch.lifecycle.LiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Toutes les activités souhaitant jouer un son et/ou continuer la musique en background doivent hériter de cette activité
 */
public class MusicActivity extends AppCompatActivity {
    private static MusicService mService;
    final private static ServiceConnection mConnection = initMConnection();

    void makeASound() {
        mService.meuh();
    }

    void inverseMute() {
        mService.inverseMute();
    }

    void resetMusic() {
        mService.resetMusic();
    }

    LiveData<MusicDataManager.InfoMusic> getInfosMusic() {
        return mService.getInfosMusic();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName(), "CREATE");
    }

    /**
     * Au démarrage, on bind l'activité au service
     */
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(getClass().getSimpleName(), "START");
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getClass().getSimpleName(), "PAUSE");
    }

    /**
     * Au stop, on unbind l'activité au service
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(getClass().getSimpleName(), "STOP");
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "DESTROY");
    }

    private static ServiceConnection initMConnection() {
        return new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                // On récupère l'instance du service auquel on vient de bind
                mService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mService = null;
            }
        };
    }
}
