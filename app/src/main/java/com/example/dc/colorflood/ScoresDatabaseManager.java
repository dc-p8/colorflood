package com.example.dc.colorflood;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class ScoresDatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "highscores";
    private static final String TABLE_NAME = "highscores";
    private static final int DATABASE_VERSION = 1;
    static final String ID_COLUMN = "_id";
    static final String TIME_TAKEN_COLUMN = "time_taken";

    ScoresDatabaseManager(Context context) {
        super(context, DATABASE_NAME+".db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                +ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +TIME_TAKEN_COLUMN+" INTEGER NOT NULL"
                +");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}

    private void deleteAll(){
        this.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    private Cursor selectAll(){
        return this.getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, ID_COLUMN);
    }

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

    public interface AsyncCursorResponse {
        void processResult(Cursor res);
    }

    private class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            deleteAll();
            return null;
        }
    }

    private class AddOrUpdateIfBetterAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... args) throws IllegalArgumentException {
            if (args.length != 2)
                throw new IllegalArgumentException("wrong number of arguments");
            addOrUpdateIfBetter(args[0].intValue(), args[1]);
            return null;
        }
    }

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

    void executeSelectAll(AsyncCursorResponse callback){
        new SelectAllAsyncTask(callback).execute();
    }
}
