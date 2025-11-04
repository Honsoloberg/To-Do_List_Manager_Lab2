package com.example.to_dolistmanager

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.SearchView
import android.widget.TextView

import android.graphics.Color
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    var items: ArrayList<taskItem>? = null

    val TASK_RESULT_CODE = 100

    //variable for database access
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var button: Button
    private lateinit var adapter: listAdapter
    private lateinit var searchView: SearchView
    private lateinit var deleteTasks: Button

    val requiredPermission = Manifest.permission.READ_MEDIA_IMAGES

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted do nothing
            } else {
                // Permission denied.
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById<SearchView>(R.id.searchBar)
        listView = findViewById<ListView>(R.id.listView)
        button = findViewById<Button>(R.id.button1)
        deleteTasks = findViewById<Button>(R.id.deleteTasks)
        val textView = findViewById<TextView>(R.id.Title)

        items = ArrayList<taskItem>()

        dbHelper = DatabaseHelper(this)
//        dbHelper.deleteAllTasks()

        checkAndRequestStoragePermission()


        //add dummy tasks to database
//        dbHelper.insertTask("Wash Clothes", "09-21-2025", "Sort laundry by color and fabric, load into washing machine.", false, 5)
//        dbHelper.insertTask("Sweep Kitchen", "09-21-2025", "this is a description", true, 3)
//        dbHelper.insertTask("Clean Bathroom", "09-21-2025", "this is a description", false, 7)

        //dummy tasks
//        val image = "/storage/emulated/0/Pictures/Screenshot 2023-11-19 134436.png"
//        items!!.add(taskItem("Wash Clothes", "09-21-2025", "Sort laundry by color and fabric, load into washing machine.", false, id=0, image = image))

//        //populate items list using the database
        items = dbHelper.getTasks()

        adapter = listAdapter(items!!, applicationContext)
        listView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        listView.onItemClickListener = AdapterView.OnItemClickListener {_,_, position, _ ->
            val items: taskItem = items!![position]
            items.checked = !items.checked
            adapter.notifyDataSetChanged()
            var nameOfTask = items.name as String
            var checkStatus = items.checked
            dbHelper.updateTaskCheck(nameOfTask, !checkStatus)
        }

        button.setOnClickListener {
            startActivityForResult(Intent(this, NewTask::class.java), TASK_RESULT_CODE)
        }

        deleteTasks.setOnClickListener {
            dbHelper.deleteAllTasks()
            items = dbHelper.getTasks()
            adapter.updateDataset(items as ArrayList<taskItem>)
        }
    }

//    when the activity is started again, update the tasklist from the database
    override fun onStart(){
        super.onStart()

        dbHelper = DatabaseHelper(this)
        items = ArrayList<taskItem>()
        items = dbHelper.getTasks()
    }

    private fun checkAndRequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                requiredPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted do nothing
        } else {
            // Permission is not granted, request it
            // Optional: Check shouldShowRequestPermissionRationale() to show an educational UI
            requestPermissionLauncher.launch(requiredPermission)
        }
    }
}