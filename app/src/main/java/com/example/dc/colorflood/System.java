package com.example.dc.colorflood;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class System extends AppCompatActivity {
    Button button_reset;
    Button toggle_audio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        final Context c = this;
        button_reset = findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
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
                            /*
                                sP.edit()
                                        .putInt("extraTry", 0)
                                        .putInt("curentLevel", 1)
                                        .commit();
                                        */
                            }
                        }).show();
            }
        });

        toggle_audio = findViewById(R.id.toggle_audio);
        toggle_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                audio logic
                 */
            }
        });
    }
}
