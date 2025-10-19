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

    var items: ArrayList<taskItem>? = null

    val TASK_RESULT_CODE = 100

    private lateinit var listView: ListView
    private lateinit var button: Button
    private lateinit var adapter: listAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById<SearchView>(R.id.searchBar)
        listView = findViewById<ListView>(R.id.listView)
        button = findViewById<Button>(R.id.button1)
        val textView = findViewById<TextView>(R.id.Title)
        val searchBar = findViewById<SearchView>(R.id.searchBar)

        items = ArrayList<taskItem>()

        //dummy tasks
        items!!.add(taskItem("Wash Clothes", "09-21-2025", "Sort laundry by color and fabric, load into washing machine.", false))
        items!!.add(taskItem("Sweep Kitchen", "09-21-2025", "this is a description", false))
        items!!.add(taskItem("Clean Bathroom", "09-21-2025", "this is a description", false))

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
        }

        button.setOnClickListener {
            startActivityForResult(Intent(this, NewTask::class.java), TASK_RESULT_CODE)
        }

//        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                var exists = items?.any {it.name?.contains(query as CharSequence, true) == true}
//
//                if (exists == true) {
//                    // if query exist within list we
//                    // are filtering our list adapter.
//                    adapter.filter(query)
//                } else {
//                    // if query is not present we are displaying
//                    // a toast message as no  data found..
//                    Toast.makeText(this@MainActivity, "No Task Found...", Toast.LENGTH_LONG).show()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                // if query text is change in that case we
//                // are filtering our adapter with
//                // new text on below line.
//                adapter.filter(newText)
//                adapter.notifyDataSetChanged()
//                return false
//            }
//        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == TASK_RESULT_CODE && resultCode == Activity.RESULT_OK){
            var title = data?.getStringExtra("title")
            var date = data?.getStringExtra("date")
            var description = data?.getStringExtra("description")
            items!!.add(taskItem(title, date, description, false))
            adapter.updateDataset(items!!)
        }
    }
}