package com.example.dc.colorflood;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Game extends MusicActivity implements Runnable
{
    private ButtonBar colorsButtonsLayout;
    private GameView gameView;
    private TextView textTimer;
    private TextView textNbMoves;
    private TextView textExtraMoves;
    private TextView textCurrentLevel;
    private Thread thread;
    private long timerFromResume;
    private Handler timerHandler;
    private long timerTotal = 0;
    private int lastPressed;
    private GameViewModel gameViewModel;


    public Game() {
        super();
    }

    @Override
    public void onBackPressed() {
        dialogBox(this);
    }

    private void dialogBox(Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.dialog_quit_message)
                .setTitle(R.string.dialog_quit_warning)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_quit_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setPositiveButton(R.string.dialog_quit_agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        timerFromResume = java.lang.System.currentTimeMillis();
        if(thread != null)
        {
            Thread.State state = thread.getState();
        }

        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.timerTotal += (java.lang.System.currentTimeMillis() - timerFromResume);
        this.timerHandler.removeCallbacks(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        this.gameView = findViewById(R.id.mygame);
        this.gameViewModel = GameViewModel.getInstance();
        this.colorsButtonsLayout = findViewById(R.id.colors);

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
                int pressed = (int)v.getTag(R.id.button_number);
                if (pressed != lastPressed){
                    makeASound();
                    lastPressed = pressed;
                    gameView.lvl.play(pressed);
                    gameView.update();
                    textNbMoves.setText(getString(R.string.nb_moves_left, gameView.lvl.getNbMoves(), gameView.lvl.getMaxNbMoves()));
                }
            }
        });

        this.textTimer = findViewById(R.id.text_timer);
        this.textTimer.setText(String.valueOf(0));
        this.textNbMoves = findViewById(R.id.text_try);
        this.textExtraMoves = findViewById(R.id.text_extra);
        this.textExtraMoves.setText("");
        this.textCurrentLevel = findViewById(R.id.text_currentLevel);
        this.textCurrentLevel.setText(getString(R.string.lvl_on_game, gameView.lvl.getCurrentLevel()));

        this.gameViewModel.getStats().observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> stats) {
                boolean init = gameView.lvl.getCurrentLevel() != stats.first;
                gameView.lvl.setCurrentLevel(stats.first);
                gameView.lvl.setExtraMoves(stats.second);

                textCurrentLevel.setText(getString(R.string.lvl_on_game, gameView.lvl.getCurrentLevel()));
                if(gameView.lvl.getExtraMoves() == 0)
                    textExtraMoves.setText("");
                else
                    textExtraMoves.setText(getString(R.string.extra_try_prefix, gameView.lvl.getExtraMoves()));
                if(init) {
                    initLevel(gameView.lvl.getCurrentLevel());
                    lastPressed = gameView.lvl.getStartingCase();
                    textNbMoves.setText(getString(R.string.nb_moves_left, gameView.lvl.getNbMoves(), gameView.lvl.getMaxNbMoves()));
                }
            }
        });

        this.gameView.lvl.setOnLevelEventListener(new LevelOnPlay.OnLevelEventListener() {
            public void onWin() {
                timerHandler.removeCallbacks(Game.this);
                new AlertDialog.Builder(Game.this)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_win_title)
                        .setMessage(getString(R.string.dialog_win_msg, gameView.lvl.getCurrentLevel()+1))
                        .setPositiveButton(R.string.dialog_winlose_agree, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                gameViewModel.updateScores(gameView.lvl.getCurrentLevel(), Long.parseLong(textTimer.getText().toString()));
                                nextLevel();
                                thread.start();
                            }
                        })
                        .create()
                        .show();

            }
            public void onLose() {
                timerHandler.removeCallbacks(Game.this);
                new AlertDialog.Builder(Game.this)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_lose_title)
                        .setPositiveButton(R.string.dialog_winlose_agree, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                restartLevel();
                                thread.start();
                            }
                        })
                        .create()
                        .show();

            }
            public void decExtraMoves(){
                gameViewModel.updateStats(gameView.lvl.getCurrentLevel(), gameView.lvl.getExtraMoves()-1);
            }
        });

        timerHandler = new Handler();

    }

    private void nextLevel(){
        this.gameViewModel.updateStats(this.gameView.lvl.getCurrentLevel()+1,
                this.gameView.lvl.getExtraMoves()+(this.gameView.lvl.getMaxNbMoves()-this.gameView.lvl.getNbMoves()));
        this.startLevel();
    }

    private void restartLevel(){
        this.gameView.lvl.restart();
        this.lastPressed = this.gameView.lvl.getStartingCase();
        this.startLevel();
    }

    private void startLevel(){
        this.textNbMoves.setText(getString(R.string.nb_moves_left, gameView.lvl.getNbMoves(), gameView.lvl.getMaxNbMoves()));
        this.timerTotal = 0;
        this.timerFromResume = java.lang.System.currentTimeMillis();
        this.gameView.update();
    }

    private void initLevel(int currentLevel){
        int size = 3+currentLevel/7;
        int nbColors = 3+((currentLevel/15)%12);
        int maxNbMoves = 10+(currentLevel/5);
        this.gameView.initLevel(size, size, nbColors, maxNbMoves);
        if (nbColors != colorsButtonsLayout.getLength()){
            colorsButtonsLayout.initButtons(gameView.lvl.getCasesColors());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.gameView.saveState(outState);
        outState.putLong("timerFromResume", this.timerFromResume);
        outState.putLong("timerTotal", this.timerTotal);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.gameView.restoreState(savedInstanceState);
        this.timerFromResume = savedInstanceState.getLong("timerFromResume");
        this.timerTotal = savedInstanceState.getLong("timerTotal");
        this.textNbMoves.setText(getString(R.string.nb_moves_left, gameView.lvl.getNbMoves(), gameView.lvl.getMaxNbMoves()));
    }


    @Override
    public void run() {
        final long currentTime = this.getCurrentTime();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textTimer.setText(String.valueOf(currentTime));
            }
        });
        this.timerHandler.postDelayed(this, 1000);
    }

    private long getCurrentTime(){
        final long delay = (java.lang.System.currentTimeMillis() - this.timerFromResume) + this.timerTotal;
        return delay / 1000;
    }
}
