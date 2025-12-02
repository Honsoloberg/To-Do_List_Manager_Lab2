package com.example.to_dolistmanager

import android.Manifest
import android.app.Activity
import android.content.Context
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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.math.sqrt
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.hardware.SensorEventListener

class MainActivity : AppCompatActivity(), SensorEventListener {

    var items: ArrayList<taskItem>? = null

    val TASK_RESULT_CODE = 100

    //variable for database access
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var button: Button
    private var taskAdapter: TaskAdapter? = null
    private lateinit var adapter: listAdapter
    private lateinit var searchView: SearchView
    private val SHAKE_THRESHOLD = 7.0f
    private lateinit var deleteTasks: Button
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null

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

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)



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

//        listView.onItemClickListener = AdapterView.OnItemClickListener {_,_, position, _ ->
//            val items: taskItem = items!![position]
//            items.checked = !items.checked
//            adapter.notifyDataSetChanged()
//            var nameOfTask = items.name as String
//            var checkStatus = items.checked
//            dbHelper.updateTaskCheck(nameOfTask, !checkStatus)
//        }

        //uses task adapter to display listView
        taskAdapter = TaskAdapter(this, items!!, dbHelper)
        listView.adapter=taskAdapter

        button.setOnClickListener {
            startActivityForResult(Intent(this, NewTask::class.java), TASK_RESULT_CODE)
        }

        deleteTasks.setOnClickListener {
            dbHelper.deleteAllTasks()
            items = dbHelper.getTasks()
            adapter.updateDataset(items as ArrayList<taskItem>)
            taskAdapter!!.clear()
            taskAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)

        }
        lightSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
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

    override fun onSensorChanged(event: SensorEvent?) {
        if(event == null) return

        when(event.sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                val (x, y, z) = event.values
                val totalAcceleration = sqrt(x + y + z)

                if (totalAcceleration > SHAKE_THRESHOLD) {
                    dbHelper.deleteAllTasks()
                    items = dbHelper.getTasks()
                    taskAdapter!!.clear()
                    taskAdapter!!.notifyDataSetChanged()
                }
            }
            Sensor.TYPE_LIGHT -> {
                val luxValue = event.values[0]

                when{
                    luxValue < 10 -> {
                        changeBrightness(0.1f)
                    }
                    luxValue < 500 -> {
                        changeBrightness(0.5f)

                    }
                    else -> {
                        changeBrightness(1.0f)
                    }
                }
            }

        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun changeBrightness(bval: Float){
        val validatedBrightness = bval.coerceIn(0.0f, 1.0f)

        val layoutParams = window.attributes
        layoutParams.screenBrightness = validatedBrightness
        window.attributes = layoutParams
    }

}