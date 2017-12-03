package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

public class Launcher extends MusicActivity{
    private TextView textExtraTry;
    private TextView textCurrentLevel;
    private int extraTry = 0;
    private int currentLevel = 0;
    private GameViewModel gameViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.gameViewModel.updateInfosMusic(0, null);
        Log.e(getClass().getSimpleName(), "ONDESTROY");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ONPAUSE", "TEST");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ONCREATE", "TEST");
        this.gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        setContentView(R.layout.activity_launcher);

        this.textCurrentLevel = findViewById(R.id.text_currentLevel);
        this.textExtraTry = findViewById(R.id.text_extraTry);

        findViewById(R.id.button_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Game.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        findViewById(R.id.button_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Credits.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        findViewById(R.id.button_system).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, System.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        findViewById(R.id.button_highscores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Highscores.class);
                Launcher.this.startActivity(myIntent);
            }
        });
    }

    private void updateStat()
    {
        gameViewModel.getStats().observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> stats) {
                currentLevel = stats.first;
                extraTry = stats.second;
            }
        });

        textExtraTry.setText(getString(R.string.extra_try, extraTry));
        textCurrentLevel.setText(getString(R.string.actual_lvl, currentLevel));
    }
}
