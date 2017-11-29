package com.example.dc.colorflood;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;

class LevelOnPlay {
    private Level lvl;
    private int nbMoves = 0;
    private OnWinEventListener winEventListener;

    public LevelOnPlay(){
        this.lvl = new Level();
    }

    public LevelOnPlay(int nbCasesWidth, int nbCasesHeight, int[][] cases, int[] casesColors, float caseWidth, float caseHeight, int maxNbMoves) {
        this.lvl = new Level(nbCasesWidth, nbCasesHeight, cases, casesColors, caseWidth, caseHeight, maxNbMoves);
    }

    public LevelOnPlay(LevelOnPlay lvl){
        this.lvl = new Level(lvl.lvl);
        this.nbMoves = lvl.nbMoves;
    }

    void setWinEventListener(OnWinEventListener eventListener) {
        this.winEventListener = eventListener;
    }

    int getNbCasesWidth() {
        return this.lvl.getNbCasesWidth();
    }

    void setNbCasesWidth(int nbCasesWidth) {
        this.lvl.setNbCasesWidth(nbCasesWidth);
    }

    int getNbCasesHeight() {
        return this.lvl.getNbCasesHeight();
    }

    void setNbCasesHeight(int nbCasesHeight) {
        this.lvl.setNbCasesHeight(nbCasesHeight);
    }

    int[][] getCases() {
        return this.lvl.getCases();
    }

    void setCases(int[][] cases) {
        this.lvl.setCases(cases);
    }

    int[] getCasesColors() {
        return this.lvl.getCasesColors();
    }

    void setCasesColors(int[] casesColors) {
        this.lvl.setCasesColors(casesColors);
    }

    float getCaseWidth() {
        return this.lvl.getCaseWidth();
    }

    void setCaseWidth(float caseWidth) {
        this.lvl.setCaseWidth(caseWidth);
    }

    float getCaseHeight() {
        return this.lvl.getCaseHeight();
    }

    void setCaseHeight(float caseHeight) {
        this.lvl.setCaseHeight(caseHeight);
    }

    int getMaxNbMoves() {
        return this.lvl.getMaxNbMoves();
    }

    void setMaxNbMoves(int maxNbMoves) {
        this.lvl.setMaxNbMoves(maxNbMoves);
    }

    int getNbMoves() {
        return nbMoves;
    }

    void draw(Canvas canvas) {
        Paint p = new Paint();

        canvas.drawRGB(50, 50, 50);


        Log.d(getClass().getName(),"" + lvl.getCaseWidth());
        Log.d(getClass().getName(), "" + lvl.getCaseHeight());


        for(int i = 0; i < lvl.getNbCasesHeight(); i++)
        {
            float y = lvl.getCaseHeight() * (float)i;
            for(int j = 0; j < lvl.getNbCasesWidth(); j++)
            {
                float x = lvl.getCaseWidth() * (float)j;
                p.setColor(lvl.getCasesColors()[lvl.getCases()[i][j]]);
                canvas.drawRect(x, y, x + lvl.getCaseWidth(), y + lvl.getCaseHeight(), p);
            }
        }
    }

    void play(int newColor) {
        this.nbMoves++;
        int nbCaseColored = 0;
        LinkedList<Pair<Integer,Integer>> changeFifo = new LinkedList<>();
        LinkedList<Pair<Integer,Integer>> winFifo = new LinkedList<>();
        HashSet<Pair<Integer,Integer>> checked = new HashSet<>();
        Pair<Integer,Integer> currentCase = new Pair<>(0,0);
        changeFifo.add(currentCase);
        checked.add(currentCase);
        int oldColor = this.lvl.getCases()[0][0];
        while(!changeFifo.isEmpty()){
            nbCaseColored++;
            currentCase = changeFifo.poll();
            this.lvl.setCase(currentCase.first, currentCase.second, newColor);
            for(int i = -1; i<2; i++){
                for (int j = -1; j<2 ; j++){
                    if(Math.abs(i)==Math.abs(j))
                        continue;
                    Pair<Integer,Integer> nextCase = new Pair<>(currentCase.first + i, currentCase.second + j);
                    if (nextCase.first < this.lvl.getNbCasesWidth() && nextCase.first >= 0
                            && nextCase.second < this.lvl.getNbCasesHeight() && nextCase.second >= 0) {
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
        if (this.lvl.getCases()[nextCase.first][nextCase.second] == color) {
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
                    if (nextCase.first < this.lvl.getNbCasesWidth() && nextCase.first >= 0
                            && nextCase.second < this.lvl.getNbCasesHeight() && nextCase.second >= 0) {
                        updateFifo(checked, winFifo, nextCase, winColor);
                    }
                }
            }
        }
        return nbCaseColored == this.lvl.getNbCasesHeight() * this.lvl.getNbCasesWidth();
    }


    void initLevel(int nbColors) {
        this.nbMoves = 0;
        this.lvl.init(nbColors);
    }

    void saveState(Bundle state) {
        this.lvl.saveState(state, "principal");
        state.putInt("nbMoves", this.nbMoves);
    }

    void restoreState(Bundle state) {
        this.lvl.restoreState(state, "principal");
        this.nbMoves = state.getInt("nbMoves");
    }

    public interface OnWinEventListener {
        void onWin();
    }
}
