package com.example.dc.colorflood;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

/**
 * Classe qui gère toutes les requêtes en base de données pour la sauvegarde des meilleurs scores
 */
class ScoresDatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "highscores";
    private static final String TABLE_NAME = "highscores";
    private static final int DATABASE_VERSION = 1;
    static final String ID_COLUMN = "_id";
    static final String TIME_TAKEN_COLUMN = "time_taken";

    ScoresDatabaseManager(Context context) {
        super(context, DATABASE_NAME+".db", null, DATABASE_VERSION);
    }

    /**
     * Crée la table qui contiendra le niveau et le meilleur score qui lui est associé
     * puisqu'il n'y aura jamais plus d'un score par niveau, celui ci sert de clef primaire
     * @param sqLiteDatabase la base de donnée qui va stocker cette table
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                +ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +TIME_TAKEN_COLUMN+" INTEGER NOT NULL"
                +");"
        );
    }

    /**
     * Méthode vide car tant qu'aucune migrattion n'a été nécessaire,
     * on ne peut pas anticiper la statégie qui sera choisi pour la mise à jour.
     * Sera probablement remplie par un switchcase sur oldVersion qui fera la migration adapté à chaque mise à jour
     * @param sqLiteDatabase la base de donnée concernée
     * @param oldVersion ancienne version de la base
     * @param newVersion nouvelle version de la base
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}

    /**
     * Supprime tout les highscores
     */
    private void deleteAll(){
        this.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    /**
     * @return un Cursor sur tous les highscores
     */
    private Cursor selectAll(){
        return this.getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, ID_COLUMN+" DESC");
    }

    /**
     * Ajoute un nouveau score si aucun n'existait pour ce niveau
     * ou mets à jour le score du niveau seulement s'il est meilleur
     * @param lvl le niveau concerné
     * @param score le dernier score obtenu
     */
    private void addOrUpdateIfBetter(int lvl, long score){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, ID_COLUMN+" = ?", new String[] {String.valueOf(lvl)}, null, null, null);
        if (cursor.getCount() == 0)
            this.insert(lvl, score, db);
        else {
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex(TIME_TAKEN_COLUMN)) > score) //since score is the time taken, lesser is better
                this.update(lvl, score, db);
        }
        cursor.close();
    }

    private void insert(int lvl, long score, SQLiteDatabase db){
        ContentValues newScore = new ContentValues();
        newScore.put(ID_COLUMN, lvl);
        newScore.put(TIME_TAKEN_COLUMN, score);
        db.insert(TABLE_NAME, null, newScore);
    }

    private void update(int lvl, long score, SQLiteDatabase db){
        ContentValues newScore = new ContentValues();
        newScore.put(ID_COLUMN, lvl);
        newScore.put(TIME_TAKEN_COLUMN, score);
        db.update(TABLE_NAME, newScore, ID_COLUMN+" = ?", new String[] {String.valueOf(lvl)});
    }

    /**
     * interface fonctionnelle qui permet de définir ce que fera une AsyncTask après avoir fini son exécution
     * lors de l'appel de cette AsyncTask et non pas seulement lors de sa déclaration
     */
    public interface AsyncCursorResponse {
        void processResult(Cursor res);
    }

    /**
     * Tâche asynchrone qui supprimera tous les highscores
     */
    private class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            deleteAll();
            return null;
        }
    }

    /**
     * Tâche asynchrone qui mettra éventuellement à jour une entrée ou en créera une nouvelle
     */
    private class AddOrUpdateIfBetterAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... args) throws IllegalArgumentException {
            if (args.length != 2)
                throw new IllegalArgumentException("wrong number of arguments");
            addOrUpdateIfBetter(args[0].intValue(), args[1]);
            return null;
        }
    }

    /**
     * Tâche asynchrone qui permet de récupérer tout le contenu de la table highscores
     * Si cette tâche est annulée, le Cursor sera fermé
     * Sinon se sera la responsabilité de la classe appelante de définir ce qu'elle veut faire avec le Cursor
     * (par le biais de l'interface fonctionnelle AsyncCursorResponse)
     * et notament de le fermer
     */
    private class SelectAllAsyncTask extends AsyncTask<Void, Void, Cursor> {
        private AsyncCursorResponse delegate = null;

        SelectAllAsyncTask(AsyncCursorResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected Cursor doInBackground(Void... args) throws IllegalArgumentException {
            return selectAll();
        }

        @Override
        protected void onCancelled(Cursor cursor) {
            cursor.close();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            delegate.processResult(cursor);
        }
    }

    void executeDeleteAll(){
        new DeleteAllAsyncTask().execute();
    }

    void executeAddOrUpdateIfBetter(int lvl, long score){
        new AddOrUpdateIfBetterAsyncTask().execute(((long) lvl), score);
    }

    /**
     *
     * @param callback Décrit ce qu'il faut faire une fois le contenu de la table obtenu
     */
    void executeSelectAll(AsyncCursorResponse callback){
        new SelectAllAsyncTask(callback).execute();
    }
}
