package com.example.dc.colorflood;


import android.os.Bundle;
import android.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Classe qui possède toutes les caractéristique d'un niveau en cours de partie
 * @see Level
 */
class LevelOnPlay {
    private Level lvl;
    private Level savedLvl;
    private int nbMoves = 0;
    private OnLevelEventListener onLevelEventListener;
    private int extraMoves = 0;
    private int currentLevel = 0;

    LevelOnPlay(){
        this.lvl = new Level();
        this.savedLvl = new Level();
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

    int[] getCasesColors() {
        return this.lvl.getCasesColors();
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
     * Ajoute dans la queue fifo la case nextCase si elle n'a pas déjà été visitée et si elle est de la couleur que color
     * Met aussi à jour checked
     * @param checked
     * La liste des cases déjà vérifiées
     * @param fifo
     * La queue à remplir
     * @param nextCase
     * coordonnée de la case considérée
     * @param color
     * couleur pour que nextCase soit valide
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
     * Joue un coup en fonction de newColor
     * @param newColor
     * la nouvelle couleur sélectionnée par le joueur
     */
    void play(int newColor) {
        this.updateMoves();
        int nbCaseColored = 0; //nombre de case qui sont colorées en newColor, utile pour savoir si le joueur a gagné
        LinkedList<Pair<Integer,Integer>> changeFifo = new LinkedList<>(); //file d'attente des cases qui vont changer de couleur
        LinkedList<Pair<Integer,Integer>> winFifo = new LinkedList<>(); //file d'attente des case qui sont déjà de la bonne couleur
        HashSet<Pair<Integer,Integer>> checked = new HashSet<>(); //set qui permet de vérifier quelles cases ont déjà été mise dans une file d'attente
        Pair<Integer,Integer> currentCase = new Pair<>(0,0); //la case actuellement vérifiée (on commence par la case en haut à gauche)
        changeFifo.add(currentCase); //on rajoute la case actuelle aux cases qui vont être colorées
        checked.add(currentCase); //on rajoute la case actuelle aux cases qui on déjà été vu
        int oldColor = this.lvl.getCases()[0][0]; //contient l'ancienne couleur contenue par la case de départ
        while(!changeFifo.isEmpty()){ //pour toutes les cases qui doivent être colorées
            nbCaseColored++;
            currentCase = changeFifo.poll();
            this.lvl.setCase(currentCase.first, currentCase.second, newColor); //on change la couleur de la case
            for(int i = -1; i<2; i++){
                for (int j = -1; j<2 ; j++){
                    if(Math.abs(i)==Math.abs(j))
                        continue; //on visite tous les voisins de currentCase, excepté les cases à ses diagonales
                    Pair<Integer,Integer> nextCase = new Pair<>(currentCase.first + i, currentCase.second + j);
                    if (nextCase.first < this.lvl.getNbCasesWidth() && nextCase.first >= 0
                            && nextCase.second < this.lvl.getNbCasesHeight() && nextCase.second >= 0) { //on vérifie que nextCase est dans le plateau
                        updateFifo(checked, changeFifo, nextCase, oldColor); //ajoute nextCase à la file d'attente à colorer si elle est de la couleur oldColor
                        updateFifo(checked, winFifo, nextCase, newColor); //ajoute nextCase à la file d'attente des cases qui compte pour la victoire si elle est de la couleur newColor
                    }
                }
            }
        }
        if (hasWon(nbCaseColored, winFifo, newColor, checked)) {
            this.win();
            return;
        }
        if (this.hasLost())
            this.lose();
    }

    /**
     * Vérifie si le joueur a gagné en mettant à jour nbCaseColored en parcourant toutes les cases restantes de la bonne couleur
     * @param nbCaseColored
     * compteur de toutes les case de la bonne couleur
     * @param winFifo
     * file d'attente des cases de la bonne couleur
     * @param winColor
     * couleur de victoire
     * @param checked
     * set qui indique si la case a été été mise dans la file d'attente
     * @return si le joueur a ou non gagné par son dernier coup
     */
    private boolean hasWon(int nbCaseColored, LinkedList<Pair<Integer,Integer>> winFifo, int winColor, HashSet<Pair<Integer,Integer>> checked) {
        while(!winFifo.isEmpty()){ //pour toutes les case de la couleur winColor
            nbCaseColored++;
            Pair<Integer,Integer> currentCase = winFifo.poll();
            for(int i = -1; i<2; i++) {
                for (int j = -1; j < 2; j++) {
                    if(Math.abs(i)==Math.abs(j))
                        continue; //on visite tous les voisins de currentCase, excepté les cases à ses diagonales
                    Pair<Integer, Integer> nextCase = new Pair<>(currentCase.first + i, currentCase.second + j);
                    if (nextCase.first < this.lvl.getNbCasesWidth() && nextCase.first >= 0
                            && nextCase.second < this.lvl.getNbCasesHeight() && nextCase.second >= 0) { //on vérifie que la nextCase est dans le plateau
                        updateFifo(checked, winFifo, nextCase, winColor); //ajoute nextCase à la file d'attente des cases qui compte pour la victoire si elle est de la couleur winColor
                    }
                }
            }
        }
        return nbCaseColored == this.lvl.getNbCasesHeight() * this.lvl.getNbCasesWidth(); // renvoit vrai si le nombre de cases de la bonne couleur est au nombre des cases total
    }

    private boolean hasLost() {
        return this.nbMoves == this.lvl.getMaxNbMoves() + this.extraMoves;
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
     * Nombre de couleurs du niveau
     */
    void initLevel(int nbColors) {
        this.nbMoves = 0;
        this.lvl.init(nbColors);
        this.savedLvl = new Level(this.lvl);
    }

    /**
     * Redémarre le niveau en copiant le niveau tel qu'il était à son démarrage
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

    /**
     * interface fonctionnelle qui permet à une autre classe
     * de recevoir les événements essentiels de la partie
     */
    public interface OnLevelEventListener {
        /**
         * event déclenché en cas de victoire
         */
        void onWin();
        /**
         * event déclenché en cas de défaite
         */
        void onLose();
        /**
         * event déclenché à chaque utilisation d'un extraMove
         */
        void decExtraMoves();
    }
}
