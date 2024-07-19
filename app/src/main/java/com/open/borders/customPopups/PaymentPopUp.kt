package com.open.borders.customPopups

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.PaymentPopupLayoutBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.utils.Constants
import com.open.borders.utils.Event
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.views.fragments.scheduleConsulting.DateAndTimeViewModel
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.Token
import com.stripe.android.view.CardMultilineWidget
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@InternalSerializationApi
class PaymentPopUp(
    private var isFromPackages: Boolean? = null,
    private var listener: PaymentPopUpInterface,
    private var paymentError: PaymentError,
    private var isUpdate: Boolean,
    private var navigateUpdatePayment: GoToUpdatePaymentScreen,
    private var cardNumber: String,
    private var cardHolder: String,
    private var expiryDate: String
) : DialogFragment() {

    private lateinit var binding: PaymentPopupLayoutBinding
    private lateinit var stripe: Stripe
    private var cardToken: String? = null
    private var stripeToken: String? = null
    private var cardInputEt: CardMultilineWidget? = null
    var cardParams: CardParams? = null
    private val viewModel: DateAndTimeViewModel by viewModel()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(true)
        binding = PaymentPopupLayoutBinding.bind(view)
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)

        if (isUpdate) {
            binding.layoutUpdate.viewVisible()
            binding.layoutEnterStripeData.viewGone()
        } else {
            binding.layoutUpdate.viewGone()
            binding.layoutEnterStripeData.viewVisible()
        }


        setEditTexts()
