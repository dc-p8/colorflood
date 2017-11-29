package com.example.dc.colorflood;


import android.graphics.Color;
import android.os.Bundle;

import java.util.Random;

public class Level {

    private int nbCasesWidth;
    private int nbCasesHeight;
    private int[][] cases;
    private int[] casesColors;
    private float caseWidth, caseHeight;
    private int maxNbMoves;
    private final static Random random = new Random();

    public Level(){}

    public Level(int nbCasesWidth, int nbCasesHeight, int[][] cases, int[] casesColors, float caseWidth, float caseHeight, int maxNbMoves) {
        this.nbCasesWidth = nbCasesWidth;
        this.nbCasesHeight = nbCasesHeight;
        this.cases = cases;
        this.casesColors = casesColors;
        this.caseWidth = caseWidth;
        this.caseHeight = caseHeight;
        this.maxNbMoves = maxNbMoves;
    }

    public Level(Level lvl){
        this.nbCasesWidth = lvl.nbCasesWidth;
        this.nbCasesHeight = lvl.nbCasesHeight;
        this.cases = new int[lvl.nbCasesWidth][lvl.nbCasesHeight];
        for(int i=0 ; i < lvl.nbCasesWidth ; i++)
            this.cases[i] = lvl.cases[i].clone();
        this.casesColors = lvl.casesColors.clone();
        this.caseWidth = lvl.caseWidth;
        this.caseHeight = lvl.caseHeight;
        this.maxNbMoves = lvl.maxNbMoves;
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

    void setCases(int x, int[] row) {
        this.cases[x] = row;
    }

    void setCase(int x, int y, int value) {
        this.cases[x][y] = value;
    }

    int[] getCasesColors() {
        return casesColors;
    }

    void setCasesColors(int[] casesColors) {
        this.casesColors = casesColors;
    }

    float getCaseWidth() {
        return caseWidth;
    }

    void setCaseWidth(float caseWidth) {
        this.caseWidth = caseWidth;
    }

    float getCaseHeight() {
        return caseHeight;
    }

    void setCaseHeight(float caseHeight) {
        this.caseHeight = caseHeight;
    }

    int getMaxNbMoves() {
        return maxNbMoves;
    }

    void setMaxNbMoves(int maxNbMoves) {
        this.maxNbMoves = maxNbMoves;
    }

    void init(int nbColors) {
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

    void saveState(Bundle state, String id) {
        state.putInt(id+"nbCasesWidth", this.nbCasesWidth);
        state.putInt(id+"nbCasesHeight", this.nbCasesHeight);
        state.putFloat(id+"caseWidth", this.caseWidth);
        state.putFloat(id+"caseHeight", this.caseHeight);
        state.putIntArray(id+"casesColors", this.casesColors);
        for(int i = 0 ; i < this.nbCasesWidth ; i++) {
            state.putIntArray(id+"cases" + i, this.cases[i]);
        }
    }

    void restoreState(Bundle state, String id) {
        this.nbCasesWidth = state.getInt(id+"nbCasesWidth");
        this.nbCasesHeight = state.getInt(id+"nbCasesHeight");
        this.caseWidth = state.getFloat(id+"caseWidth");
        this.caseHeight = state.getFloat(id+"caseHeight");
        this.casesColors = state.getIntArray(id+"casesColors");
        this.cases = new int[this.nbCasesWidth][this.nbCasesHeight];
        for(int i=0 ; i < this.nbCasesWidth ; i++)
            this.cases[i] = state.getIntArray(id+"cases"+i);
    }
}
