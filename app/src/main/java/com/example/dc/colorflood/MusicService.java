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

/**
 * Joue de la musique tant qu'une activité est bind
 */
public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private AssetManager assetManager;
    private MediaPlayer musicPlayer = null;

    private GameViewModel gameViewModel;
    private List<String> musics = null;
    private List<String> sounds = null;
    private Random rnd;

    private GameViewModel.InfoMusic mInfoMusic;
    final private android.arch.lifecycle.Observer<GameViewModel.InfoMusic> observer = new android.arch.lifecycle.Observer<GameViewModel.InfoMusic>() {
        @Override
        public void onChanged(GameViewModel.InfoMusic infos) {
            mInfoMusic = infos;

            if(mInfoMusic.mute) {
                // Si on vient d'appuyer sur mute, on arrête la musique
                stopMusic();
                return;
            }

            if(mInfoMusic.songName == null)
            {
                // Si il n'y avait pas de son définit (par exemple au lancement de l'app), on initialise un nouveau son
                newSong();
            }

            // On démarre la lecture
            setMusic();


        }
    };

    /**
     * Initialise un nouveau son
     */
    private void newSong()
    {
        mInfoMusic.songTime = 0;
        if(musics.size() > 0)
            mInfoMusic.songName = musics.get(rnd.nextInt(musics.size()));
        else
            mInfoMusic.songName = null;
    }

    /**
     * Joue la musique en fonction des données
     */
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
                        // Lorsque la musique est finit, on en met une nouvelle
                        newSong();
                        setMusic();
                    }
                });
            }
            else
            {
                // Si on veut jouer un son alors que le player est déjà utilisé, il faut obligatoriement le reset
                musicPlayer.reset();
            }


            AssetFileDescriptor afd = assetManager.openFd("musics/" + mInfoMusic.songName);
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
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

    /**
     * @return le temps où est une musiqueen train d'être jouée
     */
    private int getTime()
    {
        int time = 0;
        if(musicPlayer != null)
            time = musicPlayer.getCurrentPosition();
        return time;
    }

    /**
     * Créer un son bref et aléatoire
     */
    public void meuh()
    {
        if(mInfoMusic.mute)
            return;

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Lorsque le son est finit, il faut relacher le média player. Fuite de mémoire sinon
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
        Log.d(getClass().getSimpleName(), "CREATE");
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
        Log.d(getClass().getSimpleName(), "DESTROY");
        int time = getTime();
        if(musicPlayer != null)
        {
            musicPlayer.release();
            musicPlayer = null;
        }
        if(gameViewModel != null)
        {
            Log.d(getClass().getSimpleName(), "putting : " + time + " " + mInfoMusic.songName);
            gameViewModel.getInfosMusic().removeObserver(this.observer);
            gameViewModel.updateInfosMusic(time, mInfoMusic.songName);
        }
    }
}
