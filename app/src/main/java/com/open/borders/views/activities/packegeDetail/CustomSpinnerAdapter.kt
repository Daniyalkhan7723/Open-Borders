package com.open.borders.views.activities.packegeDetail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.open.borders.R
import com.open.borders.models.responseModel.PlanPrice

class CustomSpinnerAdapter(
    val context: Context,
    var dataSource: List<PlanPrice>,
    val planType: String,
    val recurringPeriod: String
) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.select_money_item, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        val price = dataSource.get(position).price.formatDecimalSeparator()
        if (planType == "recurring"){
            if (recurringPeriod == "month"){
                vh.label.text = "$ $price / month"
            }else{
                vh.label.text = "$ $price"
            }
        }else{
            vh.label.text = "$ $price"
        }

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val label: TextView

        init {
            label = row?.findViewById(R.id.title) as TextView
        }
    }

    private fun Int.formatDecimalSeparator(): String {
        return toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    }

}