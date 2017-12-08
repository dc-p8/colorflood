package com.example.dc.colorflood;

import android.os.Bundle;

/**
 * Affiche l'à propos du jeu
 */
public class Credits extends MusicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_credits);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

}
