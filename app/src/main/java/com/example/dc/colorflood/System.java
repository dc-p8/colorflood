package com.example.dc.colorflood;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class System extends MusicActivity {
    private StatsViewModel statsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statsViewModel = StatsViewModel.getInstance();
        setContentView(R.layout.activity_system);
        final Context c = this;
        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                new AlertDialog.Builder(c)
                        .setMessage("En êtes-vous sûr ?.")
                        .setTitle("Réinitialiser votre progression ?")
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

        Button toggleAudio = findViewById(R.id.toggle_audio);
        toggleAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                audio logic
                 */
            }
        });
    }
}
