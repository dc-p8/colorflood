package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;


import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.concurrent.Semaphore;


public class MyGame extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private final SurfaceHolder holder;
    volatile boolean running = false;
    Thread thread;
    Random random;
    Semaphore semaphore;
    public int nbCasesWidth;
    public int nbCasesHeight;
    public int[][] cases;
    public int nbColors;
    public int[] casesColors;

    volatile int caseWidth, caseHeight;

    public MyGame(Context context, AttributeSet attrs) {
        super(context, attrs);

        semaphore = new Semaphore(0);
        holder = getHolder();
        holder.addCallback(this);
        setWillNotDraw(false);

    }

    public void setLevel(int[][] cases, int w, int h, int[] colors)
    {
        this.cases = cases;
        this.nbCasesWidth = w;
        this.nbCasesHeight = h;
        this.casesColors = colors;
    }

    void nDraw(Canvas canvas)
    {
        Paint p = new Paint();

        canvas.drawRGB(50, 50, 50);



        float caseheight = (float)getHeight() / (float)nbCasesHeight;
        float casewidth =  (float)getWidth() / (float)nbCasesWidth;
        /*
        Log.d(getClass().getName(),"" + casewidth + " " + (float)nbCasesWidth);
        Log.d(getClass().getName(), "" + caseheight);
        */

        for(int i = 0; i < nbCasesHeight; i++)
        {
            float y = caseheight * i;
            for(int j = 0; j < nbCasesWidth; j++)
            {
                float x = casewidth * j;
                p.setColor(casesColors[cases[i][j]]);
                canvas.drawRect(x, y, x + casewidth, y + caseheight, p);//new Rect(x, y, x + caseWidth, y + caseHeight), p);
            }
        }
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
                if(canvas != null)
                {
                    synchronized (holder) {
                        nDraw(canvas);
                    }

                }
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
        semaphore.release();

        /*
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if(canvas != null)
            {
                nDraw(canvas);
            }
        }
        catch (Exception e){
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
        finally {
            if(canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
        */
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(getClass().getSimpleName(), "SurfaceCreated");


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(getClass().getSimpleName(), "SurfaceChanged");
        caseWidth = getWidth() / nbCasesWidth;
        caseHeight = getHeight() / nbCasesHeight;
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
        running = true;
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
                // try until work
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
