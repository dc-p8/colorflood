package com.example.dc.colorflood;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private AssetManager assetManager;
    private MediaPlayer musicPlayer = null;

    private GameViewModel gameViewModel;
    private List<String> musics = null;
    private List<String> sounds = null;
    private Random rnd;

    private GameViewModel.InfoMusic mInfoMusic;
    final private android.arch.lifecycle.Observer<GameViewModel.InfoMusic> observer;

    private void newSong()
    {
        mInfoMusic.songTime = 0;
        if(musics.size() > 0)
            mInfoMusic.songName = musics.get(rnd.nextInt(musics.size()));
        else
            mInfoMusic.songName = null;
    }
    private void setMusic()
    {
        if(mInfoMusic.mute)
            return;

        if(mInfoMusic.songName == null)
            return;

        try{
            if(musicPlayer == null) {
                musicPlayer = new MediaPlayer();
                musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.e(getClass().getSimpleName(), "music ended");
                        newSong();
                        setMusic();
                    }
                });
            }
            else
                musicPlayer.reset();

            AssetFileDescriptor afd = assetManager.openFd("musics/" + mInfoMusic.songName);
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            //
            afd.close();
            musicPlayer.prepare();
            musicPlayer.setVolume(0.5f, 0.5f);
            musicPlayer.start();
            musicPlayer.seekTo(mInfoMusic.songTime);

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            newSong();
            setMusic();
        }
    }

    private int getTime()
    {
        int time = 0;
        if(musicPlayer != null)
            time = musicPlayer.getCurrentPosition();
        return time;
    }

    public void meuh()
    {
        if(mInfoMusic.mute)
            return;

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });

            AssetFileDescriptor afd = assetManager.openFd("sounds/" + sounds.get(rnd.nextInt(sounds.size())));
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

    private void stopMusic()
    {
        if(musicPlayer != null)
        {
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    public MusicService() {
        this.observer = new android.arch.lifecycle.Observer<GameViewModel.InfoMusic>() {
            @Override
            public void onChanged(GameViewModel.InfoMusic infos) {
                Log.e(getClass().getSimpleName(), "changed");

                mInfoMusic = infos;

                if(mInfoMusic.mute) {
                    stopMusic();
                    return;
                }




                Log.e(getClass().getSimpleName(), "getting song : " + mInfoMusic.songTime + " " + mInfoMusic.songName);

                if(mInfoMusic.songName == null)
                {
                    newSong();

                }

                Log.e(getClass().getSimpleName(), "current music : " + mInfoMusic.songName);

                setMusic();


            }
        };
    }

    class LocalBinder extends Binder {
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
        rnd = new Random(java.lang.System.currentTimeMillis());
        assetManager = getAssets();
        try {
            musics = Arrays.asList(assetManager.list("musics"));
            sounds = Arrays.asList(assetManager.list("sounds"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameViewModel = GameViewModel.getInstance();

        gameViewModel.getInfosMusic().observeForever(this.observer);




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "destroyed");
        //gameViewModel.updateInfosMusic();
        /*
        mInfoMusic.songTime =  musicPlayer.getCurrentPosition();
        Log.e(getClass().getSimpleName(), "time : " + mInfoMusic.songName);
        */
        int time = getTime();
        if(musicPlayer != null)
        {
            musicPlayer.release();
            musicPlayer = null;
        }
        if(gameViewModel != null)
        {

            Log.e(getClass().getSimpleName(), "putting : " + time + " " + mInfoMusic.songName);
            gameViewModel.getInfosMusic().removeObserver(this.observer);
            gameViewModel.updateInfosMusic(time, mInfoMusic.songName);
        }
    }
}
