package com.open.borders.views.fragments.scheduleConsulting

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.AcuityItemLayoutBinding
import com.open.borders.extensions.isToNewDate
import com.open.borders.models.responseModel.AcuityModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AcuityDateTimeAdaptor(
    private val context: Context,
    private val listener: OnAcuityDateTimeInterface,

    ) :
    RecyclerView.Adapter<AcuityDateTimeAdaptor.AcuityDateTimeViewHolder>() {

    var acuityDateTimeList = mutableListOf<List<AcuityModel>>()

    inner class AcuityDateTimeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = AcuityItemLayoutBinding.bind(itemView)
        var timeRecyclerView = binding.rvTime
        var day = binding.tvDayName
        var monthDate = binding.tvDate
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcuityDateTimeViewHolder {
        val view = mInflater.inflate(R.layout.acuity_item_layout, parent, false)
        return AcuityDateTimeViewHolder(view)
    }
    fun updateList(list : MutableList<List<AcuityModel>>) {
        this.acuityDateTimeList = list
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: AcuityDateTimeViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val items = acuityDateTimeList[position]

        if (!items.isNullOrEmpty()) {
            val time = items.first().time

            try {
                val outputFormat: DateFormat = SimpleDateFormat("MMMM dd", Locale.US)
                val outputFormat1: DateFormat = SimpleDateFormat("EEEE", Locale.US)
                val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssss", Locale.US)
                val date = inputFormat.parse(time)
                val outputText = date?.let { outputFormat.format(it) }
                val outputText1 = date?.let { outputFormat1.format(it) }
                if (outputText != null) {
                    Log.e("FormattedDate", outputText)
                }

                holder.day.text = outputText1
                holder.monthDate.text = outputText
            } catch (e: Exception) {
                e.printStackTrace()
            }


//            try {
//                holder.day.text =
//                    android.text.format.DateFormat.format("EEEE", time.isToNewDate()).toString()
//                holder.monthDate.text =
//                    android.text.format.DateFormat.format("MMMM dd", time.isToNewDate()).toString()
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

        }

        holder.timeRecyclerView.layoutManager = LinearLayoutManager(context)
        val adaptor = TimeAdaptor(context, object : TimeAdaptor.OnTimeClickInterface {
            override fun onTimeClick(index: Int, time: String, timeDate: String) {
                listener.onTimeClick(position, index, time, timeDate)
            }

        })
//        adaptor.timeList.clear()
//        adaptor.notifyDataSetChanged()
//        adaptor.timeList = items.toMutableList()
        adaptor.updateList(items.toMutableList())
        Log.e("ChildRecyclerItems", items.toString())
        holder.timeRecyclerView.adapter = adaptor

    }

    override fun getItemCount(): Int {
        return acuityDateTimeList.size
    }


    interface OnAcuityDateTimeInterface {
        fun onTimeClick(index: Int, childIndex: Int, time: String, timeDate: String)
    }

}



