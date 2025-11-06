package com.example.to_dolistmanager

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
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
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File


class UpdateTask : AppCompatActivity() {

    //    //variable for database access
    private lateinit var dbHelper: DatabaseHelper

    private var selectedColor: Int = Color.parseColor("#fcfffd") // Default color
    private var selectedColorView: View? = null //
    private var selectedImage: Uri? = null
    private var taskId: Int = -1
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_task);

        //Task Name Field
        val title= findViewById<EditText>(R.id.updateTaskTitle)

        //Task Description Field
        val description = findViewById<EditText>(R.id.updateDescription)

        //Date Selector
        val datePicker= findViewById<EditText>(R.id.updatePickDate)

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


        //Back Button
        val backButton = findViewById<Button>(R.id.backButton);
        backButton.setOnClickListener () {
            val backIntent = Intent(this, MainActivity::class.java)
            finish()
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

        //functionality for uploading image from the phone
        val image = findViewById<ImageView>(R.id.image)
        val upload = findViewById<Button>(R.id.uploadButton)
        val camera = findViewById<Button>(R.id.takePicture)

        //if user selects an image from a gallery, set the image using its URI inside the imageView
        val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri ->
            if (uri != null){
                image.setImageURI(uri)
            }
        }

        //if user successfully takes a picture on the camera, set the imageView as the picture
        val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && selectedImage != null) {
                image.setImageURI(selectedImage)
            }
        }

        //when user clicks on the upload button, launch the photo picker to pick from the photo gallery
        upload.setOnClickListener {
            pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //when user clicks on the take picture button, it created a temporary file to store the
        // image with prefix IMG and suffix .jpg, it then converts that file into a URI and launches the camera
        camera.setOnClickListener {
            val photoFile = File.createTempFile("IMG_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
            selectedImage = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
            //fills the imageView with the picture taken
            takePictureLauncher.launch(selectedImage!!)
        }

        //delete task

        val delete = findViewById<Button>(R.id.deleteButton)
        delete.setOnClickListener {
            dbHelper = DatabaseHelper(this)
            dbHelper.deleteTask(taskId)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        //code for updating task

        dbHelper = DatabaseHelper(this)
        taskId=intent.getIntExtra("TASK_ID",-1)
        val fillName= intent.getStringExtra("TITLE")
        val fillDate = intent.getStringExtra("DATE")
        val fillDescription = intent.getStringExtra("DESCRIPTION")
        val fillColor = intent.getIntExtra("COLOR", 0)
        val fillPicture = intent.getStringExtra("PICTURE")

        title.setText(fillName)
        datePicker.setText(fillDate)
        description.setText(fillDescription)
        selectedColor=fillColor

        if (!fillPicture.isNullOrEmpty()){
            val imageFile = File(fillPicture)
            val imageUri = Uri.fromFile(imageFile)
            selectedImage=imageUri
            image.setImageURI(imageUri)
        }

        val updateTask = findViewById<Button>(R.id.updateTask)
        updateTask.setOnClickListener {
            val updatedTitle = title.text.toString()
            val updatedDate = datePicker.text.toString()
            val updatedDesc = description.text.toString()
            val updatedColor = selectedColor

            val savedFile = persistPickedImage(this, selectedImage!!)
            if (savedFile !=null) {
                dbHelper = DatabaseHelper(this)
//
                dbHelper.updateTaskFull(taskId, updatedTitle, updatedDate, updatedDesc, updatedColor, savedFile.absolutePath)
//
                //return to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }




    }

    private fun persistPickedImage(context: Context, srcUri: Uri, suggestedName: String? = null): File? {
        return try {
            val displayName = suggestedName ?: run {
                context.contentResolver.query(srcUri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                    if (c.moveToFirst()) c.getString(0) else "image_${System.currentTimeMillis()}.jpg"
                } ?: "image_${System.currentTimeMillis()}.jpg"
            }

            val dest = File(context.filesDir, displayName)
            context.contentResolver.openInputStream(srcUri)?.use { input ->
                dest.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null
            dest
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}