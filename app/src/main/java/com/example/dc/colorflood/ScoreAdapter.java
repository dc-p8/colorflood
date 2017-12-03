package com.example.dc.colorflood;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    final private Cursor data;

    static class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView levelView;
        final private TextView scoreView;

        ViewHolder(LinearLayout v) {
            super(v);
            levelView = v.findViewById(R.id.level);
            scoreView = v.findViewById(R.id.score);
        }

        void bind(Score score){
            levelView.setText(String.valueOf(score.lvl));
            scoreView.setText(prettifyTime(score.score));
        }

        String prettifyTime(long time){
            long minutes = time / 60;
            final long seconds = time % 60;
            final long hours = minutes / 60;
            minutes %= 60;
            return (hours != 0 ? hours + "h" : "") +
                    (minutes != 0 ? minutes + "m" : "") +
                    seconds + "s";
        }
    }

    ScoreAdapter(Cursor scores) {
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
        final int lvl;
        final long score;

        Score(int lvl, long score) {
            this.lvl = lvl;
            this.score = score;
        }
    }
}