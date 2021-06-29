package com.example.wwubricks;

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseManager(context:Context) :
    SQLiteOpenHelper(context, "MyDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS IMAGES(name, url, score)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    //we want to insert a string into the database
    fun insert(name: String, url: String, score: Int) {
        writableDatabase.execSQL("INSERT INTO IMAGES VALUES(\"$name\", \"$url\", \"$score\");")
    }

    //Return list of all rows
    fun readAllRows(): List<String> {
        var result = mutableListOf<String>()
        var cursor = writableDatabase.rawQuery("SELECT * FROM IMAGES", null)
        while (cursor.moveToNext()) {
            result.add(cursor.getString(1))
            Log.d("DEBUG", cursor.getString(0) + cursor.getString(1) + cursor.getString(2))
        }
        return result
    }
}