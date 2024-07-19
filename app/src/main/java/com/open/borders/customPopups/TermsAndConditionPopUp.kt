package com.open.borders.customPopups

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.TermAndConditionPopupLayoutBinding
import com.open.borders.extensions.*
import com.open.borders.thirdPartyModules.htmltextview.HtmlResImageGetter
import com.open.borders.thirdPartyModules.htmltextview.HtmlTextView
import com.open.borders.utils.Constants
import com.open.borders.utils.Event
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.BaseActivity
import com.open.borders.views.activities.navigationHost.AuthenticationHost
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.scheduleConsulting.DateAndTimeViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@InternalSerializationApi
class TermsAndConditionPopUp(
    private var mActivity: BaseActivity,
    private var listener: TermAndCheckInterface
) : DialogFragment() {

    val viewModel: DateAndTimeViewModel by viewModel()
    private lateinit var binding: TermAndConditionPopupLayoutBinding

    private var termsAndCondition: String? = null
    private var termsAndConditionEs: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var termAndCheck: HtmlTextView? = view.findViewById(R.id.termAndCheck)

        termsAndCondition = SharePreferenceHelper.getInstance(requireContext()).getTermAndCondition()
        termsAndConditionEs = SharePreferenceHelper.getInstance(requireContext()).getTermAndConditionEs()

        binding.termsCheck.isClickable = true

        if (Constants.termsFromSignUp){
//            binding.termsCheck.viewGone()
            when ((activity as AuthenticationHost).getCurrentLanguage()) {
                "es" -> {
                    termAndCheck?.setHtml(
                        Html.fromHtml(termsAndConditionEs).toString(),
                        HtmlResImageGetter(mActivity), mActivity
                    )
//                termsAndConditionEs?.let { binding.termAndCheck?.setHtmlText(it) }
                }
                else -> {

                    if(termsAndCondition!=null){
                        termAndCheck?.setHtml(
                            Html.fromHtml(termsAndCondition).toString(),
                            HtmlResImageGetter(mActivity), mActivity
                        )
                    }
//                termsAndCondition?.let { binding.termAndCheck?.setHtmlText(it) }
                }
            }
        }else{
            binding.termsCheck.viewVisible()
            when ((activity as HomeHost).getCurrentLanguage()) {
                "es" -> {
                    termAndCheck?.setHtml(
                        Html.fromHtml(termsAndConditionEs).toString(),
                        HtmlResImageGetter(mActivity), mActivity
                    )
//                termsAndConditionEs?.let { binding.termAndCheck?.setHtmlText(it) }
                }
                else -> {
                    termAndCheck?.setHtml(
                        Html.fromHtml(termsAndCondition).toString(),
                        HtmlResImageGetter(mActivity), mActivity
                    )
//                termsAndCondition?.let { binding.termAndCheck?.setHtmlText(it) }
                }
            }
        }

        binding.cross?.setOnClickListener {
            Constants.termsFromSignUp = false
            dismiss()
        }

//        if (isNetworkAvailable(requireContext())){
//            viewModel.getTermsAndCondition()
//        }else{
//            CustomToasts.showToast(requireActivity(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false)
//        }

//        binding.termAndCheck?.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view =
            layoutInflater.inflate(R.layout.term_and_condition_popup_layout, container, false)
        binding = TermAndConditionPopupLayoutBinding.bind(view)

        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
//        attachViewModel()

        binding.termsCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dismiss()
                lifecycleScope.launch {
                    SharePreferenceHelper.getInstance(requireContext()).saveFirstTimeTrue("true")
                }
                listener.onCheckClickYes()
            } else {
                dismiss()
            }
        }

        return view
    }

    interface TermAndCheckInterface {
        fun onCheckClickYes()
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
                CustomToasts.failureToastCenterTop(requireContext(), msg)
            }
        }

        viewModel.getTermsAndCoditionLiveData.observe(viewLifecycleOwner) {
            if (it.content.rendered.isNotEmpty()){
//                binding.shimmerFrameLayout?.viewGone()
                binding.termAndCheck?.viewVisible()
//                binding.termAndCheck?.setNewHtmlText(it.content.rendered)
            }
        }
    }


    fun setupProgressDialog(
        showHideProgress: LiveData<Event<Boolean>>,
        dialogHelper: MaterialDialogHelper
    ) {
        var mDialog: MaterialDialog? = null
        showHideProgress.observe(this) {
            if (!it.consumed) it.consume()?.let { flag ->
                if (flag)
                    mDialog?.show() ?: dialogHelper.showSimpleProgress(requireContext())
                        .also { dialog ->
                            mDialog = dialog
                        }
                else mDialog?.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Constants.termsFromSignUp = false
    }

}