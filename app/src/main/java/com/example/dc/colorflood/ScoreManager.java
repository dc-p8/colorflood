package com.example.dc.colorflood;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "highscores";
    private static final int DATABASE_VERSION = 1;

    public ScoreManager(Context context) {
        super(context, DATABASE_NAME+".db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + DATABASE_NAME + " ("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"time_taken INTEGER NOT NULL"
                +");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}
}
