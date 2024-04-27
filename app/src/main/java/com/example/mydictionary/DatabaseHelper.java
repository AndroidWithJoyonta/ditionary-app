package com.example.mydictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper extends SQLiteAssetHelper {
    public DatabaseHelper(Context context) {
        super(context, "dictionary.db", null,  1);
    }


    public Cursor getAllData(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from Dictionary ",null);
        return cursor;
    }


    public Cursor searchData(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        //  Cursor cursor = db.rawQuery("SELECT * FROM Dictionary WHERE word LIKE '%"+word+"%' OR meaning LIKE '%"+word+"%'",null);
        Cursor cursor = db.rawQuery("SELECT * FROM Dictionary WHERE word LIKE '"+word+"%'",null);
        return cursor;
    }
}
