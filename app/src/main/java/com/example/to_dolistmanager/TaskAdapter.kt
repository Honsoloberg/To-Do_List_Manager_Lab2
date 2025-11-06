package com.example.to_dolistmanager

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Intent
import android.widget.TextView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.net.Uri
import java.io.File
class TaskAdapter (
    private val context: Context,
    private val items: List<taskItem>,
    private val dbHelper: DatabaseHelper
    ): ArrayAdapter<taskItem>(context,0,items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //declare all task views
        val item = items[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.task, parent, false)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val taskName = view.findViewById<TextView>(R.id.taskName)
        val date = view.findViewById<TextView>(R.id.taskDate)
        val description = view.findViewById<TextView>(R.id.taskDesc)
        val imageView = view.findViewById<ImageView>(R.id.taskImage)
        val background = view.findViewById<RelativeLayout>(R.id.taskLayout)
        val imageUri = Uri.fromFile(File(item.image))


        checkBox.isChecked = item.checked
        taskName.text=item.name
        date.text=item.date
        description.text=item.description
        background.setBackgroundColor(item.color)
        imageView.setImageURI(imageUri)

        //if checkbox is clicked
        checkBox.setOnClickListener {
            item.checked = checkBox.isChecked
            dbHelper.updateTaskCheck(item.name ?: "", item.checked)
        }

        //if anywhere other than check box is clicked, open the updateTask activity
        //also send all the task information to updateTask
        view.setOnClickListener { event ->
            if (!checkBox.isPressed) {
                val intent = Intent(context, UpdateTask::class.java)
                intent.putExtra("TASK_ID", item.id)
                intent.putExtra("TITLE", item.name)
                intent.putExtra("DATE", item.date)
                intent.putExtra("DESCRIPTION", item.description)
                intent.putExtra("COLOR", item.color)
                intent.putExtra("PICTURE", item.image)
                context.startActivity(intent)
            }
        }

        return view




    }


    }


