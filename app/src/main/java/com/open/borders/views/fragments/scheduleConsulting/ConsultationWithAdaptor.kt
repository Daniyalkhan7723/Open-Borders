package com.open.borders.views.fragments.scheduleConsulting

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.open.borders.R
import com.open.borders.databinding.ConsultationWithLayoutBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.models.responseModel.CalenderModel
import com.open.borders.utils.Constants

class ConsultationWithAdaptor(
    private val context: Context,
    private val listener: CalenderListener
) :
    RecyclerView.Adapter<ConsultationWithAdaptor.TimeSlotViewHolder>() {

    var calenderList = mutableListOf<CalenderModel>()

    inner class TimeSlotViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = ConsultationWithLayoutBinding.bind(itemView)
        var name = binding.consultantTv1
        var type = binding.types
        var image = binding.selectImage1
        var consultantImage = binding.imageView
        var layout = binding.consultant1
//        var cardLayout = binding.timeCardId
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = mInflater.inflate(R.layout.consultation_with_layout, parent, false)
        return TimeSlotViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val items = calenderList[position]

        holder.name.text = items.name
        val image = "https:${items.image}"

        if (items.isSelected) {
//            holder.image.viewVisible()
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.actionlink_bg)
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.type?.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.consultation_bg_new)
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
            holder.type?.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
        }
//        else {
//            holder.image.viewGone()
//        }

        if (position == 0){
            holder.binding.types?.text = "(English, Español)"
        }
        if (position == 1){
            holder.binding.types?.text = "(English)"
        }

        Glide.with(context)
            .load(image)
            .into(holder.consultantImage)

        holder.itemView.setOnClickListener {
            listener.onNameClick(position, items)
        }

        holder.consultantImage.setOnClickListener {
            listener.onImageClick(position, image)
        }
    }

    override fun getItemCount(): Int {
        return calenderList.size
    }


    interface CalenderListener {
        fun onNameClick(index: Int, item: CalenderModel)
        fun onImageClick(index: Int, imageUrl: String)
    }
}
