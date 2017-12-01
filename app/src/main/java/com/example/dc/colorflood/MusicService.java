package com.example.dc.colorflood;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    AssetManager assetManager;
    MediaPlayer musicPlayer = null;
    String current_music;
    int current_sec;
    StatsViewModel statsViewModel;
    List<String> musics = null;
    List<String> sounds = null;
    Random rdn;

    android.arch.lifecycle.Observer<Pair<Integer, String>> observer = new android.arch.lifecycle.Observer<Pair<Integer, String>>() {
        @Override
        public void onChanged(Pair<Integer, String> stats) {
            Log.e(getClass().getSimpleName(), "changed");
            if(musicPlayer == null)
                return;

            current_sec = stats.first;
            current_music = stats.second;



            Log.e(getClass().getSimpleName(), "getting song : " + current_sec + " " + current_music);

            if(current_music == null)
            {
                newSong();

            }
            if(current_sec == -1)
            {
                current_sec = 0;
            }

            Log.e(getClass().getSimpleName(), "current music : " + current_music);

            setMusic();


        }
    };

    public void newSong()
    {
        current_sec = 0;
        if(musics.size() > 0)
            current_music = musics.get(rdn.nextInt(musics.size()));
        else
            current_music = null;
    }
    public void setMusic()
    {
        if(current_music == null)
            return;

        try{
            musicPlayer.reset();
            AssetFileDescriptor afd = assetManager.openFd("musics/" + current_music);
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            //
            afd.close();
            musicPlayer.prepare();
            musicPlayer.setVolume(0.5f, 0.5f);
            musicPlayer.start();
            musicPlayer.seekTo(current_sec);

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            newSong();
            setMusic();
        }
    }

    public void meuh()
    {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });

            AssetFileDescriptor afd = assetManager.openFd("sounds/" + sounds.get(rdn.nextInt(sounds.size())));
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            //
            afd.close();
            mediaPlayer.prepare();

            mediaPlayer.start();
            //soundPlayer.seekTo(0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

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
        Log.e(getClass().getSimpleName(), "create");
        rdn = new Random(java.lang.System.currentTimeMillis());
        assetManager = getAssets();
        try {
            musics = Arrays.asList(assetManager.list("musics"));
            sounds = Arrays.asList(assetManager.list("sounds"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        musicPlayer = new MediaPlayer();
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(getClass().getSimpleName(), "music ended");
                newSong();
                setMusic();
            }
        });


        statsViewModel = StatsViewModel.getInstance();

        statsViewModel.getInfosMusic().observeForever(observer);




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "destroyed");
        //statsViewModel.updateInfosMusic();
        current_sec =  musicPlayer.getCurrentPosition();
        Log.e(getClass().getSimpleName(), "time : " + current_sec);
        if(musicPlayer != null)
        {
            musicPlayer.release();
            musicPlayer = null;
        }
        if(statsViewModel != null)
        {
            Log.e(getClass().getSimpleName(), "putting : " + current_sec + " " + current_music);
            statsViewModel.getInfosMusic().removeObserver(observer);
            statsViewModel.updateInfosMusic(current_sec, current_music);
        }


    }
}