//        cardInputEt = view.findViewById<CardMultilineWidget>(R.id.card_no_et)

        stripe = Stripe(
            requireContext(),
            Constants.stripePublishKey
        )

        binding.imageIcon.setOnClickListener {
            dismiss()
        }

        binding.button?.frameLayoutGoogle?.setOnClickListener {
            dismiss()
            listener.onGooglePayClick()
        }

        binding.cardNumber?.text = "**** **** **** $cardNumber"
        binding.btnUpdate?.setOnClickListener {
            navigateUpdatePayment.onNavigateToUpdateScreen()
        }

        binding.cardPayButton.setOnClickListener {
            if (isUpdate) {
                listener.onCardPayClick(
                    "", isUpdate, cardNumber, cardHolder,
                    expiryDate
                )
            } else {
                val cardNo = binding.cardNo.unMaskedText.toString()
                val monthYear = binding.monthDate.text.toString()
                val cardHolder = binding.cardNo2.text.toString()
                val cvc = binding.cvc.text.toString()

                var enteredYear: String = ""
                var enterMonth: String = ""
                var currentYear: Int? = null

                if (monthYear.isNotEmpty()) {
                    val working = monthYear.replace("/", "")
                    enteredYear = working.substring(2)
                    enterMonth = working.substring(0, 2)
                    //getting last 2 digits of current year i.e. 2018 % 100 = 18
                    currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
                }

                if (cardNo.isEmpty()) {
                    binding.errorCardNoTv.viewVisible()
                    binding.errorCardNoTv.text = "Please enter Card no."
                    binding.cardNo.requestFocus()
                    return@setOnClickListener
                } else if (monthYear.isEmpty()) {
                    binding.errorCardNoTv.viewVisible()
                    binding.errorCardNoTv.text = "Please enter expiry date."
                    binding.monthDate.requestFocus()
                    return@setOnClickListener
                } else if (currentYear != null) {
                    if (enterMonth.toInt() > 12 || currentYear > enteredYear.toInt()) {
                        binding.errorCardNoTv.viewVisible()
                        binding.errorCardNoTv.text = getString(R.string.please_enter_valid_exp)
                        binding.monthDate.requestFocus()
                    } else if (cardHolder.isEmpty()) {
                        binding.errorCardNoTv.viewVisible()
                        binding.errorCardNoTv.text = "Please enter card holder name."
                        binding.cardNo2.requestFocus()
                        return@setOnClickListener
                    } else if (cvc.isEmpty()) {
                        binding.errorCardNoTv.viewVisible()
                        binding.errorCardNoTv.text = "Please enter CVC number."
                        binding.cvc.requestFocus()
                        return@setOnClickListener
                    } else {
                        val cardMonth =
                            binding.monthDate.text.toString().trim().split("/").first().toInt()
                        val cardYear =
                            binding.monthDate.text.toString().trim().split("/").last().toInt()
                        cardParams = CardParams(
                            cardNo,
                            cardMonth,
                            cardYear,
                            cvc,
                            cardHolder
                        )
                        //            if (cardInputEt?.cardParams?.typeDataParams?.entries?.isNotEmpty() == true) {
                        viewModel.showHideProgressBar(true)
                        var expiryDate = "$cardMonth/$cardYear"

                        if (isFromPackages == true) {
                            createStripePaymentMethod(cardNo, cardHolder, expiryDate)
                        } else {
                            createCardToken(cardNo, cardHolder, expiryDate)
                        }
                        //            }else{
                        //                CustomToasts.failureToast(requireContext(), "Card input not be empty or incorrect.")
                        //            }
                    }
                }
            }
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
        val view = layoutInflater.inflate(R.layout.payment_popup_layout, container, false)
        binding = PaymentPopupLayoutBinding.bind(view)
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


    interface GoToUpdatePaymentScreen {
        fun onNavigateToUpdateScreen()
    }

    interface PaymentPopUpInterface {
        fun onGooglePayClick()
        fun onCardPayClick(
            token: String,
            isCardSave: Boolean,
            cardNo: String,
            cardHolder: String,
            expiryDate: String
        )
    }

    private fun createCardToken(cardNo: String, cardHolder: String, expiryDate: String) {
//        val cardParams = cardInputEt?.cardParams
        cardParams?.let {
            stripe.createCardToken(it, callback = object : ApiResultCallback<Token> {
                override fun onSuccess(result: Token) {
                    //Get Token from card Data tok_xxxxxxxxxxx
                    cardToken = result.id
                    if (cardToken != null) {
                        listener.onCardPayClick(
                            cardToken!!,
                            isUpdate,
                            cardNo,
                            cardHolder,
                            expiryDate
                        )
                        viewModel.showHideProgressBar(false)
                        dismiss()
                    }
                }

                override fun onError(e: Exception) {
                    Log.e("StripeExample", "Error while creating card token", e)
                    CustomToasts.failureToast(requireContext(), e.toString())
//                    paymentError.onPaymentError(e.toString())
                    viewModel.showHideProgressBar(false)
                }
            })
        }
    }

    private fun createStripePaymentMethod(cardNo: String, cardHolder: String, expiryDate: String) {
        //Create Payment Method Token pm_xxxxxxxxxxx
//        val cardParams = cardInputEt?.cardParams
        val params = cardParams?.let {
            PaymentMethodCreateParams.createCard(it)
        }

        if (params != null) {
            stripe.createPaymentMethod(
                params,
                callback = object : ApiResultCallback<PaymentMethod> {
                    override fun onSuccess(result: PaymentMethod) {
                        // handle success
                        stripeToken = result.id ?: ""
                        Log.e("sToken", stripeToken!!)
                        if (stripeToken != null) {
                            listener.onCardPayClick(
                                stripeToken!!,
                                isUpdate,
                                cardNo,
                                cardHolder,
                                expiryDate
                            )
                            viewModel.showHideProgressBar(false)
                            dismiss()
                        }
                    }

                    override fun onError(e: Exception) {
                        CustomToasts.failureToast(requireContext(), e.toString())
                        viewModel.showHideProgressBar(false)
                        dismiss()
//                        paymentError.onPaymentError(e.toString())
//                        CustomToasts.showToast(requireActivity(), e.toString(), false)
                    }
                })
        }
    }

    @InternalSerializationApi
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

    private fun setEditTexts() {
        binding.cardNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.errorCardNoTv.viewGone()
                if (s.toString().length == 19) {
                    binding.monthDate.requestFocus()
                }

                val visa = Regex("^4[0-9]{6,}$")
                val mastercard = Regex("^5[1-5][0-9]{14}$")
                val amx = Regex("^3[47][0-9]{13}$")

                var input = ""
                if (s != null) {
                    if (s.contains(" ")) {
                        input = s.toString().replace(" ", "")
                    }
                }
//                getCardType(input)
                if (input.matches(visa)) {
                    Log.e("Visa", "VisaCardFound")
                    binding.imageView.setImageResource(R.drawable.ic_visa_card)
                    binding.cardNo.backgroundTintList = ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.editText_line_color
                    )
                    binding.errorCardNoTv.error = null
                    binding.errorCardNoTv.viewGone()

                } else if (input.matches(mastercard)) {
                    Log.e("Master", "MasterCardFound")
                    binding.imageView.setImageResource(R.drawable.ic_master_card)
                    binding.cardNo.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.editText_line_color
                        )
                    binding.errorCardNoTv.error = null
                    binding.errorCardNoTv.viewGone()

                } else {
                    binding.imageView.setImageResource(R.drawable.ic_credit_card_grey)
                    if (input.length == 16) {
                        binding.errorCardNoTv.viewVisible()
                        binding.errorCardNoTv.text = getString(R.string.please_enter_valid_card)
                        binding.cardNo.requestFocus()
                        Log.e("None", "NoCardFound")
                    }
                }
            }
        })

        binding.monthDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 1 && !s.toString().contains("0")) {
                    val mString = s.toString().toInt()
                    if (mString in 2..9) {
                        binding.monthDate.setText("0$s")
                        binding.monthDate.requestFocus()
                        binding.monthDate.post {
                            binding.monthDate.setSelection(binding.monthDate.length())
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.errorCardNoTv.viewGone()
//                binding.monthDate.setSelection(2)
//                binding.monthDate.setSelection( binding.errorCardNoTv.length())
                if (s.toString().length == 5) {
                    binding.cardNo2.requestFocus()
                    val working = s.toString().replace("/", "")
                    val enteredYear = working.substring(2)
                    val enterMonth = working.substring(0, 2)
                    val currentYear = Calendar.getInstance()
                        .get(Calendar.YEAR) % 100//getting last 2 digits of current year i.e. 2018 % 100 = 18
                    val month = Calendar.getInstance().get(Calendar.MONTH)
                    if (enterMonth.toInt() > 12 || currentYear > enteredYear.toInt() || enterMonth.toInt() < month) {
                        binding.cardNo.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                        binding.errorCardNoTv.viewVisible()
                        binding.errorCardNoTv.text = getString(R.string.please_enter_valid_exp)
                        binding.monthDate.requestFocus()
                    } else {
                        binding.errorCardNoTv.viewGone()
                    }
                }
            }
        })

        binding.cardNo2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.errorCardNoTv.viewGone()
            }
        })

        binding.cvc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.errorCardNoTv.viewGone()
            }
        })
    }

    private fun getCardType(number: String): String {
        val visa = Regex("^4[0-9]{12}(?:[0-9]{3})?$")
        val mastercard = Regex("^5[1-5][0-9]{14}$")
        val amx = Regex("^3[47][0-9]{13}$")

        return when {
            visa.matches(number) -> "Visa"
            mastercard.matches(number) -> "Mastercard"
            amx.matches(number) -> "American Express"
            else -> "Unknown"
        }
    }

    interface PaymentError {
        fun onPaymentError(e: String)
    }
}