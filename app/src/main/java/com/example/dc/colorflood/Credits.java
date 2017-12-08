package com.example.dc.colorflood;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

/**
 * Affiche l'à propos du jeu
 */
public class Credits extends MusicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_credits);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

}
