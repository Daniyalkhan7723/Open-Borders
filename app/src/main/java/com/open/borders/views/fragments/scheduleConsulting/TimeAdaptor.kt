package com.open.borders.views.fragments.scheduleConsulting

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.AcuityItemRvDateBinding
import com.open.borders.extensions.isToNewDate
import com.open.borders.extensions.sFormat
import com.open.borders.models.responseModel.AcuityModel
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeAdaptor(
    private val context: Context,
    private val listener: OnTimeClickInterface
) :
    RecyclerView.Adapter<TimeAdaptor.TimeViewHolder>() {

    var timeList = mutableListOf<AcuityModel>()
    var outputText: String? = null
    var UTCText: String? = null

    inner class TimeViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = AcuityItemRvDateBinding.bind(itemView)
        var time = binding.tvTime

//        var cardLayout = binding.timeCardId
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<AcuityModel>) {
        this.timeList = list
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = mInflater.inflate(R.layout.acuity_item_rv_date, parent, false)
        return TimeViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val items = timeList[position]

//        val inputFormatter =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+SSSS", Locale.ENGLISH)
//        val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
//        val date = LocalTime.parse(items.time, inputFormatter)
//        val formattedDate = outputFormatter.format(date)


//        val formattedDate = items.time.sDate().sFormat("hh:mm")

//        val formattedDate = utcToLocalTimeZone("yyyy-MM-dd'T'HH:mm:ss+SSSS" ,"hh:mm", items.time )\


//        val dtStart = items.time
//        val format =  SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
//        var date: Date? = null
//        try {
//            date = format.parse(dtStart);
//            UTCText = getDateInUTC(date)
//
//        } catch (e:Exception) {
//            e.printStackTrace();
//        }

        try {
            val outputFormat: DateFormat = SimpleDateFormat("hh:mm a")
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss")
            val date = inputFormat.parse(items.time)
            outputText = date?.let { outputFormat.format(it) }
            Log.e("FormattedDate", outputText.toString())

            holder.time.text = outputText


        } catch (e: Exception) {
            e.printStackTrace()
        }

        val formattedDate = items.time.isToNewDate()?.sFormat()
//        print(formattedDate)
//
//        holder.time.text = formattedDate

        if (items.isSelected) {
            holder.time.background = ContextCompat.getDrawable(context, R.drawable.actionlink_bg)
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        holder.itemView.setOnClickListener {
            outputText?.let { it1 -> listener.onTimeClick(position, it1, items.time) }
        }
    }

    override fun getItemCount(): Int {
        return timeList.size
    }

    interface OnTimeClickInterface {
        fun onTimeClick(index: Int, time: String, timeDate: String)
    }


    fun parseDateToddMMyyyy(time: String?): String? {
        val inputPattern = "yyyy-MM-dd'T'hh:mm:ssZ"
        val outputPattern = "hh:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    @Throws(ParseException::class)
    fun getTimeStampFromDateTime(mDateTime: String?, mDateFormat: String?): Long {
        val dateFormat = SimpleDateFormat(mDateFormat)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(mDateTime)
        return date.time
    }

    fun getDateInUTC(date: Date?): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val dateAsString = sdf.format(date)
        println(dateAsString)
        return dateAsString
    }
}

