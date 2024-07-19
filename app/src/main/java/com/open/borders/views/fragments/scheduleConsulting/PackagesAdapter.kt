package com.open.borders.views.fragments.scheduleConsulting

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import com.open.borders.databinding.PackagesRecyclerLayoutBinding
import com.open.borders.extensions.formatDecimalSeparator
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.extensions.viewGone
import com.open.borders.models.responseModel.GetPackagesModel
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class PackagesAdapter(
    private var context: BaseActivity,
    private var listener: PackagesInterface
) :
    RecyclerView.Adapter<PackagesAdapter.PackagesViewHolder>() {

    var packagesList = mutableListOf<GetPackagesModel>()

    inner class PackagesViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = PackagesRecyclerLayoutBinding.bind(itemView)

        var titleTv = binding.titleText
        var amountTv = binding.amountTv
        var imageView = binding.imageView
        var moreText = binding.viewDetail
        var packageLayout = binding.packagesLayoutId
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesViewHolder {
        val view = mInflater.inflate(R.layout.packages_recycler_layout, parent, false)
        return PackagesViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: PackagesViewHolder, position: Int) {
        val items = packagesList[position]
        holder.moreText.viewGone()
        val image = com.open.borders.utils.Constants.IMAGE_BASE_URL + items.image

        when (context.getCurrentLanguage()) {
            "es" -> {
                holder.titleTv.text = items.plan_name_es

            }
            else -> {
                holder.titleTv.text = items.plan_name
            }
        }


        val amount = items.amount?.formatDecimalSeparator()
        if (items.plan_type == "recurring") {
            when (context.getCurrentLanguage()) {
                "es" -> {
                    holder.amountTv.text =
                        "$${amount} ${context.getString(R.string.every_month_for)} ${items.duration} ${items.recurring_period_es}"
                }
                else -> {
                    holder.amountTv.text =
                        "$${amount} ${context.getString(R.string.every_month_for)} ${items.duration} ${items.recurring_period}s"
                }
            }
            if (items.plan_prices?.size!! > 1) {

                Log.d("vvvvvvvvv", "check")
                Log.d("vvvvvvvvv", "" + items.duration)
                if (items.recurring_period == "every_month") {
                    holder.amountTv.text =
                        "${context.getString(R.string.from_)} $${amount} ${context.getString(R.string.every_month)}"
                } else {
                    holder.amountTv.text =
                        "${context.getString(R.string.from_)} $${amount} ${context.getString(R.string.every_month)}"
                }
            }

        } else {
            holder.amountTv.text = "$${amount}"
            if (items.plan_prices?.size!! > 1) {
                holder.amountTv.text =
                    "${context.getString(R.string.from_)} $${amount}"
            }
        }

        Glide.with(context)
            .load(image)
            .into(holder.imageView)

        holder.itemView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.packageLayout.background =
                        ContextCompat.getDrawable(context, R.drawable.actionlink_bg)
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                    holder.amountTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                MotionEvent.ACTION_UP -> {
                    holder.packageLayout.background =
                        ContextCompat.getDrawable(context, R.drawable.drawable_bg_with_border)
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
                    holder.amountTv.setTextColor(ContextCompat.getColor(context, R.color.brown_new))

                    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as AppCompatActivity,
                        holder.imageView as View,
                        "flatImage"
                    )
                    listener.onPackagesItemClick(items, position, option)

                }
                MotionEvent.ACTION_CANCEL -> {
                    holder.packageLayout?.background =
                        ContextCompat.getDrawable(context, R.drawable.drawable_bg_with_border)
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
                    holder.amountTv.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
                }
            }
            true
        }


//        holder.itemView.setOnClickListener {
//            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(context as AppCompatActivity, holder.imageView as View, "flatImage")
//            listener.onPackagesItemClick(items, position, option)
//        }

        holder.imageView.setOnClickListener {
            listener.onImageClick(position, items.image.toString())
        }
    }

    override fun getItemCount(): Int {
        return packagesList.size
    }

}