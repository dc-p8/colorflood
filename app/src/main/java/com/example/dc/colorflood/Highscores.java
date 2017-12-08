package com.example.dc.colorflood;


import android.arch.lifecycle.Observer;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

/**
 * Activité qui gère les meilleures scores
 */
public class Highscores extends MusicActivity {
    private RecyclerView mRecyclerView;
    private Cursor scores;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

    public Highscores() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        GameViewModel.getInstance().getScores().observe(this, new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor data) {
                scores = data;
                mRecyclerView.setAdapter(new ScoreAdapter(scores));
            }
        });

        setContentView(R.layout.activity_highscores);
        ((TextView) findViewById(R.id.level_title)).setTypeface(null, Typeface.BOLD);
        ((TextView) findViewById(R.id.score_title)).setTypeface(null, Typeface.BOLD);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
