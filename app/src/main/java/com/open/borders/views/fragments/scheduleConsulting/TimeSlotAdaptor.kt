package com.open.borders.views.fragments.scheduleConsulting

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.ConsultationTypeLayoutBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.models.responseModel.TimeSlotModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeSlotAdaptor(
    private val context: Context,
    private val listener: TimeSlotClick
    ) :
    RecyclerView.Adapter<TimeSlotAdaptor.TimeSlotViewHolder>(){

    var timeSlotList = mutableListOf<TimeSlotModel>()

    inner class TimeSlotViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = ConsultationTypeLayoutBinding.bind(itemView)
        var name = binding.name
        var layout = binding.consultant1

//        var cardLayout = binding.timeCardId
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = mInflater.inflate(R.layout.time_slot_layout, parent, false)
        return TimeSlotViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val items = timeSlotList[position]

        val inputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+SSSS", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        val date = LocalTime.parse(items.time, inputFormatter)
        val formattedDate = outputFormatter.format(date)

//        holder.name.text = items.time.toDate().formatTo("hh-mm a")
        holder.name.text = formattedDate.toString()

        if (items.isSelected){
            holder.binding.selectImage1.viewVisible()
//            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.select_answer_bg)
//            holder.name.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        else{
            holder.binding.selectImage1.viewGone()
//            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.bg_popup)
//            holder.name.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        holder.itemView.setOnClickListener{
            listener.onTimeSlotClick(position, formattedDate, items.time)
        }
    }

    override fun getItemCount(): Int {
        return timeSlotList.size
    }

    interface TimeSlotClick {
        fun onTimeSlotClick(index: Int, time: String, timeDate: String)
    }
}
