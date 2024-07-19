package com.open.borders.customPopups

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.PopupSelectTimeBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.models.responseModel.TimeSlotModel
import com.open.borders.thirdPartyModules.loopView.LoopView
import com.open.borders.utils.Constants
import com.open.borders.views.activities.baseActivity.BaseActivity
import com.open.borders.views.fragments.scheduleConsulting.DateAndTimeViewModel
import com.open.borders.views.fragments.scheduleConsulting.SelectTimeAndDateDialogInterface
import com.open.borders.views.fragments.scheduleConsulting.TimeSlotAdaptor
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.ext.android.viewModel


@InternalSerializationApi
class SelectTimePopup(
    var mActivity: BaseActivity,
    private var listener: SelectTimeAndDateDialogInterface,
    private var date: String,
    private var calenderId: String,
    private var appointmentTypeId: String,
    private var timeZone: String
    ) : DialogFragment(), TimeSlotAdaptor.TimeSlotClick {
    private lateinit var binding: PopupSelectTimeBinding
    val viewModel: DateAndTimeViewModel by viewModel()
    private var myTimeSlotList = mutableListOf<TimeSlotModel>()
    private var hours = "01"
    private var minutes = "00"
    private var amPm = "AM"
    private var time = ""
    private var ctime = ""
    private var cDateTime = ""

    private val timeSlotAdator: TimeSlotAdaptor by lazy {
        TimeSlotAdaptor(requireContext(), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NO_FRAME,R.style.DialogTheme)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.popup_select_time, null, false
        )

        attachViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (isNetworkAvailable(requireContext())){
            viewModel.getTimeSlots(date, appointmentTypeId, calenderId, timeZone)
        }else{
            CustomToasts.failureToastCenterTop(requireContext(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE)
        }

//        setUpHours(binding.hoursLoopStarting)
//        setUpMinutes(binding.minuteLoopStarting)
//        setUpAmPm(binding.amPmLoopStarting)
//        setListeners()



        binding.saveTimeBtn.setOnClickListener {
            dismiss()
            listener.onSaveTimeClick(ctime, cDateTime)
        }
    }

//    private fun setListeners() {
//        binding.hoursLoopStarting?.setListener {
//            hours = mActivity.generateHoursList()[it]
////            binding.tvTime.text = "$hours:$minutes $amPm"
//            time = "$hours:$minutes $amPm"
//        }
//        binding.minuteLoopStarting?.setListener {
//            minutes = mActivity.generateMinutesList()[it]
////            binding.tvTime.text = "$hours:$minutes $amPm"
//            time = "$hours:$minutes $amPm"
//        }
//        binding.amPmLoopStarting?.setListener {
//            amPm = mActivity.generateAmPmList()[it]
////            binding.tvTime.text = "$hours:$minutes $amPm"
//            time = "$hours:$minutes $amPm"
//        }
//    }

    private fun setUpHours(loopView: LoopView) {
        loopView.setTypeface(mActivity.fontRegular)
        loopView.setItems(mActivity.generateHoursList())
        loopView.setInitPosition(0)
    }

    private fun setUpMinutes(loopView: LoopView) {
        loopView.setTypeface(mActivity.fontRegular)
        loopView.setItems(mActivity.generateMinutesList())
        loopView.setInitPosition(0)
    }

    private fun setUpAmPm(loopView: LoopView) {
        loopView.setTypeface(mActivity.fontRegular)
        loopView.setItems(mActivity.generateAmPmList())
        loopView.setInitPosition(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTimeSlotClick(index: Int, time: String, timeDate: String) {
        myTimeSlotList.forEach { item ->
            item.isSelected = false
        }
        myTimeSlotList[index].isSelected = true
        timeSlotAdator.notifyDataSetChanged()
        ctime = time
        cDateTime = timeDate
    }

    private fun setAdapter() {
        binding.timeslotRecyclerId.let {
            it?.layoutManager = GridLayoutManager(requireContext(), 2)
            it?.adapter = timeSlotAdator
        }
    }

    private fun attachViewModel() {
        viewModel.snackBarMessage.observe(viewLifecycleOwner) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
//                dialog?.dismiss()
                CustomToasts.failureToastCenterTop(requireContext(), msg)
            }
        }

        viewModel.getTimeSlotLiveData.observe(viewLifecycleOwner) {
            if(it.status){
                binding.shimmerFrameLayout?.viewGone()
                binding.timeslotRecyclerId?.viewVisible()
                myTimeSlotList = it.data.toMutableList()
                timeSlotAdator.timeSlotList = myTimeSlotList
                setAdapter()
            }
        }
    }
}