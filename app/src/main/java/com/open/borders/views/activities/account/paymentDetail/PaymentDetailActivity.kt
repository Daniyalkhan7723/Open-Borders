package com.open.borders.views.activities.account.paymentDetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.ActivityPaymentDetailBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.PaymentDetailViewModelModule
import com.open.borders.models.generalModel.User
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.MainMVVMBaseActivity
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import kotlinx.serialization.InternalSerializationApi
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import org.koin.core.module.Module
import java.util.*

@InternalSerializationApi
class PaymentDetailActivity :
    MainMVVMBaseActivity<PaymentDetailViewModel>(PaymentDetailViewModel::class) {
    private lateinit var binding: ActivityPaymentDetailBinding
    private var emailIsVerified = false
    var cardParams: CardParams? = null
    var creditCard = ""
    var expiryDate = ""
    var cardHolder = ""
    var cvc = ""
    private lateinit var stripe: Stripe
    private var cardToken: String? = null
    var user: User? = null
    override fun registerModule(): Module {
        return PaymentDetailViewModelModule
    }

    private lateinit var sharePreferenceHelper: SharePreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityPaymentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharePreferenceHelper = SharePreferenceHelper.getInstance(this@PaymentDetailActivity)
        stripe = Stripe(this@PaymentDetailActivity, Constants.stripePublishKey)
        user = SharePreferenceHelper.getInstance(this@PaymentDetailActivity).getUser()
        logScreenName("Payment Detail")

        if (user?.is_guest == 0) {
            getProfileApiCall()
//            if (user!!.pm_last_four != "" && user!!.pm_last_four != null) {
//                binding.creditCardNumber.setText("**** **** **** " + user!!.pm_last_four)
//            }
//            binding.cardHolder.setText(user!!.first_name + " " + user!!.last_name)
        }

        binding.toolbarId.toolbarNameTv.text = getString(R.string.payment_detail)
        binding.toolbarId.llChangeLanguage.viewGone()
        if (user?.is_guest != 0) {
            binding.emailLayout.viewVisible()
            binding.layoutCardDetail.viewGone()
            binding.btnAddCard.text = getString(R.string.check_email)
        } else {
            binding.emailLayout.viewGone()
            binding.layoutCardDetail.viewVisible()
        }

        if (user?.is_guest != 0) {
            setEditTexts()
        }

        backPress()
//        val dialogHelper by inject<MaterialDialogHelper>()
//        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
        binding.btnAddCard.setOnClickListener {
            if (user?.is_guest != 0) {
                if (!emailIsVerified) {
                    var email = binding.email.text.toString().trim()
                    if (email.isEmpty()) {
                        binding.email.backgroundTintList =
                            ContextCompat.getColorStateList(
                                this@PaymentDetailActivity,
                                R.color.red_color
                            )
                        binding.errorEmail.viewVisible()
                        binding.errorEmail.text = getString(R.string.please_enter_email)
                        binding.email.requestFocus()
                    } else if (isValidEmail(email).not()) {
                        binding.email.backgroundTintList =
                            ContextCompat.getColorStateList(
                                this@PaymentDetailActivity,
                                R.color.red_color
                            )
                        binding.errorEmail.viewVisible()
                        binding.errorEmail.text =
                            getString(R.string.please_enter_valid_email_address)
                        binding.email.requestFocus()
                    } else {
                        getUserCardDetailApiCall(email)
                    }
                } else {
                    if (validation()) {
                        val cardMonth =
                            binding.expiryDate.text.toString().trim().split("/").first().toInt()
                        val cardYear =
                            binding.expiryDate.text.toString().trim().split("/").last().toInt()
                        cardParams = CardParams(
                            creditCard,
                            cardMonth,
                            cardYear,
                            cvc,
                            cardHolder
                        )
                        createCardToken(1)
                    }
                }

            } else {
                if (validation()) {
                    val cardMonth =
                        binding.expiryDate.text.toString().trim().split("/").first().toInt()
                    val cardYear =
                        binding.expiryDate.text.toString().trim().split("/").last().toInt()

                    cardParams = CardParams(
                        creditCard,
                        cardMonth,
                        cardYear,
                        cvc,
                        cardHolder
                    )
                    createCardToken(0)
                }
            }
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(this, msg, false)
            }
        }

        observe(viewModel.getUserLiveData) {
            if (it.status!!) {
//                if (it.data?.pmLastFour != "" && it.data?.pmLastFour != null) {
//                    binding.creditCardNumber.setText("**** **** **** " + it.data?.pmLastFour)
//                    binding.cardHolder.setText(it.data?.firstName + " " + it.data?.lastName)
//                }
                if (user?.is_guest == 1) {
//                    CustomToasts.showToast(this, it.message!!, true)
                    binding.emailLayout.viewGone()
                    binding.layoutCardDetail.viewVisible()
                    binding.btnAddCard.text = "Update Card"
                    emailIsVerified = true
                }
            }

            observe(viewModel.updatePaymentLiveData) {
                if (it.status!!) {
                    CustomToasts.showToast(this, it.message!!, true)
                    Toast.makeText(this@PaymentDetailActivity, it.message!!, Toast.LENGTH_SHORT)
                        .show()
                    finish()
//                    getProfileApiCall()
                } else {
                    CustomToasts.showToast(this, it.message!!, false)
                }
            }
        }

        observe(viewModel.addPaymentLiveData) {
            if (it.status!!) {
                Toast.makeText(this@PaymentDetailActivity, it.message!!, Toast.LENGTH_SHORT).show()
//                CustomToasts.showToast(this, it.message!!, true)
                finish()
//                    getProfileApiCall()
            } else {
                CustomToasts.showToast(this, it.message!!, false)
            }
        }

        observe(viewModel.getProfileLiveData) {
            if (it.data?.user?.pm_last_four != "" && it.data?.user?.pm_last_four != null) {
                binding.creditCardNumber.setText("**** **** **** " + it.data?.user?.pm_last_four)
            }
            if (it.data?.user?.card_holder_name != "" && it.data?.user?.card_holder_name != null) {
                binding.cardHolder.setText(it.data.user.card_holder_name)
            }
            if (it.data?.user?.expire_date != "" && it.data?.user?.expire_date != null) {
                binding.expiryDate.setText(it.data.user.expire_date)
            }
            setEditTexts()
        }
    }

    private fun setEditTexts() {
        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.email.backgroundTintList =
                    ContextCompat.getColorStateList(
                        this@PaymentDetailActivity,
                        R.color.editText_line_color
                    )
                binding.errorEmail.error = null
                binding.errorEmail.viewGone()
            }
        })
