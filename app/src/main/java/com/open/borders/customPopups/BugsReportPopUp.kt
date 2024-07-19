package com.open.borders.customPopups

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
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
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class BugsReportPopUp(
    private var listener: BugsReportInterface
) : DialogFragment() {

    private lateinit var binding: BugsReportPopupLayoutBinding
    private var description = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(true)
        binding = BugsReportPopupLayoutBinding.bind(view)

        binding.textEt.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP ->  v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        binding.textEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                //                binding.descriptionEt!!.backgroundTintList =
                //                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorDetailTv?.error = null
                binding.errorDetailTv?.viewGone()
            }
        })
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
        val view = layoutInflater.inflate(R.layout.bugs_report_popup_layout, container, false)
        binding = BugsReportPopupLayoutBinding.bind(view)
//        binding.bugsPopUpLayout.setOnClickListener { dismiss() }

//        binding.buttonId.setOnClickListener {
//            dismiss()
//        }
        binding.buttonId.setOnClickListener {
            description = binding.textEt.text.toString()
            if (description.isNullOrEmpty()) {
                binding.errorDetailTv?.viewVisible()
                binding.errorDetailTv?.text = "Please write error you noticed."
                binding.textEt?.requestFocus()

            } else {
                dismiss()
                listener.onSendButtonClick(description)
            }
        }


        binding.consultationLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.consultationLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_select_bg)
                    binding.tvConsult.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.icConsult.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                        )
                    }

                }
                MotionEvent.ACTION_UP -> {
                    binding.consultationLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_tv_bg)
                    binding.tvConsult.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.icConsult.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        )
                    }
                    listener.onConsultationClick()
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.consultationLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_tv_bg)
                    binding.tvConsult?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.icConsult.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        )
                    }

                }
            }
            true
        }

        binding.helpLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.helpLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_select_bg)
                    binding.tvHelp.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.icHelp.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                        )
                    }

                }
                MotionEvent.ACTION_UP -> {
                    binding.helpLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_tv_bg)
                    binding.tvHelp.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.icHelp.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        )
                    }

                    val help = binding.tvHelp.text.toString()
                    if (!help.isNullOrEmpty()) {
                        listener.onHelpEmailClick(help)
                    }

                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.helpLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_tv_bg)
                    binding.tvHelp.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.icHelp.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        )
                    }

                }
            }
            true
        }

        binding.phoneLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.phoneLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_select_bg)
                    binding.tvPhone.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.icPhone.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                        )
                    }

                }
                MotionEvent.ACTION_UP -> {
                    binding.phoneLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_tv_bg)
                    binding.tvPhone.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.icPhone.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        )
                    }

                    val phoneNo = binding.tvPhone.text.toString()
                    if (!phoneNo.isNullOrEmpty()){
                        listener.onPhoneCallClick(phoneNo)
                    }

                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.phoneLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bugs_pop_tv_bg)
                    binding.tvPhone.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.icPhone.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        )
                    }

                }
            }
            true
        }


        binding.imageIcon.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onResume() {
        val window: Window? = dialog!!.window
        val size = Point()
        // Store dimensions of the screen in `size`
        // Store dimensions of the screen in `size`
        val display: Display = window!!.getWindowManager().getDefaultDisplay()
        display.getSize(size)
        // Set the width of the dialog proportional to 75% of the screen width
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((size.x * 0.90).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        // Call super onResume after sizing
        // Call super onResume after sizing
        super.onResume()
    }

    interface BugsReportInterface {
        fun onSendButtonClick(description: String)
        fun onHelpEmailClick(Email: String)
        fun onPhoneCallClick(Phone: String)
        fun onConsultationClick()
    }

}