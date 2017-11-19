package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;


import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.Semaphore;


public class MyGame extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private final SurfaceHolder holder;
    volatile boolean running = false;
    Thread thread;
    Semaphore semaphore;
    volatile Level lvl;


    public MyGame(Context context, AttributeSet attrs) {
        super(context, attrs);

        semaphore = new Semaphore(0);
        holder = getHolder();
        holder.addCallback(this);
        setWillNotDraw(false);
        this.lvl = new Level();

    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (running)
        {
            try {
                semaphore.acquire();
                Log.d(getClass().getSimpleName(), "after sem wait");
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

    public void update()
    {
        semaphore.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(getClass().getSimpleName(), "SurfaceCreated");


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(getClass().getSimpleName(), "SurfaceChanged");
        this.lvl.setCaseWidth(getWidth() / this.lvl.getNbCasesWidth());
        this.lvl.setCaseHeight(getHeight() / this.lvl.getNbCasesHeight());
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
        running = true;

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
        Log.d(getClass().getSimpleName(), "SurfaceDestroyed");
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
