package com.open.borders.customPopups

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.PopupScheduleConsultationBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.views.fragments.scheduleConsulting.SelectTimeAndDateDialogInterface


class ScheduleConsultationPopup(
    private var listener: SelectTimeAndDateDialogInterface
) : DialogFragment() {
    private lateinit var binding: PopupScheduleConsultationBinding
    private var consultationType: String? = null
    private var consultationWith: String? = null

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
            R.layout.popup_schedule_consultation, null, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        radioCheck()
        consultantSelection()
        consultationListener()


    }

    private fun radioCheck() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            consultationType = if (R.id.radioPhoneCall == checkedId) {
                "1"
            } else {
                "2"
            }
        }
    }

    private fun consultantSelection(){
        val selectionImage1 = binding.selectImage1
        val selectionImage2 = binding.selectImage2

        binding.consultant1?.setOnClickListener{
            selectionImage1?.viewVisible()
            selectionImage2?.viewGone()
            consultationWith = binding.consultantTv1?.text.toString()
        }

        binding.consultant2?.setOnClickListener{
            selectionImage1?.viewGone()
            selectionImage2?.viewVisible()
            consultationWith = binding.consultantTv2?.text.toString()
        }
    }

    private fun consultationListener(){
        binding.continueButton?.setOnClickListener{
            if (consultationType.isNullOrEmpty()){
                CustomToasts.failureToastCenterTop(requireActivity(), getString(R.string.please_select_consultation_type))
            }else if (consultationWith.isNullOrEmpty()){
                CustomToasts.failureToastCenterTop(requireActivity(), getString(R.string.please_select_consultant))
            }
            else{
                dismiss()
                listener.onContinueConsultationClick(consultationType!!, consultationWith!!)
            }
        }
    }
}