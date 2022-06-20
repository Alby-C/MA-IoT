package com.multimediaapp.bikeactivity.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Bike Activity";
    private static final int VERSION = 1;
    private static final String TAG = "Database";

    public MyDataBase (Context context) {
        super(context, DATABASE_NAME , null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE Velocità(_id INTEGER PRIMARY KEY AUTOINCREMENT, Velocità_Istantanea FLOAT , TimeStamp LONG)");
        Log.i(TAG, "Tabella Velocità creata");
        db.execSQL("CREATE TABLE Angolo_di_piega(_id INTEGER PRIMARY KEY AUTOINCREMENT, Angolo_Istantaneo FLOAT, TimeStamp LONG)");
        db.execSQL("CREATE TABLE Accelerazione(_id INTEGER PRIMARY KEY AUTOINCREMENT, AccelerazioneXY FLOAT, AccelerazioneXYZ FLOAT, TimeStamp LONG)");
        Log.i(TAG, "Tabella acc creata");
        db.execSQL("CREATE TABLE Sessioni(_id INTEGER PRIMARY KEY AUTOINCREMENT, Velocità_Max FLOAT, Velocità_Media FLOAT, Angolo_Dx FLOAT, Angolo_Sx FLOAT, Tempo_Tot TEXT)");
        Log.i(TAG, "Tabella sessioni creata");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}

