package com.example.dc.colorflood;


import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Pair;

/**
 * ViewModel qui gère les données afffichée dans l'UI
 */
@SuppressWarnings("WeakerAccess")
public class GameViewModel extends AndroidViewModel {
    private final SharedPreferences sP;
    private MutableLiveData<Pair<Integer, Integer>> stats;
    private MutableLiveData<Cursor> scores;
    @SuppressLint("StaticFieldLeak")
    private static GameViewModel instance = null;
    private final ScoresDatabaseManager scoresManager;

    public GameViewModel(Application application) {
        super(application);
        this.sP = PreferenceManager.getDefaultSharedPreferences(application);
        this.scoresManager = new ScoresDatabaseManager(application);
        GameViewModel.instance = this;
    }

    /**
     * Ceci est un hack en attendant des nouvelles de https://github.com/googlesamples/android-architecture-components/issues/29
     * @return l'instance GameViewModel présentement utilisée par la dernière activité qui en a instancié
     */
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
