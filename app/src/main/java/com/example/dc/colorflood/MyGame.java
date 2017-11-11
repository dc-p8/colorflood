package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import java.io.File;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Dodo on 09/11/2017.
 */

public class MyGame extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private SurfaceHolder holder;
    boolean running = false;
    Thread t;
    Random random;

    public int cases_w;
    public int cases_h;
    public int[][] cases;
    public int nb_colors;
    public int[] cases_colors;

    int px, py;

    public MyGame(Context context, AttributeSet attrs) {
        super(context, attrs);


        holder = getHolder();
        holder.addCallback(this);
        setWillNotDraw(false);

    }

    public void setLevel(int[][] cases, int w, int h, int[] colors)
    {
        this.cases = cases;
        this.cases_w = w;
        this.cases_h = h;
        this.cases_colors = colors;
    }

    void nDraw(Canvas canvas)
    {
        Paint p = new Paint();

        canvas.drawRGB(50, 50, 50);

        Log.d(getClass().getName(),"" + px);
        Log.d(getClass().getName(), "" + py);

        for(int i = 0; i < cases_h; i++)
        {
            int y = py * i;
            for(int j = 0; j < cases_w; j++)
            {
                int x = px * j;
                p.setColor(cases_colors[cases[i][j]]);
                canvas.drawRect(new Rect(x, y, x + px, y + py), p);
            }
        }
    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (running)
        {
            try {
                t.sleep(1000);
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
        }

    }

    public void update()
    {
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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(t == null)
        {
            //t = new Thread(this);
            //running = true;
            //t.start();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        px = getWidth() / cases_w;
        py = getHeight() / cases_h;
        update();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        /*
        running = false;
        try{
            t.join();
        }
        catch (Exception e){
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
        */
    }
}
