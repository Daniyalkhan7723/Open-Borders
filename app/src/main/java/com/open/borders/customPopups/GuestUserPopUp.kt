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
import com.open.borders.databinding.GuestUserPopupBinding
import com.open.borders.databinding.PopupRestartConfirmationBinding
import com.open.borders.models.responseModel.PlanPrice
import com.open.borders.views.fragments.guide.guideQuestions.EvaluationAdapter
import com.open.borders.views.fragments.guide.guideQuestions.GuideAnswerAdapter
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class GuestUserPopUp(
    private var listener: GuestPopInterface
) : DialogFragment() {

    private lateinit var binding: GuestUserPopupBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(true)
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
        val view = layoutInflater.inflate(R.layout.guest_user_popup, container, false)
        binding = GuestUserPopupBinding.bind(view)
        binding.popUpLayout.setOnClickListener { dismiss() }

        binding.guestButton.setOnClickListener {
            dismiss()
        }
        binding.signIn.setOnClickListener {
            listener.onPopUpCreateAccountClick()
        }
        binding.signUp.setOnClickListener {
            listener.onSignUpClick()
        }
        return view
    }

    interface GuestPopInterface {
        fun onPopUpCreateAccountClick()
        fun onSignUpClick()
    }

}