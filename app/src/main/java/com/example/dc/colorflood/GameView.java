package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;


import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Vue qui permet d'afficher le plateau de jeu
 */
public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private final SurfaceHolder holder;
    volatile private boolean running = false;
    private Thread thread;
    final LevelOnPlay lvl;
    private float caseWidth, caseHeight;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        holder.addCallback(this);
        setWillNotDraw(false);
        this.lvl = new LevelOnPlay();

    }

    @Override
    public void run() {
        Canvas canvas = null;
        boolean first = true;
        while (running)
        {
            try {
                // Le thread ne doit pas s'endormir si on vient de le démarrer
                if(!first)
                {
                    // On est obligé de synchroniser le thread si on veut pouvoir le réveiller et l'endormir
                    synchronized (this)
                    {
                        // Le thread dort tant qu'il n'y a rien à afficher
                        wait();
                    }

                }
                first = false;
                canvas = holder.lockCanvas();
                if(canvas != null)
                    drawLevel(canvas);

            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if(canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void update()
    {
        // On est obligé de synchroniser le thread si on veut pouvoir le réveiller et l'endormir
        synchronized (this) {
            // On notifie le thread pour qu'il mette à jour l'affichage
            notify();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(getClass().getSimpleName(), "SurfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(getClass().getSimpleName(), "SurfaceChanged");

        if(thread == null)
            Log.d(getClass().getSimpleName(), "thread null");
        else
        {
            Log.d(getClass().getSimpleName(), "SurfaceChanged : thread state : " + thread.getState().name());
        }

        this.caseWidth = (float)width / (float)this.lvl.getNbCasesWidth();
        this.caseHeight = (float)height / (float)this.lvl.getNbCasesHeight();

        running = true;

        if(thread == null || thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(this);
            thread.start();
        } else {
            update();
        }
    }

    void initLevel(int nbCasesWidth, int nbCasesHeight, int nbColors, int maxNbCount){
        this.lvl.setNbCasesWidth(nbCasesWidth);
        this.lvl.setNbCasesHeight(nbCasesHeight);
        this.lvl.setMaxNbMoves(maxNbCount);
        this.lvl.initLevel(nbColors);
    }

    private void drawLevel(Canvas canvas) {
        Paint p = new Paint();

        canvas.drawRGB(50, 50, 50);


        Log.d(getClass().getSimpleName(),"" + this.caseWidth);
        Log.d(getClass().getSimpleName(), "" + this.caseHeight);


        for(int i = 0; i < lvl.getNbCasesHeight(); i++)
        {
            float y = this.caseHeight * (float)i; // Tout l'affichage se fait en flottant pour éviter les bandes noires
            for(int j = 0; j < lvl.getNbCasesWidth(); j++)
            {
                float x = this.caseWidth * (float)j;
                p.setColor(lvl.getCasesColors()[lvl.getCases()[i][j]]);
                canvas.drawRect(x, y, x + this.caseWidth, y + this.caseHeight, p);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(getClass().getSimpleName(), "SurfaceDestroyed");
        running = false;
        boolean retry = true;
        while (retry) {
            try {
                synchronized (this)
                {
                    notifyAll();
                }

                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try until it works
                e.printStackTrace();
            }
        }
    }

    void saveState(Bundle state) {
        this.lvl.saveState(state);
    }

    void restoreState(Bundle state) {
        this.lvl.restoreState(state);
    }
}
