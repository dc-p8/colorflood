package com.example.dc.colorflood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Launcher extends AppCompatActivity {
    Button play;
    Button credits;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "ONDESTROY");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ONPAUSE", "TEST");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ONCREATE", "TEST");
        setContentView(R.layout.activity_launcher);

        play = findViewById(R.id.button_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, MainActivity.class);
                Launcher.this.startActivity(myIntent);
            }
        });

        credits = findViewById(R.id.button_credits);
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Launcher.this, Credits.class);
                Launcher.this.startActivity(myIntent);
            }
        });

    }
}
