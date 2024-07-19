package com.open.borders.customPopups

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.open.borders.R
import com.open.borders.databinding.EvaluationPopupLayoutBinding
import com.open.borders.databinding.LogoutConfirmationLayoutBinding
import com.open.borders.databinding.PopupRestartConfirmationBinding
import com.open.borders.views.fragments.guide.guideQuestions.EvaluationAdapter
import com.open.borders.views.fragments.guide.guideQuestions.GuideAnswerAdapter
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class LogOutConfirmationPopUp(
    private var listener: LogoutInterface,
    private var type: String
) : DialogFragment() {
    private lateinit var binding: LogoutConfirmationLayoutBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.logout_confirmation_layout, container, false)
        binding = LogoutConfirmationLayoutBinding.bind(view)

        if (type == "updatePayment") {
            binding.tvTitle.text =
                getString(R.string.do_you_want_to_update_your_credit_card)
        }

        binding.btnNo.setOnClickListener {
            dismiss()

        }
        binding.btnYes.setOnClickListener {
            dismiss()
            listener.onPopUpYesClick(type)
        }
        return view
    }

    interface LogoutInterface {
        fun onPopUpYesClick(type: String)
        fun onPopUpNoClick(type: String)
    }

}