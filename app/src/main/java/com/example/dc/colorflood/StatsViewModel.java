package com.example.dc.colorflood;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

public class StatsViewModel extends AndroidViewModel {
    private SharedPreferences sP;
    private MutableLiveData<Pair<Integer, Integer>> stats;
    private static StatsViewModel instance = null;

    public StatsViewModel(Application application) {
        super(application);
        this.sP = PreferenceManager.getDefaultSharedPreferences(application);
    }

    void resetInstance(){
        StatsViewModel.instance = null;
    }

    void provideInstance(){
        StatsViewModel.instance = this;
    }

    static StatsViewModel getInstance(){
        return StatsViewModel.instance;
    }

    void updateStats(int currentLvl, int extraTry) {
        this.stats.setValue(new Pair<>(currentLvl, extraTry));
        sP.edit()
                .putInt("extraTry", extraTry)
                .putInt("currentLevel", currentLvl)
                .apply();
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

}