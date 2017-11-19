package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;


import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class MyGame extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private final SurfaceHolder holder;
    volatile boolean running = false;
    Thread thread;
    volatile Level lvl;


    public MyGame(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.holder = getHolder();
        this.holder.addCallback(this);
        setWillNotDraw(false);
        this.lvl = new Level();

    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (running)
        {
            try {
                Thread.sleep(500);
                canvas = holder.lockCanvas();
                synchronized (holder) {
                        lvl.draw(canvas);
                }
            }
            catch (Exception e){
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
            finally {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
        running = true;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.lvl.setCaseWidth(getWidth() / this.lvl.getNbCasesWidth());
        this.lvl.setCaseHeight(getHeight() / this.lvl.getNbCasesHeight());
    }

    void initLevel(int nbCasesWidth, int nbCasesHeight, int nbColors){
        this.lvl.setNbCasesWidth(nbCasesWidth);
        this.lvl.setNbCasesHeight(nbCasesHeight);
        this.lvl.setCaseWidth(getWidth() / nbCasesWidth);
        this.lvl.setCaseHeight(getHeight() / nbCasesHeight);
        this.lvl.initLevel(nbColors);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try until it works
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
