package com.example.to_dolistmanager

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
import android.widget.SearchView
import android.widget.TextView

import android.graphics.Color

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

        //dummy tasks
        //add dummy tasks to database
//        dbHelper.insertTask("Wash Clothes", "09-21-2025", "Sort laundry by color and fabric, load into washing machine.", false, 5)
//        dbHelper.insertTask("Sweep Kitchen", "09-21-2025", "this is a description", true, 3)
//        dbHelper.insertTask("Clean Bathroom", "09-21-2025", "this is a description", false, 7)
//        items!!.add(taskItem("Wash Clothes", "09-21-2025", "Sort laundry by color and fabric, load into washing machine.", false, id=0))
//        items!!.add(taskItem("Sweep Kitchen", "09-21-2025", "this is a description", false, id=0))
//        items!!.add(taskItem("Clean Bathroom", "09-21-2025", "this is a description", false, id =0))

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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == TASK_RESULT_CODE && resultCode == Activity.RESULT_OK){
//            var title = data?.getStringExtra("title")
//            var date = data?.getStringExtra("date")
//            var description = data?.getStringExtra("description")
//            items!!.add(taskItem(title, date, description, false))
//            adapter.updateDataset(items!!)
//        }
//    }
}