//        val formatter = MaskedFormatter("**** **** **** ****")
//        binding.creditCardNumber.addTextChangedListener(
//            MaskedWatcher(
//                formatter,
//                binding.creditCardNumber
//            )
//        )

        binding.creditCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
//                binding.creditCardNumber.backgroundTintList =
//                    ContextCompat.getColorStateList(
//                        this@PaymentDetailActivity,
//                        R.color.editText_line_color
//                    )
//                binding.errorCreditCardNumber.error = null
//                binding.errorCreditCardNumber.viewGone()

                binding.errorCreditCardNumber.viewGone()
                binding.creditCardNumber.backgroundTintList =
                    ContextCompat.getColorStateList(
                        this@PaymentDetailActivity,
                        R.color.editText_line_color
                    )

                binding.errorCreditCardNumber.error = null
                val visa = Regex("^4[0-9]{6,}$")
                val mastercard = Regex("^5[1-5][0-9]{14}$")

                var input = ""
                if (s != null) {
                    if (s.contains(" ")) {
                        input = s.toString().replace(" ", "")
                    }
                }

                if (input.matches(visa)) {
                    Log.e("Visa", "VisaCardFound")
//                    binding.creditCardNumber.setCompoundDrawablesWithIntrinsicBounds(
//                        R.drawable.ic_visa_card,
//                        0,
//                        0,
//                        0
//                    );
//                    binding.imageView.setImageResource(R.drawable.ic_visa_card)
                    binding.creditCardNumber.backgroundTintList =
                        ContextCompat.getColorStateList(
                            this@PaymentDetailActivity,
                            R.color.editText_line_color
                        )
                    binding.errorCreditCardNumber.error = null
                    binding.errorCreditCardNumber.viewGone()

                } else if (input.matches(mastercard)) {
                    Log.e("Master", "MasterCardFound")
//                    binding.creditCardNumber.setCompoundDrawablesWithIntrinsicBounds(
//                        R.drawable.ic_master_card,
//                        0,
//                        0,
//                        0
//                    )

//                    binding.imageView.setImageResource(R.drawable.ic_master_card)
                    binding.creditCardNumber.backgroundTintList =
                        ContextCompat.getColorStateList(
                            this@PaymentDetailActivity,
                            R.color.editText_line_color
                        )
                    binding.errorCreditCardNumber.error = null
                    binding.errorCreditCardNumber.viewGone()

                } else {
//                    binding.creditCardNumber.setCompoundDrawablesWithIntrinsicBounds(
//                        R.drawable.ic_credit_card_grey,
//                        0,
//                        0,
//                        0
//                    )
//                    binding.imageView.setImageResource(R.drawable.ic_credit_card_grey)
                    if (input.length == 16) {
                        binding.errorCreditCardNumber.viewVisible()
                        binding.errorCreditCardNumber.text =
                            getString(R.string.please_enter_valid_card)
                        binding.creditCardNumber.requestFocus()
                        Log.e("None", "NoCardFound")
                    }
                }


            }
        })

        binding.cardHolder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.cardHolder.backgroundTintList =
                    ContextCompat.getColorStateList(
                        this@PaymentDetailActivity,
                        R.color.editText_line_color
                    )
                binding.errorCardHolder.error = null
                binding.errorCardHolder.viewGone()
            }
        })

        binding.cvc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.cvc.backgroundTintList =
                    ContextCompat.getColorStateList(
                        this@PaymentDetailActivity,
                        R.color.editText_line_color
                    )
                binding.errorCvc.error = null
                binding.errorCvc.viewGone()
            }
        })

