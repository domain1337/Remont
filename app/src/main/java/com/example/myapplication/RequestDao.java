package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class RequestDao {
    public DatabaseHelper dbHelper;

    public RequestDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // 1. Добавление заявки (7 аргументов)
    public long addRequest(String name, String phone, String model, String color, String date, String type, String problemDescription) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_MODEL, model);
        values.put(DatabaseHelper.COLUMN_COLOR, color != null ? color : "");
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_TYPE, type != null ? type : "repair");
        values.put(DatabaseHelper.COLUMN_STATUS, "в работе");
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, problemDescription != null ? problemDescription : "");
        long id = db.insert(DatabaseHelper.TABLE_REQUESTS, null, values);
        db.close();
        return id;
    }

    // 2. Получение всех заявок
    public List<String[]> getAllRequests() {
        List<String[]> requests = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_REQUESTS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String[] row = new String[9]; // 9 колонок
                for(int i=0; i<9; i++) {
                    row[i] = cursor.getString(i);
                }
                requests.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return requests;
    }

    // 3. Получение заявок по типу
    public List<String[]> getRequestsByType(String type) {
        List<String[]> requests = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_REQUESTS +
                " WHERE " + DatabaseHelper.COLUMN_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{type});
        if (cursor.moveToFirst()) {
            do {
                String[] row = new String[9];
                for(int i=0; i<9; i++) {
                    row[i] = cursor.getString(i);
                }
                requests.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return requests;
    }

    // 4. Получение одной заявки по ID
    public String[] getRequestById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_REQUESTS, null,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{id},
                null, null, null);
        String[] request = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                request = new String[9];
                for(int i=0; i<9; i++) {
                    request[i] = cursor.getString(i);
                }
            }
            cursor.close();
        }
        db.close();
        return request;
    }

    // 5. Обновление статуса
    public void updateStatus(String id, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STATUS, status);
        db.update(DatabaseHelper.TABLE_REQUESTS, values,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }

    // 6. Удаление заявки
    public long deleteRequestById(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = db.delete(DatabaseHelper.TABLE_REQUESTS,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{id});
        db.close();
        return result;
    }

    // 7. Обновление данных заявки
    public boolean updateRequest(String id, String name, String phone, String model, String color, String date, String problemDescription) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_MODEL, model);
        values.put(DatabaseHelper.COLUMN_COLOR, color != null ? color : "");
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, problemDescription != null ? problemDescription : "");

        int rows = db.update(DatabaseHelper.TABLE_REQUESTS, values,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{id});
        db.close();
        return rows > 0;
    }
}