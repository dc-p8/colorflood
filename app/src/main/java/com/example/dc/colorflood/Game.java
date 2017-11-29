package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Game extends AppCompatActivity implements Runnable
{
    private ButtonBar colorsButtonsLayout;
    private GameView gameView;
    private TextView textTimer;
    private TextView textNbMoves;
    private TextView textExtraMoves;
    private TextView textCurrentLevel;
    private int lvlHeight = 10, lvlWidth = 10;
    private int nbColors = 5;
    private int maxNbMoves = 22;
    private Thread thread;
    private long timerFromResume;
    private Handler timerHandler;
    private long timerTotal = 0;
    private int extraTry = 0;
    private int currentLevel = 0;
    private StatsViewModel statsViewModel;


    public Game() {
        super();
    }

    @Override
    public void onBackPressed() {
        dialogBox(this);
    }

    private void dialogBox(Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Quitter le jeu ?")
                .setTitle("Votre progression sur cette partie sera perdue.")
                .setCancelable(false)
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setPositiveButton("Quitter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
        Log.d(getClass().getSimpleName(), "DIALOG");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(getClass().getSimpleName(), "RESUMED");
        timerFromResume = java.lang.System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "ONDESTROY");
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.timerTotal += (java.lang.System.currentTimeMillis() - timerFromResume);
        Log.e(getClass().getSimpleName(), "PAUSED");
        this.timerHandler.removeCallbacks(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.statsViewModel = StatsViewModel.getInstance();
        this.statsViewModel.getStats().observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> stats) {
                currentLevel = stats.first;
                extraTry = stats.second;

                textCurrentLevel.setText(String.valueOf("lvl:"+currentLevel));
                if(extraTry == 0)
                    textExtraMoves.setText("");
                else
                    textExtraMoves.setText('+'+String.valueOf(extraTry));
            }
        });

        setContentView(R.layout.activity_game);
        this.colorsButtonsLayout = findViewById(R.id.colors);
        this.gameView = findViewById(R.id.mygame);
        this.gameView.initLevel(this.lvlWidth, this.lvlHeight, this.nbColors, this.maxNbMoves);

        this.textTimer = findViewById(R.id.text_timer);
        this.textTimer.setText(String.valueOf(0));
        this.textNbMoves = findViewById(R.id.text_try);
        this.textNbMoves.setText(String.valueOf(this.gameView.lvl.getNbMoves()+"/"+this.gameView.lvl.getMaxNbMoves()));
        this.textExtraMoves = findViewById(R.id.text_extra);
        this.textExtraMoves.setText("");
        this.textCurrentLevel = findViewById(R.id.text_currentLevel);
        this.textCurrentLevel.setText(String.valueOf("lvl:"+currentLevel));

        this.gameView.lvl.setWinEventListener(new LevelOnPlay.OnWinEventListener() {
            public void onWin() {
                nextLevel();
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                r.getDisplayMetrics()
        );
        params.setMargins(px, px, px, px);

        this.colorsButtonsLayout.setParams(params);
        this.colorsButtonsLayout.setBtnCallback(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.lvl.play((int)v.getTag(R.id.button_number));
                gameView.update();
                textNbMoves.setText(String.valueOf(gameView.lvl.getNbMoves()+"/"+gameView.lvl.getMaxNbMoves()));
            }
        });

        this.colorsButtonsLayout.addButtons(this.gameView.lvl.getCasesColors());
        timerHandler = new Handler();
        thread = new Thread(this);
    }

    private void nextLevel(){
        this.statsViewModel.updateStats(this.currentLevel+1,
                this.extraTry+(this.gameView.lvl.getMaxNbMoves()-this.gameView.lvl.getNbMoves()));
        this.gameView.initLevel(this.lvlWidth, this.lvlHeight, this.nbColors, this.maxNbMoves);
        this.textNbMoves.setText(String.valueOf(this.gameView.lvl.getNbMoves()+"/"+this.gameView.lvl.getMaxNbMoves()));
        this.timerTotal = 0;
        timerFromResume = java.lang.System.currentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.gameView.lvl.saveState(outState);
        outState.putLong("timerFromResume", this.timerFromResume);
        outState.putLong("timerTotal", this.timerTotal);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.gameView.lvl.restoreState(savedInstanceState);
        this.timerFromResume = savedInstanceState.getLong("timerFromResume");
        this.timerTotal = savedInstanceState.getLong("timerTotal");
        textNbMoves.setText(String.valueOf(gameView.lvl.getNbMoves()+"/"+gameView.lvl.getMaxNbMoves()));
    }


    @Override
    public void run() {
        long delay = (java.lang.System.currentTimeMillis() - this.timerFromResume) + this.timerTotal;
        final long currenttime = delay / 1000;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(getClass().getSimpleName(), "running");
                textTimer.setText(String.valueOf(currenttime));
            }
        });
        this.timerHandler.postDelayed(this, 1000);
    }
}
