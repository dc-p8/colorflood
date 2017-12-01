package com.example.dc.colorflood;


import android.arch.lifecycle.Observer;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class Highscores extends MusicActivity {
    RecyclerView mRecyclerView;
    Cursor scores;

    public Highscores() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scores != null && !scores.isClosed())
            scores.close();
    }
}
