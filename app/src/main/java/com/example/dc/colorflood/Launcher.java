package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;


/**
 * Menu principal de l'application
 * se charge de démarrer toutes les autres activités
 */
public class Launcher extends MusicActivity{
    private TextView textExtraTry;
    private TextView textCurrentLevel;
    private int extraTry = 0;
    private int currentLevel = 0;
    static private final int EXIT_REQUEST = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "ONDESTROY");
        if (isFinishing()) {
            this.resetMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getClass().getSimpleName(), "ONPAUSE");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "ONRESUME");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(getClass().getSimpleName(),"ONCREATE");

        GameViewModel gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        //this.gameViewModel = new GameViewModel(getApplication());
        setContentView(R.layout.activity_launcher);

        this.textCurrentLevel = findViewById(R.id.text_currentLevel);
        this.textExtraTry = findViewById(R.id.text_extraTry);

        findViewById(R.id.button_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Game.class);
                startActivity(myIntent);
            }
        });

        findViewById(R.id.button_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Credits.class);
                startActivity(myIntent);
            }
        });

        findViewById(R.id.button_system).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, SystemMenu.class);
                startActivityForResult(myIntent, EXIT_REQUEST);
            }
        });

        findViewById(R.id.button_highscores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Highscores.class);
                startActivity(myIntent);
            }
        });

        gameViewModel.getStats().observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> stats) {
                currentLevel = stats.first;
                extraTry = stats.second;
                textExtraTry.setText(getString(R.string.extra_try, extraTry));
                textCurrentLevel.setText(getString(R.string.actual_lvl, currentLevel));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EXIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("Exit", false))
                    finish();
            }
        }
    }
}
