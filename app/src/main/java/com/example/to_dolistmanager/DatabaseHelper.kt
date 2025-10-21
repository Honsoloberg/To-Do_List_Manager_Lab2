package com.example.to_dolistmanager

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context): SQLiteOpenHelper(
    context, "TaskDatabase.db",null,1
){
    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS Tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                description TEXT NOT NULL,
                checked INTEGER DEFAULT 0,
                color TEXT CHECK (type IN('red', 'yellow', 'green', 'blue', 'purple')) NOT NULL
                )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS Tasks")
        onCreate(db)
    }

    fun insertTask(title: String, date: String, desc: String, check: Int, color: String ){
        val db = writableDatabase
        db.execSQL("INSERT INTO Tasks (title, date, description, checked, color) VALUES (?,?,?,?,?)",
            arrayOf(title, date, desc, check, color))
    }

}