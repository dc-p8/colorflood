package com.example.dc.colorflood;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.Os;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Game extends AppCompatActivity implements Runnable
{
    ButtonBar colorsButtonsLayout;
    MyGame myGame;
    TextView text_timer;
    int lvlHeight = 10, lvlWidth = 10;
    int nbColors = 5;
    private long timer_from_resume;
    private Handler timerHandler;
    private Thread thread;
    private long timer_total = 0;


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
        timer_from_resume = java.lang.System.currentTimeMillis();
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
        timer_total += (java.lang.System.currentTimeMillis() - timer_from_resume);
        Log.e(getClass().getSimpleName(), "PAUSED");
        timerHandler.removeCallbacks(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.colorsButtonsLayout = findViewById(R.id.colors);
        this.myGame = findViewById(R.id.mygame);
        this.text_timer = findViewById(R.id.text_timer);
        this.myGame.initLevel(this.lvlWidth, this.lvlHeight, this.nbColors);
        this.myGame.lvl.setWinEventListener(new Level.OnWinEventListener() {
            public void onWin() {
                myGame.initLevel(lvlWidth, lvlHeight, nbColors);
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
                myGame.lvl.play((int)v.getTag(R.id.button_number));
                myGame.update();
            }
        });

        this.colorsButtonsLayout.addButtons(this.myGame.lvl.getCasesColors());
        timerHandler = new Handler();
        thread = new Thread(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.myGame.lvl.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.myGame.lvl.restoreState(savedInstanceState);
    }


    @Override
    public void run() {
        long delay = (java.lang.System.currentTimeMillis() - timer_from_resume) + timer_total;
        final long currenttime = delay / 1000;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(getClass().getSimpleName(), "running");
                text_timer.setText(String.valueOf(currenttime));
            }
        });
        timerHandler.postDelayed(this, 1000);
    }
}
