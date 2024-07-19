package com.open.borders.views.fragments.flatFee

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.AcuityItemRvDateBinding
import com.open.borders.databinding.SelectMoneyItemBinding
import com.open.borders.databinding.SpinnerItemLayoutBinding
import com.open.borders.extensions.*
import com.open.borders.models.responseModel.AcuityModel
import com.open.borders.models.responseModel.PlanPrice
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SelectPriceAdapter(
    private val context: Context,
    private val listener: SelectPriceInterface,
) :
    RecyclerView.Adapter<SelectPriceAdapter.TimeViewHolder>() {

    var priceList = mutableListOf<PlanPrice>()

    inner class TimeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = SelectMoneyItemBinding.bind(itemView)
        var priceTv = binding.title

//        var cardLayout = binding.timeCardId
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = mInflater.inflate(R.layout.select_money_item, parent, false)
        return TimeViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val items = priceList[position]

        val mPrice = items.price.formatDecimalSeparator()
        holder.priceTv.text = "$${mPrice}"

        holder.itemView.setOnClickListener {
            listener.onPriceClick(position, items)
        }
    }

    override fun getItemCount(): Int {
        return priceList.size
    }

    interface SelectPriceInterface {
        fun onPriceClick(index: Int, data: PlanPrice)
    }

    private fun Int.formatDecimalSeparator(): String {
        return toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    }
}

