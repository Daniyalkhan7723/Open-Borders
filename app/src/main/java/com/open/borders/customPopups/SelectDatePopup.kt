package com.open.borders.customPopups

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.textview.MaterialTextView
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.kizitonwose.calendarview.utils.yearMonth
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.CalenderDayLayoutBinding
import com.open.borders.databinding.PopupSelectDateBinding
import com.open.borders.extensions.*
import com.open.borders.models.responseModel.DateModel
import com.open.borders.utils.Constants
import com.open.borders.views.fragments.scheduleConsulting.DateAndTimeViewModel
import com.open.borders.views.fragments.scheduleConsulting.SelectTimeAndDateDialogInterface
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.stopKoin
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@InternalSerializationApi
class SelectDatePopup(
    private var listener: SelectTimeAndDateDialogInterface,
    private var calenderId: String,
    private var appointmentTypeId: String
) : DialogFragment() {
    private lateinit var binding: PopupSelectDateBinding

    private var selectedDate: LocalDate? = null
    private var mMonth: String? = null
    val viewModel: DateAndTimeViewModel by viewModel()
    private val today = LocalDate.now()
    private val daysOfWeek = daysOfWeekFromLocale()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    private val currentMonth = YearMonth.now()
    private val startMonth = currentMonth.minusMonths(10)
    private val endMonth = currentMonth.plusMonths(10)
    private val timeZone = TimeZone.getDefault()

    private var datesList = mutableListOf<DateModel>()

    private var hitApiFirstTime = true
    private var hitApi = false

    private var mDate: String? = null
    private var textView: MaterialTextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val dialogHelper by inject<MaterialDialogHelper>()
//        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
//        setStyle(STYLE_NO_FRAME, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.popup_select_date, null, false
        )
        attachViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mMonth = currentMonth.toString()

        getDatesApiCall()


