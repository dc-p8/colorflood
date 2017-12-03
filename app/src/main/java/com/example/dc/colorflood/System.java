package com.example.dc.colorflood;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
                        .setTitle(R.string.dialog_system_reinit_title)
                        .setMessage(R.string.dialog_system_reinit_warning)
                        .setCancelable(false)
                        .setNegativeButton(R.string.dialog_system_reinit_disagree, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setPositiveButton(R.string.dialog_system_reinit_agree, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gameViewModel.updateStats(1, 0);
                                gameViewModel.deleteScores();
                            }
                        }).show();
            }
        });

        ((Button)findViewById(R.id.leave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(System.this, Launcher.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit", true);
                startActivity(intent);
                finish();
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
