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

        //Back Button
        val backButton = findViewById<Button>(R.id.backButton);
        backButton.setOnClickListener () {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Done Button
        val saveButton = findViewById<Button>(R.id.saveTask)
        saveButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}