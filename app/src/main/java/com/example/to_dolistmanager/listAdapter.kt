package com.example.to_dolistmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView

class listAdapter(private val dataset: ArrayList<*>, mContext: Context): ArrayAdapter<Any?>(mContext, R.layout.task, dataset) {
    private class ItemHolder {
        lateinit var taskName: TextView
        lateinit var checkBox: CheckBox
        lateinit var taskDate: TextView
        lateinit var taskDesc: TextView
    }

    override fun getCount(): Int {
        return dataset.size
    }

    override fun getItem(position: Int): taskItem {
        return dataset[position] as taskItem
    }

    override fun getView(position: Int, converter: View?, parent: ViewGroup): View{
        var converter = converter
        val itemHolder: ItemHolder
        val result: View
        if(converter == null){
            itemHolder = ItemHolder()
            converter = LayoutInflater.from(parent.context).inflate(R.layout.task, parent, false)

            itemHolder.taskName = converter.findViewById<TextView>(R.id.taskName)
            itemHolder.checkBox = converter.findViewById<CheckBox>(R.id.checkBox)
            itemHolder.taskDate = converter.findViewById<TextView>(R.id.taskDate)
            itemHolder.taskDesc = converter.findViewById<TextView>(R.id.taskDesc)

            result = converter
            converter.tag = itemHolder
        }else{
            itemHolder = converter.tag as ItemHolder
            result = converter
        }

        val item: taskItem = getItem(position)
        itemHolder.taskName.text = item.name
        itemHolder.taskDate.text = item.date
        itemHolder.taskDesc.text = item.description
        itemHolder.checkBox.isChecked = item.checked

        return result
    }
}