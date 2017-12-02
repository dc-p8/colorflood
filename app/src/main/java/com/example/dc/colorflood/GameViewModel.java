package com.example.dc.colorflood;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Pair;

public class GameViewModel extends AndroidViewModel {
    private SharedPreferences sP;
    private MutableLiveData<Pair<Integer, Integer>> stats;
    private MutableLiveData<Pair<Integer, String>> infosMusic;
    private MutableLiveData<Cursor> scores;
    private static GameViewModel instance = null;
    private ScoresDatabaseManager scoresManager;

    public GameViewModel(Application application) {
        super(application);
        this.sP = PreferenceManager.getDefaultSharedPreferences(application);
        this.scoresManager = new ScoresDatabaseManager(application);
    }

    void resetInstance(){
        GameViewModel.instance = null;
    }

    void provideInstance(){
        GameViewModel.instance = this;
    }

    static GameViewModel getInstance(){
        return GameViewModel.instance;
    }



    LiveData<Pair<Integer, Integer>> getStats(){
        if (this.stats == null) {
            this.stats = new MutableLiveData<>();
            loadStats();
        }
        return this.stats;
    }

    private void loadStats() {
        Pair<Integer, Integer> s = new Pair<>(sP.getInt("currentLevel", 1), sP.getInt("extraTry", 0));
        this.stats.setValue(s);
    }

    void updateStats(int currentLvl, int extraTry) {
        this.stats.setValue(new Pair<>(currentLvl, extraTry));
        sP.edit()
                .putInt("extraTry", extraTry)
                .putInt("currentLevel", currentLvl)
                .apply();
    }



    LiveData<Pair<Integer, String>> getInfosMusic(){
        if (this.infosMusic == null) {
            this.infosMusic = new MutableLiveData<>();
            loadInfosMusic();
        }
        return this.infosMusic;
    }

    private void loadInfosMusic() {
        Pair<Integer, String> infos = new Pair<>(sP.getInt("songTime", -1), sP.getString("songName", null));
        this.infosMusic.setValue(infos);
    }

    void updateInfosMusic(int songTime, String songName) {
        if (this.infosMusic == null)
            this.infosMusic = new MutableLiveData<>();
        this.infosMusic.setValue(new Pair<>(songTime, songName));
        sP.edit()
                .putInt("songTime", songTime)
                .putString("idSong", songName)
                .apply();
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
}
