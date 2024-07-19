package com.open.borders.views.fragments.scheduleConsulting

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.AnswersLayoutBinding
import com.open.borders.databinding.ConsultationTypeLayoutBinding
import com.open.borders.databinding.ConsultationWithLayoutBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.models.responseModel.AppointmentTypeModel
import com.open.borders.models.responseModel.CalenderModel

class ConsultationTypeAdaptor(
    private val context: Context,
    private val listener: ConsultationType
    ) :
    RecyclerView.Adapter<ConsultationTypeAdaptor.ConsultationTypeViewHolder>(){

    var consultationTypeList = mutableListOf<AppointmentTypeModel>()

    inner class ConsultationTypeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = ConsultationTypeLayoutBinding.bind(itemView)
        var name = binding.name
        var layout = binding.consultant1

//        var cardLayout = binding.timeCardId
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationTypeViewHolder {
        val view = mInflater.inflate(R.layout.consultation_type_layout, parent, false)
        return ConsultationTypeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ConsultationTypeViewHolder, position: Int) {
        val items = consultationTypeList[position]

        holder.name.text = items.name

        if (items.isSelected){
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.actionlink_bg)
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.white))
//            holder.binding.selectImage1.viewVisible()
//            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.select_answer_bg)
//            holder.name.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else{
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.consultation_bg_new)
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
//            holder.binding.selectImage1.viewGone()
//            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.bg_popup)
//            holder.name.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        holder.itemView.setOnClickListener{
            listener.onTypeClick(position, items)
        }
    }

    override fun getItemCount(): Int {
        return consultationTypeList.size
    }


    interface ConsultationType {
        fun onTypeClick(index: Int, item: AppointmentTypeModel)
    }
}
