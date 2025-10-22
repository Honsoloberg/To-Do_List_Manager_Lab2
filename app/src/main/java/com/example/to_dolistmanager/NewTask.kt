package com.example.to_dolistmanager

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.Button
import android.content.Intent
import android.widget.DatePicker

class NewTask : AppCompatActivity() {

    //variable for database access
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_task);

        //Task Name Field
        val taskName= findViewById<EditText>(R.id.taskTitle)

        //Task Description Field
        val description = findViewById<EditText>(R.id.description)

        //Date Selector
        val datePicker= findViewById<DatePicker>(R.id.pickDate)

//      Dates from DatePicker MUST BE in string form. A new dummy template has been
//      pushed to the main activity. $day/${month+1}/$year

        //Back Button
        val backButton = findViewById<Button>(R.id.backButton);
        backButton.setOnClickListener () {
            val backIntent = Intent(this, MainActivity::class.java)
            finish()
        }

        //Done Button
        val saveButton = findViewById<Button>(R.id.saveTask)
        saveButton.setOnClickListener {
            var month = ""
            if(datePicker.month<10){
                month = "0${datePicker.month}"
            }else{
                month = "${datePicker.month}"
            }
            val date = String.format("%s-%d-%d", month, datePicker.dayOfMonth, datePicker.year)
            val title = String.format("%s", taskName.text)
            val desc = String.format("%s", description.text)

            //insert new task into database, check initialized to false
            dbHelper.insertTask(title, date, desc, false)

            //return to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
//            val resultIntent = Intent().apply {
//                putExtra("title", title)
//                putExtra("date", date);
//                putExtra("description", desc)
//            }
//            setResult(Activity.RESULT_OK, resultIntent )
//            finish()
        }

    }
}