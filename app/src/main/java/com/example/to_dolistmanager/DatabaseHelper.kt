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
                checked INTEGER NOT NULL,
                color INTEGER NOT NULL
                )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS Tasks")
        onCreate(db)
    }

    //for testing
    fun deleteTable(){
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS Tasks")
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS Tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                description TEXT NOT NULL,
                checked INTEGER NOT NULL,
                color INTEGER NOT NULL
                )
            """.trimIndent()
        )
    }

    fun insertTask(title: String, date: String, desc: String, checkBool: Boolean, color: Int ){
        val checkInt: Int = if(checkBool) 1 else 0
        val db = writableDatabase
        db.execSQL("INSERT INTO Tasks (title,date,description,checked,color) VALUES (?,?,?,?,?)",
            arrayOf(title,date,desc,checkInt, color))
    }

    //return all tasks in the database as an array of taskItems
    fun getTasks(): ArrayList<taskItem> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Tasks", null)
        val tasks = ArrayList<taskItem>()

        while(cursor.moveToNext()){
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val checkInt = cursor.getInt(cursor.getColumnIndexOrThrow("checked"))
            val checkBool: Boolean = if(checkInt == 0) false else true

            tasks!!.add(taskItem(title, date, desc, checkBool))
        }
        cursor.close()
        return tasks
    }

    //function to change the checked value (update)
    fun updateTaskCheck(title: String, check: Boolean){
        val checkInt: Int = if(check) 1 else 0
        val db = writableDatabase
        db.execSQL("UPDATE Tasks SET checked = ? WHERE title = ?", arrayOf(title, checkInt))
    }

    //function to check if table is empty (return bool)
    fun isEmpty(): Boolean {
        val db = readableDatabase
        var tasks = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Tasks", null)

        if(cursor.moveToFirst()){
            tasks = cursor.getInt(0)
        }
        cursor.close()

        if (tasks == 0){
            return true
        } else return false
    }

    //function to reset table to empty
    fun deleteAllTasks(){
        val db = writableDatabase
        db.execSQL("DELETE FROM Tasks")
    }

}