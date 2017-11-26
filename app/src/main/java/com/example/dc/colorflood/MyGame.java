package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MyGame extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private final SurfaceHolder holder;
    volatile boolean running = false;
    Thread thread;
    volatile Lock l;
    volatile Level lvl;


    public MyGame(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        holder.addCallback(this);
        setWillNotDraw(false);
        this.lvl = new Level();

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
                else
                {

                }
                first = false;

                Log.d("run", "after sem wait");
                canvas = holder.lockCanvas();
                if(canvas != null)
                    lvl.draw(canvas);

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




        this.lvl.setCaseWidth((float)getWidth() / (float)this.lvl.getNbCasesWidth());
        this.lvl.setCaseHeight((float)getHeight() / (float)this.lvl.getNbCasesHeight());

        if(thread == null || thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(this);
            thread.start();
        }

        running = true;
        update();

    }

    void initLevel(int nbCasesWidth, int nbCasesHeight, int nbColors){
        this.lvl.setNbCasesWidth(nbCasesWidth);
        this.lvl.setNbCasesHeight(nbCasesHeight);
        this.lvl.setCaseWidth((float)getWidth() / (float)nbCasesWidth);
        this.lvl.setCaseHeight((float)getHeight() / (float)nbCasesHeight);
        this.lvl.initLevel(nbColors);
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
}
