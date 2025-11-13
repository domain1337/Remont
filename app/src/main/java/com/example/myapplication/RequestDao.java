package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class RequestDao {
    public com.example.myapplication.DatabaseHelper dbHelper;

    public RequestDao(com.example.myapplication.DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long addRequest(String name, String phone, String model, String color, String date, String type) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_NAME, name);
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_PHONE, phone);
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_MODEL, model);
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_COLOR, color != null ? color : "");
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_DATE, date);
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_TYPE, type != null ? type : "repair");
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_STATUS, "в работе");
        long id = db.insert(com.example.myapplication.DatabaseHelper.TABLE_REQUESTS, null, values);
        db.close();
        return id;
    }

    public String[] getRequestById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + com.example.myapplication.DatabaseHelper.TABLE_REQUESTS +
                        " WHERE " + com.example.myapplication.DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{id}
        );
        String[] result = null;
        if (cursor.moveToFirst()) {
            result = new String[]{
                    cursor.getString(0), // id
                    cursor.getString(1), // name
                    cursor.getString(2), // phone
                    cursor.getString(3), // model
                    cursor.getString(4), // color
                    cursor.getString(5), // date
                    cursor.getString(6), // type
                    cursor.getString(7)  // status
            };
        }
        cursor.close();
        db.close();
        return result;
    }

    public boolean updateRequest(String id, String name, String phone, String model, String color, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_NAME, name);
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_PHONE, phone);
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_MODEL, model);
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_COLOR, color != null ? color : "");
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_DATE, date);
        // Не обновляем type и status здесь (если нужно — добавьте)
        int rows = db.update(
                com.example.myapplication.DatabaseHelper.TABLE_REQUESTS,
                values,
                com.example.myapplication.DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{id}
        );
        db.close();
        return rows > 0;
    }

    public List<String[]> getRequestsByType(String type) {
        List<String[]> requests = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + com.example.myapplication.DatabaseHelper.TABLE_REQUESTS +
                " WHERE " + com.example.myapplication.DatabaseHelper.COLUMN_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{type});
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                String model = cursor.getString(3);
                String color = cursor.getString(4);
                String date = cursor.getString(5);
                String status = cursor.getString(7);
                requests.add(new String[]{id, name, phone, model, color, date, status});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return requests;
    }

    public List<String[]> getAllRequests() {
        List<String[]> requests = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + com.example.myapplication.DatabaseHelper.TABLE_REQUESTS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                String model = cursor.getString(3);
                String color = cursor.getString(4);
                String date = cursor.getString(5);
                String type = cursor.getString(6);
                String status = cursor.getString(7);
                requests.add(new String[]{id, name, phone, model, color, date, type, status});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return requests;
    }

    public void updateStatus(String id, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(com.example.myapplication.DatabaseHelper.COLUMN_STATUS, newStatus);
        db.update(
                com.example.myapplication.DatabaseHelper.TABLE_REQUESTS,
                values,
                com.example.myapplication.DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{id}
        );
        db.close();
    }

    public long deleteRequestById(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = db.delete(
                com.example.myapplication.DatabaseHelper.TABLE_REQUESTS,
                com.example.myapplication.DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{id}
        );
        db.close();
        return result;
    }
}