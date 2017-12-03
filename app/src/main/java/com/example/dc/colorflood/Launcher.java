package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Launcher extends MusicActivity{
    private Button buttonPlay;
    private Button buttonCredits;
    private Button buttonSystem;

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

        this.buttonPlay = findViewById(R.id.button_play);
        this.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Game.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        this.buttonCredits = findViewById(R.id.button_credits);
        this.buttonCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Credits.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        this.buttonSystem = findViewById(R.id.button_system);
        this.buttonSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, System.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        this.buttonSystem = findViewById(R.id.button_highscores);
        this.buttonSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Highscores.class);
                Launcher.this.startActivity(myIntent);
            }
        });
    }

    public void updateStat()
    {
        gameViewModel.getStats().observe(this, new Observer<Pair<Integer, Integer>>() {
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
