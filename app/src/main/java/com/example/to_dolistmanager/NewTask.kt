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
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.NewTaskLayout)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        why was this calling the entire XML file??????????????????
//        val screen = findViewById<LinearLayout>(R.id.NewTaskLayout);
        val textViewTitle = findViewById<EditText>(R.id.taskTitle);

//      Dates from DatePicker MUST BE in string form. A new dummy template has been
//      pushed to the main activity. $day/${month+1}/$year

        //Back Button
        val backButton = findViewById<Button>(R.id.backButton);
        backButton.setOnClickListener () {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        //Name
        val taskName= findViewById<EditText>(R.id.taskTitle)

        //Description
        val description = findViewById<EditText>(R.id.description)

        //Date picker into string
        val datePicker= findViewById<DatePicker>(R.id.pickDate)
        val date = String.format("%d-%d-%d", datePicker.dayOfMonth, datePicker.month, datePicker.year)
        val resultIntent = Intent().apply {
            putExtra("date", date);
            putExtra("title", taskName.text )
            putExtra("description", description.text)
        }
        setResult(Activity.RESULT_OK, resultIntent )
        finish()

    }
}