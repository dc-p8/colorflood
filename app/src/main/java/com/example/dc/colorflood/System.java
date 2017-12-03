package com.example.dc.colorflood;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class System extends MusicActivity {
    private GameViewModel gameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameViewModel = GameViewModel.getInstance();
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
                                gameViewModel.updateStats(1, 0);
                                gameViewModel.deleteScores();
                            }
                        }).show();
            }
        });

        ToggleButton toggleAudio = findViewById(R.id.toggle_audio);
        Log.e(getClass().getSimpleName(), String.valueOf(gameViewModel.getInfosMusic().getValue().mute));
        toggleAudio.setChecked(!gameViewModel.getInfosMusic().getValue().mute);
        //toggleAudio.setActivated();
        toggleAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameViewModel.inverseMuteMusic();
            }
        });
    }
}
