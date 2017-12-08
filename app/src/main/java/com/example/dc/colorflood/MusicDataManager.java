package com.example.dc.colorflood;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

class MusicDataManager {
    private final SharedPreferences sP;
    private MutableLiveData<InfoMusic> infosMusic;

    MusicDataManager(Context context) {
        this.sP = PreferenceManager.getDefaultSharedPreferences(context);
    }

    LiveData<InfoMusic> getInfosMusic(){
        if (this.infosMusic == null) {
            loadInfosMusic();
        }
        return this.infosMusic;
    }

    private void loadInfosMusic() {
        this.infosMusic = new MutableLiveData<>();
        InfoMusic infos = new InfoMusic(sP.getInt("songTime", -1), sP.getString("songName", null), sP.getBoolean("songMute", false));
        this.infosMusic.setValue(infos);
    }

    void updateInfosMusic(int songTime, String songName) {
        if (this.infosMusic == null)
            this.infosMusic = new MutableLiveData<>();
        this.infosMusic.setValue(new InfoMusic(songTime, songName, this.infosMusic.getValue() != null && this.infosMusic.getValue().mute));
        sP.edit()
                .putInt("songTime", songTime)
                .putString("songName", songName)
                .apply();
    }

    void inverseMuteMusic() {
        Log.e(getClass().getSimpleName(), "inverse");
        boolean mute;
        if (this.infosMusic == null) {
            loadInfosMusic();
            //noinspection ConstantConditions
            mute = !this.infosMusic.getValue().mute;
        } else {
            //noinspection ConstantConditions
            mute = !this.infosMusic.getValue().mute;
            String songName = this.infosMusic.getValue().songName;
            if(mute)
                songName = null;
            this.infosMusic.setValue(new InfoMusic(this.infosMusic.getValue().songTime, songName, mute));
        }
        sP.edit()
                .putBoolean("songMute", mute)
                .apply();
    }

    /**
     * classe utilitaire pour stocker toute info liée à la musique
     */
    class InfoMusic{
        int songTime;
        String songName;
        boolean mute = false;

        InfoMusic(int songTime, String songName, boolean mute) {
            this.songTime = songTime;
            this.songName = songName;
            this.mute = mute;
        }
    }
}
