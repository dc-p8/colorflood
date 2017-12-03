package com.example.dc.colorflood;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

class GameViewModel extends AndroidViewModel {
    final private SharedPreferences sP;
    private MutableLiveData<Pair<Integer, Integer>> stats;
    private MutableLiveData<InfoMusic> infosMusic;
    private MutableLiveData<Cursor> scores;
    private static GameViewModel instance = null;
    final private ScoresDatabaseManager scoresManager;

    public GameViewModel(Application application) {
        super(application);
        this.sP = PreferenceManager.getDefaultSharedPreferences(application);
        this.scoresManager = new ScoresDatabaseManager(application);
        GameViewModel.instance = this;
    }

    static GameViewModel getInstance(){
        return GameViewModel.instance;
    }



    LiveData<Pair<Integer, Integer>> getStats(){
        if (this.stats == null) {
            loadStats();
        }
        return this.stats;
    }

    private void loadStats() {
        this.stats = new MutableLiveData<>();
        Pair<Integer, Integer> s = new Pair<>(sP.getInt("currentLevel", 1), sP.getInt("extraTry", 0));
        this.stats.setValue(s);
    }

    void updateStats(int currentLvl, int extraTry) {
        if (this.stats == null) {
            this.stats = new MutableLiveData<>();
        }
        this.stats.setValue(new Pair<>(currentLvl, extraTry));
        sP.edit()
                .putInt("extraTry", extraTry)
                .putInt("currentLevel", currentLvl)
                .apply();
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
                .putString("idSong", songName)
                .apply();
    }

    void inverseMuteMusic() {
        Log.e(getClass().getSimpleName(), "inverse");
        boolean mute;
        if (this.infosMusic == null) {
            loadInfosMusic();
            mute = !this.infosMusic.getValue().mute;
        } else {
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



    LiveData<Cursor> getScores(){
        if (this.scores == null) {
            this.scores = new MutableLiveData<>();
            loadScores();
        }
        return this.scores;
    }

    private void loadScores() {
        this.scoresManager.executeSelectAll(new ScoresDatabaseManager.AsyncCursorResponse() {
            @Override
            public void processResult(Cursor res) {
                setScores(res);
            }
        });
    }

    private void setScores(Cursor cursor){
        if (this.scores != null){
            if (this.scores.getValue() != null && !this.scores.getValue().isClosed())
                this.scores.getValue().close();
        } else {
            this.scores = new MutableLiveData<>();
        }
        this.scores.setValue(cursor);
    }

    void deleteScores(){
        this.scoresManager.executeDeleteAll();
        loadScores();
    }

    void updateScores(int lvl, long score){
        this.scoresManager.executeAddOrUpdateIfBetter(lvl, score);
        loadScores();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        GameViewModel.instance = null;
        if (this.scores != null && this.scores.getValue() != null && !this.scores.getValue().isClosed())
                this.scores.getValue().close();
    }
}
