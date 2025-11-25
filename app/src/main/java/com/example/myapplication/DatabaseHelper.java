package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "service.db";
    private static final int DATABASE_VERSION = 3; // Версия базы данных

    public static final String TABLE_REQUESTS = "requests";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DESCRIPTION = "description"; // Новое поле

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_REQUESTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_MODEL + " TEXT, " +
                    COLUMN_COLOR + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TYPE + " TEXT, " +
                    COLUMN_STATUS + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        onCreate(db);
    }
}