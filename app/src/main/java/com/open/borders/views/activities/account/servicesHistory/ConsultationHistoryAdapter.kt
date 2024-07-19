package com.open.borders.views.activities.account.servicesHistory

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.ConsultationHistoryLayoutBinding
import com.open.borders.extensions.isToCDate
import com.open.borders.extensions.setHtmlText
import com.open.borders.models.generalModel.historyModels.ConsultationBookingDetail


class ConsultationHistoryAdapter(
    private var context: Context,
    private val listener: ConsultationHistoryInterface

) : RecyclerView.Adapter<ConsultationHistoryAdapter.ConsultationHistoryViewHolder>() {

    var getConsultationList = mutableListOf<ConsultationBookingDetail>()

    inner class ConsultationHistoryViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = ConsultationHistoryLayoutBinding.bind(itemView)

        var titleTv = binding.titleText
        var date = binding.date
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConsultationHistoryViewHolder {
        val view = mInflater.inflate(R.layout.consultation_history_layout, parent, false)
        return ConsultationHistoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ConsultationHistoryViewHolder, position: Int) {
        val items = getConsultationList[position]
//        val image = com.open.borders.utils.Constants.IMAGE_BASE_URL + items.plan.image
//        holder.titleTv.text = items.plan.plan_name
//        holder.descriptionTv.setHtmlText(items.plan.description)


        val date = android.text.format.DateFormat.format("MMMM dd, yyyy", items.date.isToCDate())
            .toString()

        holder.date.text = "on $date at ${items.consultation_time}"


//        holder.date.text = "on $output at ${items.consultation_time}"
        holder.titleTv.setHtmlText(items.consultation_with)

    }

    override fun getItemCount(): Int {
        return getConsultationList.size
    }

    interface ConsultationHistoryInterface {
        fun onImageClick(index: Int, imageUrl: String)
    }
}