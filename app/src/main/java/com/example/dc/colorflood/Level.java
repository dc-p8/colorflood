package com.example.dc.colorflood;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

class Level {

    private int nbCasesWidth;
    private int nbCasesHeight;
    private int[][] cases;
    private int[] casesColors;
    private int caseWidth, caseHeight;
    private final static Random random = new Random();

    Level(){}

    Level(int nbCasesWidth, int nbCasesHeight, int[][] cases, int[] casesColors, int caseWidth, int caseHeight) {
        this.nbCasesWidth = nbCasesWidth;
        this.nbCasesHeight = nbCasesHeight;
        this.cases = cases;
        this.casesColors = casesColors;
        this.caseWidth = caseWidth;
        this.caseHeight = caseHeight;
    }

    int getNbCasesWidth() {
        return nbCasesWidth;
    }

    void setNbCasesWidth(int nbCasesWidth) {
        this.nbCasesWidth = nbCasesWidth;
    }

    int getNbCasesHeight() {
        return nbCasesHeight;
    }

    void setNbCasesHeight(int nbCasesHeight) {
        this.nbCasesHeight = nbCasesHeight;
    }

    int[][] getCases() {
        return cases;
    }

    void setCases(int[][] cases) {
        this.cases = cases;
    }

    int[] getCasesColors() {
        return casesColors;
    }

    void setCasesColors(int[] casesColors) {
        this.casesColors = casesColors;
    }

    int getCaseWidth() {
        return caseWidth;
    }

    void setCaseWidth(int caseWidth) {
        this.caseWidth = caseWidth;
    }

    int getCaseHeight() {
        return caseHeight;
    }

    void setCaseHeight(int caseHeight) {
        this.caseHeight = caseHeight;
    }

    void draw(Canvas canvas) {
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

    void changeColor(int newColor) {
        this.cases[0][0] = newColor;
    }


    void initLevel(int nbColors) {
        this.cases = new int[this.nbCasesHeight][this.nbCasesWidth];
        this.casesColors = new int[nbColors];

        double part = 1.0 / (double)nbColors;
        //Log.d(getClass().getSimpleName(), "Part : " + part);
        for(int i = 0; i < nbColors; i++)
        {
            this.casesColors[i] = Color.HSVToColor(255, new float[]{(float)((double)i * part * 360.0), 0.5f, 1.0f});
            //Log.e(getClass().getSimpleName(), "" + Color.red(colors[i]) + ", " + Color.green(colors[i]) + ", " + Color.blue(colors[i]) );
        }
        for(int i = 0; i < this.nbCasesHeight; i++)
        {
            for(int j = 0; j < this.nbCasesWidth; j++)
            {
                this.cases[i][j] = Level.random.nextInt(nbColors);
            }
        }
    }
}
