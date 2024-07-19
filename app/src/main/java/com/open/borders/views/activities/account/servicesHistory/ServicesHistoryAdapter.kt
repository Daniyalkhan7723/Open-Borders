package com.open.borders.views.activities.account.servicesHistory

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.open.borders.R
import com.open.borders.databinding.ServicesHistoryLayoutBinding
import com.open.borders.extensions.formatDecimalSeparator
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.extensions.setHtmlText
import com.open.borders.models.generalModel.historyModels.PurchesPlanHistory


class ServicesHistoryAdapter(
    private var context: Context,
    private val listener: ServicesHistoryInterface

) : RecyclerView.Adapter<ServicesHistoryAdapter.ServicesHistoryViewHolder>() {

    var getServicesHistoryList = mutableListOf<PurchesPlanHistory>()

    inner class ServicesHistoryViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = ServicesHistoryLayoutBinding.bind(itemView)

        var titleTv = binding.titleText
        var descriptionTv = binding.descriptionTv
        var serviceHistoryLayout = binding.servicesHistoryLayout

        //        var viewDetailTv = binding.viewDetail.viewGone()
        var imageView = binding.imageView
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesHistoryViewHolder {
        val view = mInflater.inflate(R.layout.services_history_layout, parent, false)
        return ServicesHistoryViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ServicesHistoryViewHolder, position: Int) {
        val items = getServicesHistoryList[position]
        val image = com.open.borders.utils.Constants.IMAGE_BASE_URL + items.plan.image
//        holder.descriptionTv.setHtmlText(items.plan.description)

//        holder.titleTv.text = items.amount.toString()
//        holder.descriptionTv.setHtmlText(items.consultation_with)

        val amount = items.plan.amount.formatDecimalSeparator()
        when (context.getCurrentLanguage()) {
            "es" -> {
                holder.titleTv.text = items.plan.plan_name_es

                if (items.plan.plan_type == "recurring") {

                    if (items.plan.duration == 1000) {
                        holder.descriptionTv.text =
//                            "$${amount} ${context.getString(R.string.every_month_for)} ${items.plan.duration} ${items.plan.recurring_period_es}"
                            "$${amount} ${context.getString(R.string.every_month)}"
                    } else {
                        holder.descriptionTv.text =
                            "$${amount} ${context.getString(R.string.every_month_for)} ${items.plan.duration} ${items.plan.recurring_period_es}"
//                            "$${amount} ${context.getString(R.string.every_month)}"
                    }

                } else {
                    holder.descriptionTv.text = "$${amount}"
                }


            }
            else -> {
                holder.titleTv.text = items.plan.plan_name
                if (items.plan.plan_type == "recurring") {

                    if (items.plan.duration == 1000) {
                        holder.descriptionTv.text =
                            "$${amount} ${context.getString(R.string.every_month)}"
                    } else {
                        holder.descriptionTv.text =
                            "$${amount} ${context.getString(R.string.every_month_for)} ${items.plan.duration} ${items.plan.recurring_period}s"
                    }

//                    holder.descriptionTv.text =
//                        "$${amount} ${context.getString(R.string.every_month_for)} ${items.plan.duration} ${items.plan.recurring_period}s"
////                        "$${amount} ${context.getString(R.string.every_month)}"
                } else {
                    holder.descriptionTv.text = "$${amount}"
                }
            }
        }

        Glide.with(context)
            .load(image)
            .into(holder.imageView)

        holder.itemView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.serviceHistoryLayout.background =
                        ContextCompat.getDrawable(context, R.drawable.actionlink_bg)
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                    holder.descriptionTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                }
                MotionEvent.ACTION_UP -> {
                    holder.serviceHistoryLayout.background =
                        ContextCompat.getDrawable(context, R.drawable.drawable_bg_with_border)
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
                    holder.descriptionTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.brown_new
                        )
                    )

                    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as AppCompatActivity,
                        holder.imageView as View,
                        "flatImage"
                    )
                    listener.onItemClick(position, items, option)

                }
                MotionEvent.ACTION_CANCEL -> {
                    holder.serviceHistoryLayout?.background =
                        ContextCompat.getDrawable(context, R.drawable.drawable_bg_with_border)
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
                    holder.descriptionTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.brown_new
                        )
                    )
                }
            }
            true
        }


        holder.imageView.setOnClickListener {
            listener.onImageClick(position, image)
        }

//        holder.itemView.setOnClickListener {
//            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                context as AppCompatActivity,
//                holder.imageView as View,
//                "flatImage"
//            )
//            listener.onItemClick(position, items, option)
//        }
    }

    override fun getItemCount(): Int {
        return getServicesHistoryList.size
    }

    interface ServicesHistoryInterface {
        fun onImageClick(index: Int, imageUrl: String)
        fun onItemClick(index: Int, data: PurchesPlanHistory, option: ActivityOptionsCompat)
    }
}