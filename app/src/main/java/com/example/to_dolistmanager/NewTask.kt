package com.example.to_dolistmanager

import android.app.Activity
import android.app.DatePickerDialog
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
import android.widget.Toast


import android.view.View //
import android.graphics.Color //
import androidx.core.content.ContextCompat //
import android.graphics.drawable.GradientDrawable //
import android.icu.util.Calendar
import androidx.constraintlayout.widget.ConstraintLayout


class NewTask : AppCompatActivity() {

//    //variable for database access
    private lateinit var dbHelper: DatabaseHelper


    private var selectedColor: Int = Color.parseColor("#fcfffd") // Default color
    private var selectedColorView: View? = null //
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_task);

        //Task Name Field
        val taskName= findViewById<EditText>(R.id.taskTitle)

        //Task Description Field
        val description = findViewById<EditText>(R.id.description)

        //Date Selector
        val datePicker= findViewById<EditText>(R.id.pickDate)

        datePicker.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                {_, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "${selectedMonth+1}-$selectedDay-$selectedYear"
                    datePicker.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()

        }

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
            val date = datePicker.text.toString()

//            val date = String.format("%s-%d-%d", month, datePicker.dayOfMonth, datePicker.year)
            val title = String.format("%s", taskName.text)
            val desc = String.format("%s", description.text)
            if (title.isEmpty()){
                Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//
//            val resultIntent = Intent().apply {
//                putExtra("title", title)
//                putExtra("date", date);
//                putExtra("description", desc)
//                putExtra("color", selectedColor) //
//            }
//            setResult(Activity.RESULT_OK, resultIntent )
//            finish()

            dbHelper = DatabaseHelper(this)
//            //insert new task into database, check initialized to false
            dbHelper.insertTask(title, date, desc, false, selectedColor)
//
            //return to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // colour code

        val colorPickerLayout = findViewById<LinearLayout>(R.id.colorPickerLayout)

        val colors = listOf(Color.parseColor("#fce5cd"), Color.parseColor("#f4cccc"),
            Color.parseColor("#fff2cc"), Color.parseColor("#d9ead3"),
            Color.parseColor("#d0e0e3"), Color.parseColor("#d9d2e9"), Color.parseColor("#cfe2f3"))

       // colour buttons
        colors.forEach { color ->
            val colorButton = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                   setMargins(18, 5, 18, 0)

                }
                setBackgroundColor(color)
                setOnClickListener {

                    //store selected colour
                    selectedColor = color

                }

            }

            colorPickerLayout.addView(colorButton)
        }

    }
}