package com.multimediaapp.bikeactivity.DataBase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MyContentProvider extends ContentProvider {

    private MyDataBase dbHelper;
    public static SQLiteDatabase db;

    public static final String SPEED_TABLE = "Velocità";
    public static final String ROLL_TABLE = "Angolo_di_piega";
    public static final String ACC_TABLE = "Accelerazione";
    public static final String LIN_ACC_TABLE = "Accelerazione_Lineare";
    public static final String SESSIONS_TABLE = "Sessioni";
    private static final String PROVIDER = "/MyContentProvider/";
    private static final String SPEED_PATH = PROVIDER + SPEED_TABLE;
    private static final String ROLL_PATH = PROVIDER + ROLL_TABLE;
    private static final String ACC_PATH = PROVIDER + ACC_TABLE;
    private static final String LIN_ACC_PATH = PROVIDER + LIN_ACC_TABLE;
    private static final String SESSIONS_PATH = PROVIDER + SESSIONS_TABLE;
    private static final String AUTHORITY = "com.multimediaapp.bikeactivity.DataBase" + PROVIDER;
    public static final Uri SPEED_URI = Uri.parse("content://" + AUTHORITY + SPEED_TABLE);
    public static final Uri ROLL_URI = Uri.parse("content://" + AUTHORITY + ROLL_TABLE);
    public static final Uri ACC_URI = Uri.parse("content://" + AUTHORITY + ACC_TABLE);
    public static final Uri LIN_ACC_URI = Uri.parse("content://" + AUTHORITY + LIN_ACC_TABLE);
    public static final Uri SESSIONS_URI = Uri.parse("content://" + AUTHORITY + SESSIONS_TABLE);
    public static final String _ID_Col = "_id";
    public static final String InstantSpeed_Col = "Velocità_Istantanea";
    public static final String InstantRoll_Col = "Angolo_Istantaneo";
    public static final String InstantAcc_Col = "Accelerazione";
    public static final String InstantLinAcc_Col = "Accelerazione_Lineare";
    public static final String TimeStamp_Col = "TimeStamp";
    public static final String MaxSpeed_Col = "Velocità_Max";
    public static final String MeanSpeed_Col = "Velocità_Media";
    public static final String RightRoll_Col = "Angolo_Dx";
    public static final String LeftRoll_Col = "Angolo_Sx";
    public static final String TotalTime_Col = "Tempo_Tot";

    @Override
    public boolean onCreate()
    {
        dbHelper = new MyDataBase(getContext());
        /// calling dbHelper to write into a database
        db = dbHelper.getWritableDatabase();

        if(db == null) return false;
        else return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        String path = uri.getPath();

        switch (path)
        {
            /// get data into speed table
            case SPEED_PATH:
                return db.query(SPEED_TABLE, projection, selection, null, null, null, sortOrder);
            /// get data into roll table
            case ROLL_PATH:
                return db.query(ROLL_TABLE, projection, selection, null, null, null, sortOrder);
            /// get data into acc table
            case ACC_PATH:
                return db.query(ACC_TABLE, projection, selection,null , null, null, sortOrder);
            case LIN_ACC_PATH:
                return db.query(LIN_ACC_TABLE, projection, selection,null , null, null, sortOrder);
            case SESSIONS_PATH:
                return db.query(SESSIONS_TABLE, projection, selection,null , null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
     public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        /// get path of URI
        String path = uri.getPath();

        switch (path)
        {
            /// insert data into speed table
            case SPEED_PATH:
                db.insert(SPEED_TABLE, null, values);
                break;
            /// insert data into roll table
            case ROLL_PATH:
                db.insert(ROLL_TABLE, null, values);
                break;
            /// insert data into acc table
            case ACC_PATH:
                db.insert(ACC_TABLE, null, values);
                break;
            case LIN_ACC_PATH:
                db.insert(LIN_ACC_TABLE, null, values);
                break;
            case SESSIONS_PATH:
                db.insert(SESSIONS_TABLE,null,values);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        /// get path of URI
        String path = uri.getPath();

        switch (path)
        {
            /// insert data into speed table
            case SPEED_PATH:
                db.delete(MyContentProvider.SPEED_TABLE, selection, null);
                break;
            /// insert data into roll table
            case ROLL_PATH:
                db.delete(MyContentProvider.ROLL_TABLE, selection, null);
                break;
            /// insert data into acc table
            case ACC_PATH:
                db.delete(MyContentProvider.ACC_TABLE, selection, null);
                break;
            case LIN_ACC_PATH:
                db.delete(MyContentProvider.LIN_ACC_TABLE, selection, null);
                break;
            case SESSIONS_PATH:
                db.delete(MyContentProvider.SESSIONS_TABLE, selection,null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
        return 1;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }
}