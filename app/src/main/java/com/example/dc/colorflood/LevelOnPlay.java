package com.example.dc.colorflood;


import android.os.Bundle;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

class LevelOnPlay {
    private Level lvl;
    private Level savedLvl;
    private int nbMoves = 0;
    private OnLevelEventListener onLevelEventListener;
    private int extraMoves = 0;
    private int currentLevel = 0;

    LevelOnPlay(){
        this.lvl = new Level();
    }

    void setOnLevelEventListener(OnLevelEventListener eventListener) {
        this.onLevelEventListener = eventListener;
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

    int getMaxNbMoves() {
        return this.lvl.getMaxNbMoves();
    }

    void setMaxNbMoves(int maxNbMoves) {
        this.lvl.setMaxNbMoves(maxNbMoves);
    }

    int getNbMoves() {
        return nbMoves;
    }

    int getExtraMoves() {
        return extraMoves;
    }

    void setExtraMoves(int extraMoves) {
        this.extraMoves = extraMoves;
    }

    int getCurrentLevel() {
        return currentLevel;
    }

    void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    /**
     * Ajoute dans la queue fifo la case nextCase si elle n'a pas déjà été visitée et si elle est de la couleur color
     * Met aussi à jour la liste checked
     * @param checked
     * La liste des cases déjà vérifiés
     * @param fifo
     * La queue à remplir
     */
    private void updateFifo(HashSet<Pair<Integer,Integer>> checked, LinkedList<Pair<Integer,Integer>> fifo, Pair<Integer,Integer> nextCase, int color){
        if (this.lvl.getCases()[nextCase.first][nextCase.second] == color) {
            if (!checked.contains(nextCase)) {
                fifo.add(nextCase);
                checked.add(nextCase);
            }
        }
    }

    /**
     * Retourne la liste des cases connexes de même couleur
     * @return
     */
    ArrayList<Pair<Integer, Integer>> getGroup()
    {
        ArrayList<Pair<Integer, Integer>> ret = new ArrayList<>(); // groupe à retourner

        LinkedList<Pair<Integer,Integer>> changeFifo = new LinkedList<>(); // liste d'attente des cases à visiter
        HashSet<Pair<Integer,Integer>> checked = new HashSet<>(); // liste des cases visités
        Pair<Integer,Integer> currentCase = new Pair<>(0,0); // case actuelle
        changeFifo.add(currentCase);
        checked.add(currentCase);
        int oldColor = this.lvl.getCases()[0][0];

        while(!changeFifo.isEmpty()){
            currentCase = changeFifo.poll();
            ret.add(currentCase);
            for(int i = -1; i<2; i++){
                for (int j = -1; j<2 ; j++){
                    if(Math.abs(i)==Math.abs(j)) // si la case est 0, 0 ou est une diagonale
                        continue;
                    Pair<Integer,Integer> nextCase = new Pair<>(currentCase.first + i, currentCase.second + j);
                    if (nextCase.first < this.lvl.getNbCasesWidth() && nextCase.first >= 0
                            && nextCase.second < this.lvl.getNbCasesHeight() && nextCase.second >= 0) {
                        // on vérifie que la nouvelle case est dans le champ
                        updateFifo(checked, changeFifo, nextCase, oldColor);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Condition de victoire
     * @return
     */
    private boolean checkWin()
    {
        int color = this.lvl.getCases()[0][0];
        for(int i = 0; i < this.lvl.getNbCasesWidth(); ++i)
        {
            for(int j = 0; j < this.lvl.getNbCasesHeight(); ++j)
            {
                if(this.lvl.getCases()[i][j] != color)
                    return false;
            }
        }
        return true;
    }

    /**
     * Condition de défaite
     * @return
     */
    private boolean checkLost() {
        return this.nbMoves == this.lvl.getMaxNbMoves() + this.extraMoves;
    }

    /**
     * Joue un coup en fonction de la nouvelle newColor
     * @param newColor
     */
    void play(int newColor)
    {
        // on commence par actualiser le nombre de coups restant
        this.updateMoves();

        // on créer le groupe de cases connexes de même couleur
        ArrayList<Pair<Integer,Integer>> group = getGroup();

        // on change leur couleur
        for (Pair<Integer,Integer> p : group) {
            this.lvl.setCase(p.first, p.second, newColor);
        }

        // on vérifie si on a gagné
        if (checkWin()) {
            this.win();
            return;
        }
        if (this.checkLost()) {
            this.lose();
        }

    }

    private void win(){
        this.onLevelEventListener.onWin();
    }

    private void lose(){
        this.onLevelEventListener.onLose();
    }

    /**
     * Met à jour les mouvements restants
     */
    private void updateMoves(){
        if (this.nbMoves != this.lvl.getMaxNbMoves())
            this.nbMoves++;
        else
            this.onLevelEventListener.decExtraMoves();
    }

    /**
     * Initialise le niveau
     * @param nbColors
     * Nombre de couleurs du nieau
     */
    void initLevel(int nbColors) {
        this.nbMoves = 0;
        this.lvl.init(nbColors);
        this.savedLvl = new Level(this.lvl);
    }

    /**
     * Redémmare le niveau en fonction de la sauvegarde
     */
    void restart(){
        this.nbMoves = 0;
        this.lvl = new Level(this.savedLvl);
    }

    void saveState(Bundle state) {
        this.lvl.saveState(state, "principal");
        this.savedLvl.saveState(state, "backup");
        state.putInt("nbMoves", this.nbMoves);
    }

    void restoreState(Bundle state) {
        this.lvl.restoreState(state, "principal");
        this.savedLvl.restoreState(state, "backup");
        this.nbMoves = state.getInt("nbMoves");
    }

    int getStartingCase(){
        return this.lvl.getCases()[0][0];
    }

    public interface OnLevelEventListener {
        void onWin();
        void onLose();
        void decExtraMoves();
    }
}
