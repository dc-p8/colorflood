package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Launcher extends MusicActivity {
    private Button buttonPlay;
    private Button buttonCredits;
    private Button buttonSystem;

    private TextView textExtraTry;
    private TextView textCurrentLevel;
    private int extraTry = 0;
    private int currentLevel = 0;
    private GameViewModel statsViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.statsViewModel.updateInfosMusic(-1, -1);
        this.statsViewModel.resetInstance();
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
        statsViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        this.statsViewModel.provideInstance();
        setContentView(R.layout.activity_launcher);

        textCurrentLevel = findViewById(R.id.text_currentLevel);
        textExtraTry = findViewById(R.id.text_extraTry);

        buttonPlay = findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Game.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        buttonCredits = findViewById(R.id.button_credits);
        buttonCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Credits.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        buttonSystem = findViewById(R.id.button_system);
        buttonSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, System.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        buttonSystem = findViewById(R.id.button_highscores);
        buttonSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Highscores.class);
                Launcher.this.startActivity(myIntent);
            }
        });



    }

    public void updateStat()
    {
        statsViewModel.getStats().observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> stats) {
                currentLevel = stats.first;
                extraTry = stats.second;
            }
        });

        textExtraTry.setText("Extra try : " + String.valueOf(extraTry));
        textCurrentLevel.setText("Niveau actuel : " + String.valueOf(currentLevel));
    }
}
