package com.example.dc.colorflood;


import android.arch.lifecycle.Observer;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class Highscores extends MusicActivity {
    ScoreAdapter mAdapter;
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
                mAdapter.setData(data);
            }
        });

        setContentView(R.layout.activity_highscores);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mAdapter = new ScoreAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scores != null && !scores.isClosed())
            scores.close();
    }
}