//        binding.expiryDate.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                binding.expiryDate.backgroundTintList =
//                    ContextCompat.getColorStateList(
//                        this@PaymentDetailActivity,
//                        R.color.editText_line_color
//                    )
//                binding.errorExpiryDate.error = null
//                binding.errorExpiryDate.viewGone()
//            }
//        })

        binding.expiryDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 1 && !s.toString().contains("0")) {
                    val mString = s.toString().toInt()
                    if (mString in 2..9) {
                        binding.expiryDate.setText("0$s")
                        binding.expiryDate.requestFocus()
                        binding.expiryDate.post {
                            binding.expiryDate.setSelection(binding.expiryDate.length())
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.errorExpiryDate.viewGone()
                binding.expiryDate.backgroundTintList =
                    ContextCompat.getColorStateList(
                        this@PaymentDetailActivity,
                        R.color.editText_line_color
                    )
                binding.errorExpiryDate.error = null

                if (s.toString().length == 5) {
                    binding.cardHolder.requestFocus()
                    val working = s.toString().replace("/", "")
                    val enteredYear = working.substring(2)
                    val enterMonth = working.substring(0, 2)
                    val currentYear = Calendar.getInstance()
                        .get(Calendar.YEAR) % 100//getting last 2 digits of current year i.e. 2018 % 100 = 18
                    val month = Calendar.getInstance().get(Calendar.MONTH)
                    if (enterMonth.toInt() > 12 || currentYear > enteredYear.toInt() || enterMonth.toInt() < month) {
                        binding.expiryDate.backgroundTintList =
                            ContextCompat.getColorStateList(
                                this@PaymentDetailActivity,
                                R.color.red_color
                            )
                        binding.errorExpiryDate.viewVisible()
                        binding.errorExpiryDate.text =
                            getString(R.string.please_enter_valid_exp)
                        binding.errorExpiryDate.requestFocus()
                    } else {
                        binding.expiryDate.backgroundTintList =
                            ContextCompat.getColorStateList(
                                this@PaymentDetailActivity,
                                R.color.editText_line_color
                            )
                        binding.errorExpiryDate.error = null
                        binding.errorCreditCardNumber.viewGone()
                    }
                }
            }
        })
    }

    private fun createCardToken(type: Int) {
        var lastFourDigits = creditCard.substring(creditCard.length - 4)
        cardParams?.let {
            stripe.createCardToken(it, callback = object : ApiResultCallback<Token> {
                override fun onSuccess(result: Token) {
                    //Get Token from card Data tok_xxxxxxxxxxx
                    cardToken = result.id
                    if (cardToken != null) {
                        if (isNetworkAvailable(this@PaymentDetailActivity)) {
                            if (type == 0) {
                                viewModel.addPaymentDetail(
                                    cardToken!!.toRequestBody(),
                                    lastFourDigits.toRequestBody(),
                                    cardHolder.toRequestBody(),
                                    expiryDate.toRequestBody()

                                )
                            } else {
                                viewModel.updatePaymentDetail(
                                    binding.email.text.toString().trim().toRequestBody(),
                                    cardToken!!.toRequestBody(),
                                    lastFourDigits.toRequestBody()
                                )
                            }

                        } else {
                            CustomToasts.showToast(
                                this@PaymentDetailActivity,
                                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                                false
                            )
                        }
                    }

                }

                override fun onError(e: Exception) {
                    Log.e("StripeExample", "Error while creating card token", e)
                    CustomToasts.failureToast(this@PaymentDetailActivity, e.toString())
//                    paymentError.onPaymentError(e.toString())
                }
            })
        }
    }


    private fun validation(): Boolean {
        creditCard = binding.creditCardNumber.text.toString()
        expiryDate = binding.expiryDate.text.toString()
        cardHolder = binding.cardHolder.text.toString()
        cvc = binding.cvc.text.toString()

        if (creditCard.isEmpty()) {
            binding.creditCardNumber.backgroundTintList =
                ContextCompat.getColorStateList(this@PaymentDetailActivity, R.color.red_color)
            binding.errorCreditCardNumber.viewVisible()
            binding.errorCreditCardNumber.text = "Please enter card no."
            binding.creditCardNumber.requestFocus()
            return false
        } else if (creditCard.length < 19) {
            binding.creditCardNumber.backgroundTintList =
                ContextCompat.getColorStateList(this@PaymentDetailActivity, R.color.red_color)
            binding.errorCreditCardNumber.viewVisible()
            binding.errorCreditCardNumber.text = "Please enter valid card no."
            binding.creditCardNumber.requestFocus()
            return false
        } else if (creditCard.contains("*")) {
            binding.creditCardNumber.backgroundTintList =
                ContextCompat.getColorStateList(this@PaymentDetailActivity, R.color.red_color)
            binding.errorCreditCardNumber.viewVisible()
            binding.errorCreditCardNumber.text = "Please enter valid card no."
            binding.creditCardNumber.requestFocus()
            return false
        } else if (expiryDate.isEmpty()) {
            binding.expiryDate.backgroundTintList =
                ContextCompat.getColorStateList(this@PaymentDetailActivity, R.color.red_color)
            binding.errorExpiryDate.viewVisible()
            binding.errorExpiryDate.text = "Please enter expiry date."
            binding.expiryDate.requestFocus()
            return false
        } else if (cardHolder.isEmpty()) {
            binding.cardHolder.backgroundTintList =
                ContextCompat.getColorStateList(this@PaymentDetailActivity, R.color.red_color)
            binding.errorCardHolder.viewVisible()
            binding.errorCardHolder.text = "Please enter card holder name."
            binding.cardHolder.requestFocus()
            return false

        } else if (cvc.isEmpty()) {
            binding.cvc.backgroundTintList =
                ContextCompat.getColorStateList(this@PaymentDetailActivity, R.color.red_color)
            binding.errorCvc.viewVisible()
            binding.errorCvc.text = "Please enter cvc number."
            binding.cvc.requestFocus()
            return false
        }

        return true
    }

    //Get UserDetail Api Call
    private fun getUserCardDetailApiCall(email: String) {
        if (isNetworkAvailable(this)) {
            viewModel.getUser(email.toRequestBody())
        } else {
            CustomToasts.showToast(
                this,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    //Get UserDetail Api Call
    private fun getProfileApiCall() {
        if (isNetworkAvailable(this)) {
            viewModel.getProfile()
        } else {
            CustomToasts.showToast(
                this,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    private fun backPress() {
        binding.toolbarId.backArrow.setOnClickListener {
            onBackPressed()
        }
    }


}