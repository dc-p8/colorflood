package com.example.dc.colorflood;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Launcher extends AppCompatActivity {
    Button button_play;
    Button button_credits;
    Button button_system;

    TextView text_extraTry;
    TextView text_currentLevel;
    int extraTry = 0;
    int currentLevel = 0;
    SharedPreferences sP;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "ONDESTROY");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ONPAUSE", "TEST");
        sP.edit()
                .putInt("extra_try", extraTry)
                .putInt("current_level", currentLevel)
                .commit();
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
        sP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

        button_system = findViewById(R.id.button_system);
        button_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, System.class);
                Launcher.this.startActivity(myIntent);
            }
        });




    }

    public void updateStat()
    {
        extraTry = sP.getInt("extraTry", 0);
        currentLevel = sP.getInt("currentLevel", 1);

        text_extraTry.setText("Extra try : " + String.valueOf(extraTry));
        text_currentLevel.setText("Niveau actuel : " + String.valueOf(currentLevel));
    }
}
