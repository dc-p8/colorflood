package com.example.dc.colorflood;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class LauncherAnimView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    volatile private int[] casesColors;
    volatile private int[][] cases;
    final private Random rnd;
    private final Handler animHandler;
    private final SurfaceHolder holder;
    private Thread thread;
    final private int size, nbColors;
    volatile private float caseWidth, caseHeight;

    public LauncherAnimView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.size = 10;
        this.nbColors = 5;
        this.rnd = new Random();
        this.init();
        this.animHandler = new Handler();
        this.holder = this.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void run() {
        Canvas canvas = holder.lockCanvas();
        if(canvas != null) {
            Paint p = new Paint();
            canvas.drawRGB(50, 50, 50);

            for(int i = 0; i < 10; i++) {
                float y = this.caseHeight * (float)i;
                for(int j = 0; j < 10; j++)
                {
                    float x = this.caseWidth * (float)j;
                    p.setColor(casesColors[this.cases[i][j]]);
                    canvas.drawRect(x, y, x + this.caseWidth, y + this.caseHeight, p);
                }
            }
            holder.unlockCanvasAndPost(canvas);
            this.update();
        }
        this.animHandler.postDelayed(this, 1000);
    }

    private void init(){
        this.cases = new int[this.size][this.size];
        this.casesColors = new int[this.nbColors];

        double part = 1.0 / (double)this.nbColors;
        for(int i = 0; i < nbColors; i++) {
            this.casesColors[i] = Color.HSVToColor(255, new float[]{(float)((double)i * part * 360.0), 0.5f, 1.0f});
        }
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                this.cases[i][j] = this.rnd.nextInt(nbColors);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.caseWidth = (float)width / (float)this.size;
        this.caseHeight = (float)height / (float)this.size;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                this.animHandler.removeCallbacks(this);
                this.thread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e("threadJoinFailed", e.getLocalizedMessage());
            }
        }
    }

    private void update(){
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                if (this.rnd.nextBoolean())
                    this.cases[i][j] = this.rnd.nextInt(nbColors);
            }
        }
    }
}
