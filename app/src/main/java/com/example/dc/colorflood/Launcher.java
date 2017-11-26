package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Launcher extends AppCompatActivity {
    Button button_play;
    Button button_credits;
    Button button_reset;
    TextView text_extraTry;
    TextView text_currentLevel;
    int extraTry = 0;
    int currentLevel = 0;
    StatsViewModel statsViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        setContentView(R.layout.activity_launcher);

        text_currentLevel = findViewById(R.id.text_currentLevel);
        text_extraTry = findViewById(R.id.text_extraTry);

        button_play = findViewById(R.id.button_play);
        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Game.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        button_credits = findViewById(R.id.button_credits);
        button_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Credits.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        final Context c = this;
        button_reset = findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(c)
                        .setMessage("En êtes-vous sûr ?.")
                        .setTitle("Reinitialiser votre progression ?")
                        .setCancelable(false)
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                statsViewModel.updateStats(1, 0);
                            }
                        }).show();
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

        text_extraTry.setText("Extra try : " + String.valueOf(extraTry));
        text_currentLevel.setText("Niveau actuel : " + String.valueOf(currentLevel));
    }
}
