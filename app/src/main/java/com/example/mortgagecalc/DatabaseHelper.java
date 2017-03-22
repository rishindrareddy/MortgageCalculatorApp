package com.example.mortgagecalc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "LocData.db";
    public static final String TABLE_NAME = "LocData_TABLE";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TYPE";
    public static final String COL_3 = "STREET";
    public static final String COL_4 = "CITY";
    public static final String COL_5 = "STATE";
    public static final String COL_6 = "ZIPCODE";
    public static final String COL_7 = "PRICE";
    public static final String COL_8 = "DOWN_PAY";
    public static final String COL_9 = "LOAN_AMOUNT";
    public static final String COL_10 = "ARP";
    public static final String COL_11 = "YEARS";
    public static final String COL_12 = "MONTHLY";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT, " + COL_3 + " TEXT, " + COL_4 + " TEXT, " + COL_5 + " TEXT, " + COL_6 + " TEXT, "
                + COL_7 + " TEXT, " + COL_8 + " TEXT, " + COL_9 + " TEXT, " + COL_10 + " TEXT, " + COL_11 + " TEXT, " + COL_12 + " TEXT " + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String[] values) {

        SQLiteDatabase db = this.getWritableDatabase();
        if (values.length != 11) {
            return false;
        }

        ContentValues contentValues = StringToCV(values);

        long result = db.insert(TABLE_NAME, null, contentValues);    //on insertion db return the id of the row or primary key of the row.

        if (result == -1)    //if inserting leads to an error, query result is -1
            return false;

        return true;
    }

    public Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Cursor sendRowEntry(int a) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE " + COL_1 + " = " + a, null);
        return res;
    }

    public void removeRow(Integer a) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.delete(TABLE_NAME, COL_1 + "=?", new String[]{a.toString()});

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean updateRow(Integer a, String[] val) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = StringToCV(val);

        return db.update(TABLE_NAME, cv, COL_1 + " = " + a, null) > 0;

    }

    public ContentValues StringToCV(String[] values) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, values[0]);
        contentValues.put(COL_3, values[1]);
        contentValues.put(COL_4, values[2]);
        contentValues.put(COL_5, values[3]);
        contentValues.put(COL_6, values[4]);
        contentValues.put(COL_7, values[5]);
        contentValues.put(COL_8, values[6]);
        contentValues.put(COL_9, values[7]);
        contentValues.put(COL_10, values[8]);
        contentValues.put(COL_11, values[9]);
        contentValues.put(COL_12, values[10]);

        return contentValues;

    }

}
