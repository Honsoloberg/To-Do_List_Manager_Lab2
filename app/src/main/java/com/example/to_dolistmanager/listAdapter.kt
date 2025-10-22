package com.example.to_dolistmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Filter
import android.widget.TextView

class listAdapter(private val dataset: ArrayList<taskItem>, mContext: Context): ArrayAdapter<taskItem>(mContext, R.layout.task, dataset) {

    private var filteredItems: ArrayList<taskItem> = ArrayList(dataset)
    private class ItemHolder {
        lateinit var taskName: TextView
        lateinit var checkBox: CheckBox
        lateinit var taskDate: TextView
        lateinit var taskDesc: TextView
    }

    override fun getCount(): Int {
        return filteredItems.size
    }

    override fun getItem(position: Int): taskItem {
        return filteredItems[position]
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

        itemHolder.taskName.setBackgroundColor(item.color)


        return result
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val query = constraint?.toString()?.trim()?.lowercase() ?:""
                val results = FilterResults()

                if(query.isEmpty()){
                    results.values = ArrayList(dataset)
                    results.count = dataset.size
                } else {
                    val filteredList = ArrayList<taskItem>()
                    for (t in dataset){
                        val title = t.name?.lowercase() ?: ""
                        val desc = t.description?.lowercase() ?: ""
                        val date = t.date?.lowercase() ?: ""

                        if(title.contains(query) || desc.contains(query) || date.contains(query)){
                            filteredList.add(t)
                        }
                    }

                    results.values = filteredList
                    results.count = filteredList.size
                }

                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = (results?.values as? ArrayList<taskItem>) ?: ArrayList()
                notifyDataSetChanged()
            }
        }
    }

    fun updateDataset(newDataSet: ArrayList<taskItem>){
        filteredItems = ArrayList(newDataSet)
        notifyDataSetChanged()
    }
}
