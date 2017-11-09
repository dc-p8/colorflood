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

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Dodo on 09/11/2017.
 */

public class MyGame extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    private SurfaceHolder holder;
    int w = 5, h = 5;
    boolean running = false;
    Thread t;
    Random random;



    public MyGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        setWillNotDraw(false);
        random = new Random();
    }

    void nDraw(Canvas canvas)
    {
        Paint p = new Paint();

        canvas.drawRGB(50, 50, 50);
        int px = getWidth() / w;
        Log.d(getClass().getName(),"" + px);
        int py = getHeight() / h;
        Log.d(getClass().getName(), "" + py);
        for(int i = 0; i < w; i++)
        {
            int x = px * i;
            for(int j = 0; j < h; j++)
            {
                int y = py * j;
                p.setColor(Color.rgb(random.nextInt(), random.nextInt(), random.nextInt()));
                canvas.drawRect(new Rect(x, y, x +px, y + py), p);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(t == null)
        {
            t = new Thread(this);
            running = true;
            t.start();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        try{
            t.join();
        }
        catch (Exception e){
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
    }
}
