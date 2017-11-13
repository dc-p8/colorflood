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


public class MyGame extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private final SurfaceHolder holder;
    volatile boolean running = false;
    Thread thread;
    Random random;

    public int nbCasesWidth;
    public int nbCasesHeight;
    public int[][] cases;
    public int nbColors;
    public int[] casesColors;

    volatile int caseWidth, caseHeight;

    public MyGame(Context context, AttributeSet attrs) {
        super(context, attrs);


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

        Log.d(getClass().getName(),"" + caseWidth);
        Log.d(getClass().getName(), "" + caseHeight);

        for(int i = 0; i < nbCasesHeight; i++)
        {
            int y = caseHeight * i;
            for(int j = 0; j < nbCasesWidth; j++)
            {
                int x = caseWidth * j;
                p.setColor(casesColors[cases[i][j]]);
                canvas.drawRect(new Rect(x, y, x + caseWidth, y + caseHeight), p);
            }
        }
    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (running)
        {
            try {
                Thread.sleep(500);
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

//    public void update()
//    {
//        Canvas canvas = null;
//        try {
//            canvas = holder.lockCanvas();
//            if(canvas != null)
//            {
//                nDraw(canvas);
//            }
//        }
//        catch (Exception e){
//            Log.e(getClass().getSimpleName(), e.getMessage());
//        }
//        finally {
//            if(canvas != null)
//                holder.unlockCanvasAndPost(canvas);
//        }
//    }

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
        caseWidth = getWidth() / nbCasesWidth;
        caseHeight = getHeight() / nbCasesHeight;
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
                // try until work
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
