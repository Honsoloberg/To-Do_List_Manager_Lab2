package com.example.to_dolistmanager

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    public var items: ArrayList<taskItem>? = null

    private lateinit var listView: ListView
    private lateinit var adapter: listAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.listView)

        items = ArrayList<taskItem>()

        adapter = listAdapter(items!!, applicationContext)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener {_,_, position, _ ->
            val items: taskItem = items!![position] as taskItem
            items.checked = !items.checked
            adapter.notifyDataSetChanged()
        }
    }
}