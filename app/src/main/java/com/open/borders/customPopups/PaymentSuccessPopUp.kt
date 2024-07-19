package com.open.borders.customPopups

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.DialogFragment
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.BugsReportPopupLayoutBinding
import com.open.borders.databinding.PaymentPopupLayoutBinding
import com.open.borders.databinding.SuccessPaymentPopupLayoutBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class PaymentSuccessPopUp(
    private var listener: PaymentSuccessPopInterface,
    private var listenerDailogueDismiss: DialogueDismiss
) : DialogFragment() {

    private lateinit var binding: SuccessPaymentPopupLayoutBinding


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(true)
        binding = SuccessPaymentPopupLayoutBinding.bind(view)

        binding.imageIcon.setOnClickListener {
            listenerDailogueDismiss.onDialogueDismiss()
//            dismiss()
        }

        binding.clickEmailLayout.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                val email = "attorney@bordercrossinglaw.com"
                data = Uri.parse("mailto:$email")
            }
            startActivity(Intent.createChooser(emailIntent, ""))
        }



    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.success_payment_popup_layout, container, false)
        binding = SuccessPaymentPopupLayoutBinding.bind(view)

        return view
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

    interface PaymentSuccessPopInterface {
        fun onSuccessEmailUsInformationClick(Email: String)

    }

    interface DialogueDismiss{
        fun onDialogueDismiss()
    }

}