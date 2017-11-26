package com.example.dc.colorflood;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

class Level {

    private int nbCasesWidth;
    private int nbCasesHeight;
    private int[][] cases;
    private int[] casesColors;
    private int caseWidth, caseHeight;
    private final static Random random = new Random();
    private OnWinEventListener winEventListener;

    Level(){}

    Level(int nbCasesWidth, int nbCasesHeight, int[][] cases, int[] casesColors, int caseWidth, int caseHeight) {
        this.nbCasesWidth = nbCasesWidth;
        this.nbCasesHeight = nbCasesHeight;
        this.cases = cases;
        this.casesColors = casesColors;
        this.caseWidth = caseWidth;
        this.caseHeight = caseHeight;
    }

    Level(Level lvl){
        this.nbCasesWidth = lvl.nbCasesWidth;
        this.nbCasesHeight = lvl.nbCasesHeight;
        this.cases = new int[lvl.nbCasesWidth][lvl.nbCasesHeight];
        for(int i=0 ; i < lvl.nbCasesWidth ; i++)
            this.cases[i] = lvl.cases[i].clone();
        this.casesColors = lvl.casesColors.clone();
        this.caseWidth = lvl.caseWidth;
        this.caseHeight = lvl.caseHeight;
    }

    void setWinEventListener(OnWinEventListener eventListener) {
        this.winEventListener = eventListener;
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

    void play(int newColor) {
        int nbCaseColored = 0;
        LinkedList<Pair<Integer,Integer>> changeFifo = new LinkedList<>();
        LinkedList<Pair<Integer,Integer>> winFifo = new LinkedList<>();
        HashSet<Pair<Integer,Integer>> checked = new HashSet<>();
        Pair<Integer,Integer> currentCase = new Pair<>(0,0);
        changeFifo.add(currentCase);
        checked.add(currentCase);
        int oldColor = this.cases[0][0];
        while(!changeFifo.isEmpty()){
            nbCaseColored++;
            currentCase = changeFifo.poll();
            this.cases[currentCase.first][currentCase.second] = newColor;
            for(int i = -1; i<2; i++){
                for (int j = -1; j<2 ; j++){
                    if(Math.abs(i)==Math.abs(j))
                        continue;
                    Pair<Integer,Integer> nextCase = new Pair<>(currentCase.first + i, currentCase.second + j);
                    if (nextCase.first < this.nbCasesWidth && nextCase.first >= 0
                            && nextCase.second < this.nbCasesHeight && nextCase.second >= 0) {
                        updateFifo(checked, changeFifo, nextCase, oldColor);
                        updateFifo(checked, winFifo, nextCase, newColor);
                    }
                }
            }
        }
        if (hasWon(nbCaseColored, winFifo, newColor, checked))
            this.win();
    }

    private void updateFifo(HashSet<Pair<Integer,Integer>> checked, LinkedList<Pair<Integer,Integer>> fifo, Pair<Integer,Integer> nextCase, int color){
        if (this.cases[nextCase.first][nextCase.second] == color) {
            if (!checked.contains(nextCase)) {
                fifo.add(nextCase);
                checked.add(nextCase);
            }
        }
    }

    private void win(){
        this.winEventListener.onWin();
    }

    private boolean hasWon(int nbCaseColored, LinkedList<Pair<Integer,Integer>> winFifo, int winColor, HashSet<Pair<Integer,Integer>> checked) {
        while(!winFifo.isEmpty()){
            nbCaseColored++;
            Pair<Integer,Integer> currentCase = winFifo.poll();
            for(int i = -1; i<2; i++) {
                for (int j = -1; j < 2; j++) {
                    if(Math.abs(i)==Math.abs(j))
                        continue;
                    Pair<Integer, Integer> nextCase = new Pair<>(currentCase.first + i, currentCase.second + j);
                    if (nextCase.first < this.nbCasesWidth && nextCase.first >= 0
                            && nextCase.second < this.nbCasesHeight && nextCase.second >= 0) {
                        updateFifo(checked, winFifo, nextCase, winColor);
                    }
                }
            }
        }
        return nbCaseColored == this.nbCasesHeight * this.nbCasesWidth;
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

    void saveState(Bundle state) {
        state.putInt("nbCasesWidth", this.nbCasesWidth);
        state.putInt("nbCasesHeight", this.nbCasesHeight);
        state.putInt("caseWidth", this.caseWidth);
        state.putInt("caseHeight", this.caseHeight);
        state.putIntArray("casesColors", this.casesColors);
        for(int i = 0 ; i < this.nbCasesWidth ; i++) {
            state.putIntArray("cases" + i, this.cases[i]);
        }
    }

    void restoreState(Bundle state) {
        this.nbCasesWidth = state.getInt("nbCasesWidth");
        this.nbCasesHeight = state.getInt("nbCasesHeight");
        this.caseWidth = state.getInt("caseWidth");
        this.caseHeight = state.getInt("caseHeight");
        this.casesColors = state.getIntArray("casesColors");
        this.cases = new int[this.nbCasesWidth][this.nbCasesHeight];
        for(int i=0 ; i < this.nbCasesWidth ; i++)
            this.cases[i] = state.getIntArray("cases"+i);
    }

    public interface OnWinEventListener {
        void onWin();
    }
}
