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

    public StatsViewModel(Application application) {
        super(application);
        sP = PreferenceManager.getDefaultSharedPreferences(application);
    }

    void updateStats(int currentLvl, int extraTry) {
        sP.edit()
                .putInt("extraTry", extraTry)
                .putInt("currentLevel", currentLvl)
                .apply();
    }

    LiveData<Pair<Integer, Integer>> getStats(){
        if (stats == null) {
            stats = new MutableLiveData<>();
            loadStats();
        }
        return stats;
    }

    private void loadStats() {
        Pair<Integer, Integer> s = new Pair<>(sP.getInt("currentLevel", 1), sP.getInt("extraTry", 0));
        this.stats.setValue(s);
    }

}
