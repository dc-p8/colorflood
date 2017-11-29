package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;


import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private final SurfaceHolder holder;
    volatile boolean running = false;
    Thread thread;
    volatile LevelOnPlay lvl;
    volatile private float caseWidth, caseHeight;


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
                if(!first)
                {
                    synchronized (this)
                    {
                        wait();
                    }

                }
                first = false;

                Log.d("run", "after sem wait");
                canvas = holder.lockCanvas();
                if(canvas != null)
                    drawLevel(canvas);

            }
            catch (Exception e){
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
            finally {
                if(canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void update()
    {
        Log.d("Update", "calling update");
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SufaceCreated", "SurfaceCreated");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(getClass().getSimpleName(), "SurfaceChanged");

        if(thread == null)
            Log.d(getClass().getSimpleName(), "thread null");
        else
        {
            Log.d(getClass().getSimpleName(), "thread non null");
            Log.d("surfacechanged", thread.getState().name());
        }

        this.caseWidth = (float)getWidth() / (float)this.lvl.getNbCasesWidth();
        this.caseHeight = (float)getHeight() / (float)this.lvl.getNbCasesHeight();

        if(thread == null || thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(this);
            thread.start();
        }

        running = true;
        update();

    }

    void initLevel(int nbCasesWidth, int nbCasesHeight, int nbColors, int maxNbCount){
        this.caseWidth = (float)getWidth() / (float)nbCasesWidth;
        this.caseHeight = (float)getHeight() / (float)nbCasesHeight;
        this.lvl.setNbCasesWidth(nbCasesWidth);
        this.lvl.setNbCasesHeight(nbCasesHeight);
        this.lvl.setMaxNbMoves(maxNbCount);
        this.lvl.initLevel(nbColors);
    }

    void drawLevel(Canvas canvas) {
        Paint p = new Paint();

        canvas.drawRGB(50, 50, 50);


        Log.d(getClass().getName(),"" + this.caseWidth);
        Log.d(getClass().getName(), "" + this.caseHeight);


        for(int i = 0; i < lvl.getNbCasesHeight(); i++)
        {
            float y = this.caseHeight * (float)i;
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
        Log.d("surfacedestroyed", "SurfaceDestroyed");
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
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    void saveState(Bundle state) {
        state.putFloat("caseWidth", this.caseWidth);
        state.putFloat("caseHeight", this.caseHeight);
        this.lvl.saveState(state);
    }

    void restoreState(Bundle state) {
        this.caseWidth = state.getFloat("caseWidth");
        this.caseHeight = state.getFloat("caseHeight");
        this.lvl.restoreState(state);
    }
}
