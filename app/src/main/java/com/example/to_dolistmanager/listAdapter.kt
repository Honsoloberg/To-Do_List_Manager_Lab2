package com.example.to_dolistmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Filter
import android.widget.TextView
import java.util.Locale


class listAdapter(private val dataset: ArrayList<taskItem>, mContext: Context): ArrayAdapter<Any?>(mContext, R.layout.task, dataset) {

    private var listOriginal: ArrayList<taskItem> = dataset
    private var listDisplayed: ArrayList<taskItem> = dataset
//    var inflater: LayoutInflater?


    private class ItemHolder {
        lateinit var taskName: TextView
        lateinit var checkBox: CheckBox
        lateinit var taskDate: TextView
        lateinit var taskDesc: TextView
    }

    override fun getCount(): Int {
        return listDisplayed.size
    }

    override fun getItem(position: Int): taskItem {
        return listOriginal[position] as taskItem
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

//    override fun getFilter(): Filter {
//        val filter: Filter = object : Filter() {
//            protected override fun publishResults(
//                constraint: CharSequence?,
//                results: FilterResults
//            ) {
//                mDisplayed = results.values as ArrayList<taskItem> // has the filtered values
//                notifyDataSetChanged() // notifies the data with new filtered values
//            }
//
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                var constraint = constraint
//                val results: FilterResults =
//                    FilterResults() // Holds the results of a filtering operation in values
//                val FilteredArrList: ArrayList<taskItem?> = ArrayList<taskItem?>()
//
//                if (mOriginal == null) {
//                    mOriginalValues =
//                        ArrayList<Product?>(mDisplayedValues) // saves the original data in mOriginalValues
//                }
//
//                /********
//                 *
//                 * If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
//                 * else does the Filtering and returns FilteredArrList(Filtered)
//                 *
//                 */
//                if (constraint == null || constraint.length == 0) {
//                    // set the Original result to return
//
//                    results.count = mOriginalValues.size()
//                    results.values = mOriginalValues
//                } else {
//                    constraint = constraint.toString().lowercase(Locale.getDefault())
//                    for (i in 0..<mOriginalValues.size()) {
//                        val data: String = mOriginalValues.get(i).name
//                        if (data.lowercase(Locale.getDefault()).startsWith(constraint.toString())) {
//                            FilteredArrList.add(
//                                Product(
//                                    mOriginalValues.get(i).name,
//                                    mOriginalValues.get(i).price
//                                )
//                            )
//                        }
//                    }
//                    // set the Filtered result to return
//                    results.count = FilteredArrList.size
//                    results.values = FilteredArrList
//                }
//                return results
//            }
//        }
//        return filter
//    }
}