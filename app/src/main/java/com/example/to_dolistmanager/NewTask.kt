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
import android.content.Context
import android.provider.OpenableColumns


import android.view.View //
import android.graphics.Color //
import androidx.core.content.ContextCompat //
import android.graphics.drawable.GradientDrawable //
import android.icu.util.Calendar
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

import android.net.Uri
import android.os.Environment
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File


class NewTask : AppCompatActivity() {

//    //variable for database access
    private lateinit var dbHelper: DatabaseHelper

    private var selectedColor: Int = Color.parseColor("#fcfffd") // Default color
    private var selectedColorView: View? = null //
    private var selectedImage: Uri? = null
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

            //saves selectedImage into app storage if not null
            val savedFile = if (selectedImage!=null){persistPickedImage(this, selectedImage!!)} else null
            //Either choose a saved path or a Null String
            val imageFile = savedFile?.absolutePath ?:""

            dbHelper = DatabaseHelper(this)
//              //insert new task into database, check initialized to false
            dbHelper.insertTask(title, date, desc, false, selectedColor, imageFile)
//
            //return to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
//
//            val resultIntent = Intent().apply {
//                putExtra("title", title)
//                putExtra("date", date);
//                putExtra("description", desc)
//                putExtra("color", selectedColor) //
//            }
//            setResult(Activity.RESULT_OK, resultIntent )
//            finish()


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
                selectedImage = uri
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
    }


    //function that takes the relative Uri and creates a file context.filesDir in apps internal storage
    //to save the image permanently within the app
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