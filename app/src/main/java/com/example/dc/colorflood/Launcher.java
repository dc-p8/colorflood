package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Launcher extends AppCompatActivity {
    Button buttonPlay;
    Button buttonCredits;
    Button buttonSystem;

    TextView textExtraTry;
    TextView textCurrentLevel;
    int extraTry = 0;
    int currentLevel = 0;
    StatsViewModel statsViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.statsViewModel.resetInstance();
        Log.e(getClass().getSimpleName(), "ONDESTROY");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ONPAUSE", "TEST");
        statsViewModel.updateStats(currentLevel, extraTry);
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
        statsViewModel = ViewModelProviders.of(this).get(StatsViewModel.class);
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