//        calenderListener()
        binding.saveDateBtn.setOnClickListener {
//            mDate = binding.calendarView.selectedDate?.date.toString()
            dismiss()

            listener.onSaveDateClick(selectedDate.toString(), timeZone.id)
        }
    }

    private fun getDatesApiCall() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getDates(mMonth.toString(), appointmentTypeId, calenderId, timeZone.id)
        } else {
            CustomToasts.failureToastCenterTop(
                requireContext(),
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calenderListener() {
        if (hitApiFirstTime) {
            binding.exOneCalendar?.setup(startMonth, endMonth, daysOfWeek.first())
            binding.exOneCalendar?.scrollToMonth(currentMonth)
            hitApiFirstTime = false
        }

        @SuppressLint("SimpleDateFormat")
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: com.kizitonwose.calendarview.model.CalendarDay
            val textView = CalenderDayLayoutBinding.bind(view).calendarDayText

            init {
                textView.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        for (day_ in datesList) {
                            val inputFormatter =
                                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
                            val outputFormatter = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH)
                            //val date = LocalTime.parse(day_.date, inputFormatter)
                            //val formattedDate = outputFormatter.format(date)
                            val parser = SimpleDateFormat("yyyy-MM-dd")
                            val formatter = SimpleDateFormat("dd")
                            val output: String =
                                formatter.format(parser.parse(day_.date))
                            if (output == outputFormatter.format(day.date)) {
                                if (selectedDate == day.date) {
                                    selectedDate = null
                                    binding.exOneCalendar?.notifyDayChanged(day)
                                } else {
                                    val oldDate = selectedDate
                                    selectedDate = day.date
                                    binding.exOneCalendar?.notifyDateChanged(day.date)
                                    oldDate?.let { binding.exOneCalendar?.notifyDateChanged(oldDate) }
                                }
                                binding.exOneCalendar?.notifyDayChanged(day)
                            }
                        }
                    }
                }
            }
        }

        binding.exOneCalendar?.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            @SuppressLint("SimpleDateFormat")
            override fun bind(
                container: DayViewContainer,
                day: com.kizitonwose.calendarview.model.CalendarDay
            ) {
                container.day = day
                textView = container.textView
                textView?.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {

                    when (day.date) {
                        selectedDate -> {
                            textView?.setTextColorRes(R.color.white)
                            textView?.setBackgroundResource(R.drawable.calender_selected_bg)
                        }
                        today -> {
                            textView?.setTextColorRes(R.color.red_color)
                            textView?.background = null
                        }
                        else -> {
                            textView?.setTextColorRes(R.color.black_80)
                            textView?.background = null
                        }
                    }
                    textView?.alpha = 0.3f
                    for (day_ in datesList) {
                        val parser = SimpleDateFormat("yyyy-MM-dd")
                        val formatter = SimpleDateFormat("d")
                        val output: String =
                            formatter.format(parser.parse(day_.date))
                        if (textView?.text.toString() == output) {
                            textView?.alpha = 1.0f
                        }
                    }

                } else {
                    textView?.setTextColorRes(R.color.grey_30)
                    textView?.background = null
                }

            }
        }

        binding.exOneCalendar?.monthScrollListener = {
            datesList.clear()
            binding.exOneCalendar?.notifyCalendarChanged()
//            textView?.alpha = 0.3f
            if (binding.exOneCalendar?.maxRowCount == 6) {
                binding.calenderHeader?.dateTv?.text = it.yearMonth.year.toString()
                binding.calenderHeader?.monthTv?.text = monthTitleFormatter.format(it.yearMonth)
            } else {
                // In week mode, we show the header a bit differently.
                // We show indices with dates from different months since
                // dates overflow and cells in one index can belong to different
                // months/years.
                val firstDate = it.weekDays.first().first().date
                val lastDate = it.weekDays.last().last().date
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    binding.calenderHeader?.dateTv?.text = firstDate.yearMonth.year.toString()
                    binding.calenderHeader?.monthTv?.text = monthTitleFormatter.format(firstDate)
                } else {
                    binding.calenderHeader?.monthTv?.text =
                        "${monthTitleFormatter.format(firstDate)} - ${
                            monthTitleFormatter.format(
                                lastDate
                            )
                        }"
                    if (firstDate.year == lastDate.year) {
                        binding.calenderHeader?.dateTv?.text = firstDate.yearMonth.year.toString()
                    } else {
                        binding.calenderHeader?.dateTv?.text =
                            "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    }
                }
            }

            mMonth = it.yearMonth.toString()
            getDatesApiCall()

        }

        binding.calenderHeader?.forwardIcon?.setOnClickListener {
            binding.exOneCalendar?.findFirstVisibleMonth()?.let {
                mMonth = it.yearMonth.toString()
//                getDatesApiCall()
                binding.exOneCalendar!!.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        binding.calenderHeader?.prevIcon?.setOnClickListener {
            binding.exOneCalendar?.findFirstVisibleMonth()?.let {
                mMonth = it.yearMonth.toString()
//                getDatesApiCall()
                binding.exOneCalendar!!.smoothScrollToMonth(it.yearMonth.previous)
            }
        }
    }


    override fun onResume() {
        val window: Window? = dialog!!.window
        val size = Point()
        // Store dimensions of the screen in `size`
        // Store dimensions of the screen in `size`
        val display: Display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        // Set the width of the dialog proportional to 75% of the screen width
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((size.x * 0.90).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        // Call super onResume after sizing
        // Call super onResume after sizing
        super.onResume()
    }


    private fun attachViewModel() {
        viewModel.snackBarMessage.observe(viewLifecycleOwner) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
//                dialog?.dismiss()
//                CustomToasts.showToast(requireActivity(), msg, false)
                CustomToasts.failureToastCenterTop(requireContext(), msg)
            }
        }

        viewModel.getDatesLiveData.observe(viewLifecycleOwner) {
            if (it.status) {
                binding.shimmerFrameLayout?.viewGone()
                binding.exOneCalendar?.viewVisible()
                datesList = it.data.toMutableList()
                calenderListener()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
