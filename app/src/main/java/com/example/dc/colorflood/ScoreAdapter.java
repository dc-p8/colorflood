package com.example.dc.colorflood;

import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private Cursor data;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView levelView;
        private TextView scoreView;

        ViewHolder(LinearLayout v) {
            super(v);
            levelView = (TextView) v.findViewById(R.id.level);
            scoreView = (TextView) v.findViewById(R.id.score);
            levelView.setTypeface(null, Typeface.BOLD);
            levelView.setText("Chargement...");
        }

        void bind(Score score){
            levelView.setText("Niveau :"+score.lvl);
            levelView.setText(String.valueOf(score.score));
        }
    }

    ScoreAdapter(Cursor scores) {
        this.data = scores;
    }

    void setData(Cursor scores){
        this.data = scores;
    }

    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (data == null || data.isClosed())
            return;
        data.moveToPosition(position);
        holder.bind(new Score(data.getInt(data.getColumnIndex(ScoresDatabaseManager.ID_COLUMN)),
                data.getInt(data.getColumnIndex(ScoresDatabaseManager.TIME_TAKEN_COLUMN))));

    }

    @Override
    public int getItemCount() {
        if (data == null || data.isClosed())
            return 0;
        return data.getCount();
    }

    class Score{
        int lvl;
        long score;

        Score(int lvl, long score) {
            this.lvl = lvl;
            this.score = score;
        }
    }
}