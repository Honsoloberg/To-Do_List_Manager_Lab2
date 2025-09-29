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
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    public var items: ArrayList<taskItem>? = null

    val TASK_RESULT_CODE = 100

    private lateinit var listView: ListView
    private lateinit var button: Button
    private lateinit var adapter: listAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize views
        listView = findViewById<ListView>(R.id.listView)
        button = findViewById<Button>(R.id.button1)
        val textView = findViewById<TextView>(R.id.Title)
        val searchBar = findViewById<SearchView>(R.id.searchBar)

        //Array for tasks in list
        items = ArrayList<taskItem>()

        //dummy tasks
        items!!.add(taskItem("Wash Clothes", "09-21-2025", "Sort laundry by color and fabric, load into washing machine.", false))
        items!!.add(taskItem("Sweep Kitchen", "09-21-2025", "this is a description", false))
        items!!.add(taskItem("Clean Bathroom", "09-21-2025", "this is a description", false))

        //Adapter for tasklist
        adapter = listAdapter(items!!, applicationContext)
        listView.adapter = adapter

        //Function for updating checkbox's onclick
        listView.onItemClickListener = AdapterView.OnItemClickListener {_,_, position, _ ->
            val items: taskItem = items!![position] as taskItem
            items.checked = !items.checked
            adapter.notifyDataSetChanged()
        }

        //Start's New Task activity
        button.setOnClickListener {
            val intent = Intent(this, NewTask::class.java)
            startActivity(intent)
        }
    }


}