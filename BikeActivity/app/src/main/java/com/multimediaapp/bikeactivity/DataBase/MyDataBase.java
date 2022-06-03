package com.multimediaapp.bikeactivity.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Bike Activity";
    private static final int VERSION = 1;

    public MyDataBase (Context context) {
        super(context, DATABASE_NAME , null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE Velocità(_id INTEGER PRIMARY KEY AUTOINCREMENT, Velocità_Istantanea FLOAT , TimeStamp LONG)");
        db.execSQL("CREATE TABLE Angolo_di_piega(_id INTEGER PRIMARY KEY AUTOINCREMENT, Angolo_Istantaneo FLOAT, TimeStamp LONG)");
        db.execSQL("CREATE TABLE Accelerazione(_id INTEGER PRIMARY KEY AUTOINCREMENT, AccelerazioneX FLOAT, AccelerazioneY FLOAT , AccelerazioneZ FLOAT, TimeStamp LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}

