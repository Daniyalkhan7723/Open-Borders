package com.open.borders.views.activities.packegeDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.customPopups.*
import com.open.borders.databinding.ActivityPackageDetailBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.ConsultationViewModelModule
import com.open.borders.models.generalModel.historyModels.PurchesPlanHistory
import com.open.borders.models.responseModel.GetPackagesModel
import com.open.borders.models.responseModel.PlanPrice
import com.open.borders.thirdPartyModules.htmltextview.HtmlResImageGetter
import com.open.borders.thirdPartyModules.htmltextview.HtmlTextView
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.PaymentsUtil
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.account.paymentDetail.PaymentDetailActivity
import com.open.borders.views.activities.baseActivity.BaseActivity
import com.open.borders.views.activities.baseActivity.MainMVVMBaseActivity
import com.open.borders.views.activities.navigationHost.AuthenticationHost
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.flatFee.SelectPriceAdapter
import com.open.borders.views.fragments.scheduleConsulting.ScheduleConsultingViewModel
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class PackageDetailActivity :
    MainMVVMBaseActivity<ScheduleConsultingViewModel>(ScheduleConsultingViewModel::class),
    SelectPriceAdapter.SelectPriceInterface {
    private lateinit var binding: ActivityPackageDetailBinding
    private var priceList = mutableListOf<PlanPrice>()
    private var data: GetPackagesModel? = null
    private lateinit var paymentsClient: PaymentsClient
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
    private val LOAD_PAYMENT_DATA_REQUEST_CODE_For_Guest_Mode = 992
    private var planId: Int? = null
    private var priceId: Int? = null
    private var quantity: Int? = 1
    private var stripeToken: String? = null
    private var totalQuantityPrice: String? = null
    private var image: String? = null
    private var fromAmount: Int? = null
    private var amount: Double? = null
    private var mDescriptionTV: HtmlTextView? = null
    private val mActivity: BaseActivity? = null
    private var language: String? = null
    private var firstName = ""
    private var lastName = ""
    private var mEmail = ""
    private var phoneNo = ""
    private var streetAddress = ""
    private var country = ""
    private var city = ""
    private var state = ""
    private var zipCode = ""
    private var mCountryNameCode = ""
    private var currentCountryCode = ""
    private var firstNameUs = ""
    private var lastNameUs = ""
    private var emailUs = ""
    private var mPassword = ""
    private var confirm_password = ""
    private var mIs_Login = 0
    lateinit var popUp: GuestModePopUp
    private var isUpdate = false
    private var cardToken = ""
    private var cardLastFour: String = ""

    private var mCardHolder: String = ""
    private var mExpiryDate: String = ""
    var cardParams: CardParams? = null
    lateinit var paymentSuccessPopUp: PaymentSuccessPopUp
    lateinit var paymentPopup: PaymentPopUp
    private val stripe: Stripe by lazy {
        Stripe(
            this, Constants.stripePublishKey
        )
    }

    override fun registerModule(): Module {
        return ConsultationViewModelModule
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackageDetailBinding.inflate(layoutInflater)
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        attachViewModel()
        logScreenName("Services Details")
        binding.toolbarId.toolbarNameTv.text = getString(R.string.package_detail)
        binding.toolbarId.llChangeLanguage.viewGone()
        binding.toolbarId.backArrow.setOnClickListener {
            onBackPressed()
        }
        language = tinyDB.getString(Constants.language)
        setEditTexts()
        mDescriptionTV = findViewById(R.id.packageTvDescription)
        binding.quentityEt?.setText("1")
        when (intent.getSerializableExtra("fromFragment") as Int) {
            1 -> {
                setData()
                paymentsClient = PaymentsUtil.createPaymentsClient(this)
                possiblyShowGooglePayButton()
            }
            2 -> {
                binding.layout3.viewGone()
                binding.buttonLayoutId?.viewGone()
                setHistoryData()
            }
            3 -> {
                setData()
                paymentsClient = PaymentsUtil.createPaymentsClient(this)
                possiblyShowGooglePayButton()
            }
        }
        //fixme
        binding.buttonLayoutId?.setOnClickListener {
            val user = SharePreferenceHelper.getInstance(this).getUser()
            if (user.is_guest == 0) {
                if (user.is_guest == 0) {
                    getUserCardDetailApiCall()
                }
            } else {
                when (language) {
                    "es" -> {
                        popUp = GuestModePopUp(object : GuestModePopUp.GuestModeCallBack {
                            override fun onCheckSubmit(
                                fName: String,
                                lName: String,
                                email: String,
                                phoneNumber: String,
                                street_address: String,
                                mcountry: String,
                                mcity: String,
                                mstate: String,
                                zip_code: String,
                                countryNameCode: String,
                                Usfname: String,
                                UsLastName: String,
                                UsEmail: String,
                                password: String,
                                confirmPassword: String,
                                is_Login: Int
                            ) {
                                firstName = fName
                                lastName = lName
                                mEmail = email
                                phoneNo = phoneNumber
                                streetAddress = street_address
                                country = mcountry
                                city = mcity
                                state = mstate
                                zipCode = zip_code
                                mCountryNameCode = countryNameCode
                                firstNameUs = Usfname
                                lastNameUs = UsLastName
                                emailUs = UsEmail
                                mPassword = password
                                confirm_password = confirmPassword
                                mIs_Login = is_Login

                                paymentPopup =
                                    PaymentPopUp(true, object : PaymentPopUp.PaymentPopUpInterface {
                                        override fun onGooglePayClick() {
                                            requestPaymentForGuestMode()
                                        }

                                        override fun onCardPayClick(
                                            token: String,
                                            isCardSave: Boolean,
                                            cardNo: String,
                                            cardHolder: String,
                                            expiryDate: String
                                        ) {
                                            subscriptionApiCallForGuestMode(
                                                fName,
                                                lName,
                                                email,
                                                phoneNumber,
                                                street_address,
                                                mcountry,
                                                mcity,
                                                mstate,
                                                zip_code,
                                                Usfname,
                                                UsLastName,
                                                UsEmail,
                                                password,
                                                confirmPassword,
                                                is_Login,
                                                token
                                            )
                                        }
                                    }, object : PaymentPopUp.PaymentError {
                                        override fun onPaymentError(e: String) {
                                            Toast.makeText(
                                                this@PackageDetailActivity,
                                                e,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }, isUpdate, object : PaymentPopUp.GoToUpdatePaymentScreen {
                                        override fun onNavigateToUpdateScreen() {
                                            paymentPopup.dismiss()
                                            startActivity(
                                                Intent(
                                                    this@PackageDetailActivity,
                                                    PaymentDetailActivity::class.java
                                                )
                                            )
                                        }
                                    }, cardLastFour,
                                        mCardHolder,
                                        mExpiryDate
                                    )
                                paymentPopup.show(supportFragmentManager, "")
                            }

                        }, data?.plan_name_es, object : GuestModePopUp.GoToLogin {
                            override fun onGoToLoginScreen() {
                                val myIntent = Intent(
                                    this@PackageDetailActivity, AuthenticationHost::class.java
                                )
                                startActivity(myIntent)
                            }
                        })
                    }
                    else -> {
                        popUp = GuestModePopUp(object : GuestModePopUp.GuestModeCallBack {
                            override fun onCheckSubmit(
                                fName: String,
                                lName: String,
                                email: String,
                                phoneNumber: String,
                                street_address: String,
                                mcountry: String,
                                mcity: String,
                                mstate: String,
                                zip_code: String,
                                countryNameCode: String,
                                Usfname: String,
                                UsLastName: String,
                                UsEmail: String,
                                password: String,
                                confirmPassword: String,
                                is_Login: Int
                            ) {
                                firstName = fName
                                lastName = lName
                                mEmail = email
                                phoneNo = phoneNumber

                                streetAddress = street_address
                                country = mcountry
                                city = mcity
                                state = mstate
                                zipCode = zip_code

                                mCountryNameCode = countryNameCode

                                firstNameUs = Usfname
                                lastNameUs = UsLastName
                                emailUs = UsEmail
                                mPassword = password
                                confirm_password = confirmPassword
                                mIs_Login = is_Login

                                paymentPopup = PaymentPopUp(
                                    false,
                                    object : PaymentPopUp.PaymentPopUpInterface {
                                        override fun onGooglePayClick() {
                                            requestPaymentForGuestMode()
                                        }

                                        override fun onCardPayClick(
                                            token: String,
                                            isCardSave: Boolean,
                                            cardNo: String,
                                            cardHolder: String,
                                            expiryDate: String
                                        ) {
                                            subscriptionApiCallForGuestMode(
                                                fName,
                                                lName,
                                                email,
                                                phoneNumber,
                                                street_address,
                                                mcountry,
                                                mcity,
                                                mstate,
                                                zip_code,
                                                Usfname,
                                                UsLastName,
                                                UsEmail,
                                                password,
                                                confirmPassword,
                                                is_Login,
                                                token
                                            )
                                        }
                                    },
                                    object : PaymentPopUp.PaymentError {
                                        override fun onPaymentError(e: String) {
                                            Toast.makeText(
                                                this@PackageDetailActivity, e, Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    },
                                    isUpdate,
                                    object : PaymentPopUp.GoToUpdatePaymentScreen {
                                        override fun onNavigateToUpdateScreen() {
                                            paymentPopup.dismiss()
                                            startActivity(
                                                Intent(
                                                    this@PackageDetailActivity,
                                                    PaymentDetailActivity::class.java
                                                )
                                            )
                                        }

                                    },
                                    cardLastFour,
                                    mCardHolder,
                                    mExpiryDate
                                )
                                paymentPopup.show(supportFragmentManager, "")
                            }
                        }, data?.plan_name, object : GuestModePopUp.GoToLogin {
                            override fun onGoToLoginScreen() {
                                val myIntent = Intent(
                                    this@PackageDetailActivity, AuthenticationHost::class.java
                                )
                                startActivity(myIntent)
                            }
                        })
                    }
                }
                popUp.show(supportFragmentManager, "")
//                val myIntent =
//                    Intent(this@PackageDetailActivity, AuthenticationHost::class.java)
//                startActivity(myIntent)
            }
        }

        binding.mImageView.setOnClickListener {
            lifecycleScope.launch {
                if (image != null) {
                    SharePreferenceHelper.getInstance(this@PackageDetailActivity)
                        .saveFileName(image!!)
                }
            }
            showFilePopup()
        }


//        binding.pricePop.setOnClickListener {
//            pricePopUp()
//        }

        val planType = data?.plan_type
        val recurringPeriod = data?.recurring_period
        val mAdapter: CustomSpinnerAdapter =
            CustomSpinnerAdapter(this, priceList, planType.toString(), recurringPeriod.toString())
        binding.spinnerSample?.adapter = mAdapter
        binding.spinnerSample?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                    val data = priceList[index]
                    fromAmount = data.price
                    planId = data.plan_id
                    priceId = data.id
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        data = intent.getSerializableExtra("myObject") as GetPackagesModel
        val duration = data?.duration
        val period = data?.recurring_period
        val periodEs = data?.recurring_period_es
        val planType = data?.plan_type
        image = Constants.IMAGE_BASE_URL + data?.image
        fromAmount = data?.amount
        planId = data?.id
        priceId = data?.plan_prices?.first()?.id

        when (language) {
            "es" -> {
                binding.tvTitle.text = data?.plan_name_es
                data?.description_es?.let { mDescriptionTV?.setHtml(it) }
                if (mActivity != null) {
                    HtmlResImageGetter(mActivity)
                }
            }
            else -> {
                binding.tvTitle.text = data?.plan_name
                data?.description?.let { mDescriptionTV?.setHtml(it) }
                mActivity?.let { HtmlResImageGetter(it) }
            }
        }

        binding.mImageView.let {
            Glide.with(this).load(image).into(it)
        }
        priceList = data?.plan_prices?.toMutableList()!!

        //Todo
        when (planType) {
            "single" -> {
                binding.pricePop?.viewGone()
                binding.buyTv?.text = getString(R.string.buy_this_service)
                if (priceList.size > 1) {
                    binding.tvAmountOptions.viewGone()
                    binding.spinnerFrameLayout?.viewVisible()
                    binding.tvFromAmount.text = "From $${fromAmount?.formatDecimalSeparator()}"
                } else {
                    binding.spinnerFrameLayout?.viewGone()
                    binding.tvAmountOptions.text = "$${fromAmount?.formatDecimalSeparator()}"
                    binding.tvFromAmount.text = " $${fromAmount?.formatDecimalSeparator()}"
                }
                if (data?.is_quantity_enable == 1) {
                    binding.tvQuantity.viewVisible()
                    binding.quentityEt?.viewVisible()
                } else {
                    binding.tvQuantity.viewGone()
                    binding.quentityEt?.viewGone()
                }
            }
            "recurring" -> {
                if (priceList.size > 1) {
                    binding.tvAmountOptions.viewGone()
                    binding.spinnerFrameLayout?.viewVisible()
//                    binding.pricePop?.viewVisible()
//                    binding.pricePop?.setText("$${data.amount}")
                    if (duration != null) {
                        if (duration > 1) {
                            if (data!!.recurring_period == "every_month") {
                                binding.tvFromAmount.text =
                                    "${getString(R.string.from_)} $${fromAmount?.formatDecimalSeparator()} ${
                                        getString(
                                            R.string.every_month
                                        )
                                    }"
                            } else {
                                binding.tvFromAmount.text =
                                    "${getString(R.string.from_)} $${fromAmount?.formatDecimalSeparator()} ${
                                        getString(
                                            R.string.every_month
                                        )
                                    }"
                            }

                        } else {
                            when (language) {
                                "es" -> {
                                    binding.tvFromAmount.text =
                                        "$${fromAmount?.formatDecimalSeparator()} ${getString(R.string.every_month_for)} $duration $periodEs"
                                }
                                else -> {
                                    binding.tvFromAmount.text =
                                        "$${fromAmount?.formatDecimalSeparator()} ${getString(R.string.every_month_for)} $duration $period"
                                }
                            }
                        }
                    }
//
                    if (data?.is_quantity_enable == 1) {
                        binding.tvQuantity.viewVisible()
                        binding.quentityEt?.viewVisible()
                    } else {
                        binding.tvQuantity.viewGone()
                        binding.quentityEt?.viewGone()
                    }
                } else {

                    binding.tvAmountOptions.viewVisible()
                    binding.pricePop?.viewGone()
                    binding.spinnerFrameLayout?.viewGone()
                    if (duration != null) {
                        if (duration > 1) {
                            when (language) {
                                "es" -> {
                                    binding.tvFromAmount.text =
                                        "$${fromAmount?.formatDecimalSeparator()} ${getString(R.string.every_month_for)} $duration ${periodEs}s"
                                    binding.tvAmountOptions.text =
                                        "$" + fromAmount?.formatDecimalSeparator()
                                            .toString() + " ${getString(R.string.every_month_for)} $duration ${periodEs}s"
                                }
                                else -> {
                                    binding.tvFromAmount.text =
                                        "$${fromAmount?.formatDecimalSeparator()} ${getString(R.string.every_month_for)} $duration ${period}s"
                                    binding.tvAmountOptions.text =
                                        "$" + fromAmount?.formatDecimalSeparator()
                                            .toString() + " ${getString(R.string.every_month_for)} $duration ${period}s"

                                }
                            }

                        } else {
                            when (language) {
                                "es" -> {
                                    binding.tvFromAmount.text =
                                        "$${fromAmount?.formatDecimalSeparator()} ${getString(R.string.every_month_for)} $duration ${periodEs}"
                                    binding.tvAmountOptions.text =
                                        "$" + fromAmount?.formatDecimalSeparator()
                                            .toString() + " ${getString(R.string.every_month_for)} $duration ${periodEs}"


                                }
                                else -> {
                                    binding.tvFromAmount.text =
                                        "$${fromAmount?.formatDecimalSeparator()} ${getString(R.string.every_month_for)} $duration ${period}"
                                    binding.tvAmountOptions.text =
                                        "$" + fromAmount?.formatDecimalSeparator()
                                            .toString() + " ${getString(R.string.every_month_for)} $duration ${period}"


                                }
                            }
                        }
                    }
                    if (data?.is_quantity_enable == 1) {
                        binding.tvQuantity.viewVisible()
                        binding.quentityEt?.viewVisible()
                    } else {
                        binding.tvQuantity.viewGone()
                        binding.quentityEt?.viewGone()
                    }
//                    if (duration != null) {
//                        if (duration > 1) {
//                            binding.quentityEt.text = "$duration $period(s)"
//                        } else {
//                            binding.quentityEt.text = "$duration $period"
//                        }
//                    }
                }
            }
        }
    }


    //Get UserDetail Api Call
    private fun getUserCardDetailApiCall() {
        if (isNetworkAvailable(this)) {
//            viewModel.getUser(email.toRequestBody())
            viewModel.getProfile()
        } else {
            CustomToasts.showToast(
                this, Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setHistoryData() {
        val data = intent.getSerializableExtra("myObject") as PurchesPlanHistory
        val duration = data.plan.duration
        val period = data.plan.recurring_period
        image = Constants.IMAGE_BASE_URL + data.plan.image
        val planType = data.plan.plan_type
        fromAmount = data.total_amount.toInt()
        planId = data.id

        binding.mImageView.let {
            Glide.with(this).load(image).into(it)
        }

        when (language) {
            "es" -> {
                binding.tvTitle.text = data.plan.plan_name_es
                data.plan.description_es?.let { mDescriptionTV?.setHtml(it) }
                if (mActivity != null) {
                    HtmlResImageGetter(mActivity)
                }
            }
            else -> {
                binding.tvTitle.text = data?.plan.plan_name
                data.plan.description.let { mDescriptionTV?.setHtml(it) }
                mActivity?.let { HtmlResImageGetter(it) }
            }
        }

        binding.tvAmountOptions.text = "$" + data.plan.amount.toString()
//        if (duration != null) {
//            if (duration > 1) {
//                binding.quentityEt.text = "$duration $period(s)"
//            } else {
//                binding.quentityEt.text = "$duration $period"
//            }
//        }

        if (planType == "recurring") {
            binding.tvFromAmount.text = " $${fromAmount!!.formatDecimalSeparator()}"
        } else {
            binding.tvFromAmount.text = " $${fromAmount?.formatDecimalSeparator()}"
        }
    }

    private fun requestPayment() {
        val priceCents = Math.round(fromAmount!! * PaymentsUtil.CENTS.toDouble())
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents)
        if (paymentDataRequestJson == null) {
            CustomToasts.showToast(this, getString(R.string.payment_request_failed), false)
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE
        )
    }

    private fun requestPaymentForGuestMode() {
        val priceCents = Math.round(fromAmount!! * PaymentsUtil.CENTS.toDouble())
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents)
        if (paymentDataRequestJson == null) {
            CustomToasts.showToast(this, getString(R.string.payment_request_failed), false)
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(request),
            this,
            LOAD_PAYMENT_DATA_REQUEST_CODE_For_Guest_Mode
        )
    }

    private fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            binding.buttonLayoutId?.viewVisible()
        } else {
            Toast.makeText(
                this,
                getString(R.string.Unfortunately_google_pay_is_not_available_on_this_device),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> data?.let { intent ->
                        try {
                            val paymentData = PaymentData.getFromIntent(intent)
                            handlePaymentSuccess(paymentData!!)

                        } catch (e: Throwable) {
                            e.localizedMessage
                        }
                    }
                    RESULT_CANCELED -> {
//                        CustomToasts.showToast(this, "Payment Cancelled", false)
                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
            }
            LOAD_PAYMENT_DATA_REQUEST_CODE_For_Guest_Mode -> {
                when (resultCode) {
                    RESULT_OK -> data?.let { intent ->
                        try {
                            val paymentData = PaymentData.getFromIntent(intent)
                            handlePaymentSuccessForGuestMode(paymentData!!)
                        } catch (e: Throwable) {
                            e.localizedMessage
                        }
                    }

                    RESULT_CANCELED -> {
//                        CustomToasts.showToast(this, "Payment Cancelled", false)
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
            }
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        var paymentInfo = paymentData.toJson() ?: return
        try {
            val paymentMethodData = JSONObject(paymentInfo).getJSONObject("paymentMethodData")
            val tokenizationData = paymentMethodData.getJSONObject("tokenizationData")
            val lastFour = paymentMethodData.getJSONObject("info").getString("cardDetails")
            val mtoken = tokenizationData.getString("token")
            var mToken = minimize(mtoken)
            var jsonObject = JSONObject(mToken)
            val token = jsonObject.getString("id")
            if (!token.isNullOrBlank() && token != "") {
                subscriptionApiCall(token, false, lastFour, "", "", true)
            } else {
                CustomToasts.showToast(
                    this@PackageDetailActivity, Constants.WENT_WRONG_MESSAGE, false
                )
            }
        } catch (e: JSONException) {
            throw RuntimeException("The selected garment cannot be parsed from the list of elements")
        }

        //Get Stripe Payment Method Token pm_token
//        val params = PaymentMethodCreateParams.createFromGooglePay(JSONObject(paymentData.toJson()))
//        stripe.createPaymentMethod(params, callback = object : ApiResultCallback<PaymentMethod> {
//            override fun onSuccess(result: PaymentMethod) {
//                // handle success
//                stripeToken = result.id ?: ""
//                Log.e("sToken", stripeToken.toString())
//                if (!stripeToken.isNullOrBlank()) {
//                    subscriptionApiCall(stripeToken!!, isUpdate, cardLastFour)
//                } else {
//                    CustomToasts.showToast(
//                        this@PackageDetailActivity, Constants.WENT_WRONG_MESSAGE, false
//                    )
//                }
//            }
//
//            override fun onError(e: Exception) {
//                CustomToasts.showToast(this@PackageDetailActivity, e.toString(), false)
//            }
//        })

    }

    private fun handlePaymentSuccessForGuestMode(paymentData: PaymentData) {
        var paymentInfo = paymentData.toJson() ?: return
        try {
            val paymentMethodData = JSONObject(paymentInfo).getJSONObject("paymentMethodData")
            val tokenizationData = paymentMethodData.getJSONObject("tokenizationData")
            val lastFour = paymentMethodData.getJSONObject("info").getString("cardDetails")
            val mtoken = tokenizationData.getString("token")
            var mToken = minimize(mtoken)
            var jsonObject = JSONObject(mToken)
            val token = jsonObject.getString("id")
            Log.d("ffffddfffff", token)
            if (!token.isNullOrBlank() && token != "") {
                subscriptionApiCallForGuestMode(
                    firstName,
                    lastName,
                    mEmail,
                    phoneNo,
                    streetAddress,
                    country,
                    city,
                    state,
                    zipCode,
                    firstNameUs,
                    lastNameUs,
                    emailUs,
                    mPassword,
                    confirm_password,
                    mIs_Login,
                    token
                )
            } else {
                CustomToasts.showToast(
                    this@PackageDetailActivity, Constants.WENT_WRONG_MESSAGE, false
                )
            }
        } catch (e: JSONException) {
            throw RuntimeException("The selected garment cannot be parsed from the list of elements")
        }


        //Get Stripe Payment Method Token pm_token
//        val params = PaymentMethodCreateParams.createFromGooglePay(JSONObject(paymentData.toJson()))
//
//        stripe.createPaymentMethod(params, callback = object : ApiResultCallback<PaymentMethod> {
//            override fun onSuccess(result: PaymentMethod) {
//                // handle success
//
//                stripeToken = result.id ?: ""
//                Log.e("sToken", stripeToken.toString())
//
//                if (!stripeToken.isNullOrBlank()) {
//                    subscriptionApiCallForGuestMode(
//                        firstName,
//                        lastName,
//                        mEmail,
//                        phoneNo,
//
//                        streetAddress,
//                        country,
//                        city,
//                        state,
//                        zipCode,
//
//                        firstNameUs,
//                        lastNameUs,
//                        emailUs,
//                        mPassword,
//                        confirm_password,
//                        mIs_Login,
//                        stripeToken!!
//                    )
//                } else {
//                    CustomToasts.showToast(
//                        this@PackageDetailActivity, Constants.WENT_WRONG_MESSAGE, false
//                    )
//                }
//            }
//
//            override fun onError(e: Exception) {
//                CustomToasts.showToast(this@PackageDetailActivity, e.toString(), false)
//            }
//        })
    }

    fun minimize(input: String): String? {
        val strBuffer = StringBuffer()
        var qouteOpened = false
        var wasEscaped = false
        for (i in 0 until input.length) {
            val c = input[i]
            if (c == '\\') {
                wasEscaped = true
            }
            if (c == '"') {
                qouteOpened = if (wasEscaped) qouteOpened else !qouteOpened
            }
            if (!qouteOpened && c == ' ') {
                continue
            }
            if (c != '\\') {
                wasEscaped = false
            }
            strBuffer.append(c)
        }
        return strBuffer.toString()
    }


    private fun subscriptionApiCall(
        stripeToken: String,
        isCardSave: Boolean,
        lastFourDigits: String,
        cardHolder: String,
        expiryDate: String,
        is_other_payment_gateway: Boolean
    ) {
        if (isNetworkAvailable(this@PackageDetailActivity)) {
            viewModel.subscription(
                stripeToken,
                planId.toString(),
                priceId.toString(),
                quantity!!,
                isCardSave,
                lastFourDigits,
                is_other_payment_gateway,
                cardHolder,
                expiryDate
            )
        } else {
            CustomToasts.showToast(
                this@PackageDetailActivity, Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }

    private fun subscriptionApiCallForGuestMode(
        fName: String,
        lName: String,
        email: String,
        phoneNumber: String,
        street_address: String,
        country: String,
        city: String,
        state: String,
        zip_code: String,
        Usfname: String,
        UsLastName: String,
        UsEmail: String,
        password: String,
        confirmPassword: String,
        is_Login: Int,
        stripeToken: String
    ) {
        if (isNetworkAvailable(this@PackageDetailActivity)) {
            val userId = sharedPreference?.getUser()?.id

            viewModel.subscriptionForGuestMode(
                userId.toString(),
                fName,
                lName,
                email,
                phoneNumber,
                street_address,
                country,
                city,
                state,
                zip_code,
                mCountryNameCode,
                Usfname,
                UsLastName,
                UsEmail,
                password,
                confirmPassword,
                is_Login,
                stripeToken,
                planId.toString(),
                priceId.toString(),
                quantity!!
            )
        } else {
            CustomToasts.showToast(
                this@PackageDetailActivity, Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }


    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }

    //fixme
    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(this, msg, false)
            }
        }
        observe(viewModel.subscriptionLivaData) {
            if (it.status) {
//                CustomToasts.showToast(this, it.message, true)
                val planName = it.data.plan_detail.plan_name
                val clientSecret = it.data.client_secret
                if (planName.isNotEmpty() || clientSecret.isNotEmpty()) {
                    purchaseEventPlanSubscription(clientSecret, "Flat Fee purchase: $planName")
                }
                paymentSuccessPopUp()
//                lifecycleScope.launch {
//                    delay(1000)
//                    startActivity(
//                        Intent(
//                            this@PackageDetailActivity,
//                            ServicesHistoryActivity::class.java
//                        ).putExtra("fromServices", 1)
//                    )
//                }
            } else {
                paymentPopup.dismiss()
                CustomToasts.showToast(this, it.message, false)
            }
        }

        observe(viewModel.subscriptionForGuestLivaData) {
            if (it.status) {
                val planName = it.data.plan_detail.plan_name
                val clientSecret = it.data.client_secret
                if (planName.isNotEmpty() || clientSecret.isNotEmpty()) {
                    purchaseEventPlanSubscription(
                        clientSecret, "Flat Fee purchase: $planName"
                    )
                }
                popUp.dismiss()
                paymentSuccessPopUp()
            } else {
                paymentPopup.dismiss()
                Toast.makeText(this@PackageDetailActivity, it.message, Toast.LENGTH_SHORT).show()
//                popUp.showToast(this@PackageDetailActivity,it.message)
//                CustomToasts.showToast(this, it.message, false)
            }
        }

        observe(viewModel.subscriptionForGuestLivaDataForSignUp) {
            if (it.status) {
//                val planName = it.data.plan_detail.plan_name
//                val clientSecret = it.data.client_secret
//                if (planName.isNotEmpty() || clientSecret.isNotEmpty()) {
//                    purchaseEventPlanSubscription(
//                        clientSecret,
//                        "Flat Fee purchase: $planName"
//                    )
//                }
                paymentSuccessPopUp()
                popUp.dismiss()

            } else {
                paymentPopup.dismiss()
                Toast.makeText(this@PackageDetailActivity, it.message, Toast.LENGTH_SHORT).show()
//                popUp.showToast(this@PackageDetailActivity,it.message)
//                CustomToasts.showToast(this, it.message, false)
            }
        }

        observe(viewModel.getProfileLiveData) {
            if (it.data?.user?.pm_last_four != "" && it.data?.user?.pm_last_four != null) {
                isUpdate = true
                cardToken = it.data.user.card_token!!
                cardLastFour = it.data.user.pm_last_four

                mCardHolder = it.data.user.card_holder_name!!
                mExpiryDate = it.data.user.expire_date!!
            }

            paymentPopup = PaymentPopUp(false, object : PaymentPopUp.PaymentPopUpInterface {
                override fun onGooglePayClick() {
                    requestPayment()
                }

                override fun onCardPayClick(
                    token: String,
                    isCardSave: Boolean,
                    cardNo: String,
                    cardHolder: String,
                    expiryDate: String
                ) {
                    if (isCardSave) {
                        subscriptionApiCall(
                            cardToken,
                            isCardSave,
                            cardLastFour,
                            mCardHolder,
                            mExpiryDate,
                            false
                        )
                    } else {
                        var lastFourDigits = cardNo.substring(cardNo.length - 4)
                        subscriptionApiCall(
                            token,
                            isCardSave,
                            lastFourDigits,
                            cardHolder,
                            expiryDate,
                            false
                        )
                    }
                }
            }, object : PaymentPopUp.PaymentError {
                override fun onPaymentError(e: String) {
                    CustomToasts.showToast(this@PackageDetailActivity, e, false)
                }
            }, isUpdate, object : PaymentPopUp.GoToUpdatePaymentScreen {
                override fun onNavigateToUpdateScreen() {
                    paymentPopup.dismiss()
                    startActivity(
                        Intent(
                            this@PackageDetailActivity, PaymentDetailActivity::class.java
                        )
                    )
                }
            }, cardLastFour, mCardHolder,
                mExpiryDate
            )
            paymentPopup.show(supportFragmentManager, "")
        }
    }

    private fun pricePopUp() {
        val popup = PricePopUp(priceList, object : PricePopUp.PriceInterface {
            override fun onPriceClick(index: Int, data: PlanPrice) {
                binding.pricePop?.setText("$${data.price}")
                planId = data.plan_id
            }
        })
        popup.show(supportFragmentManager, "PricePopUp")
    }

    private fun paymentSuccessPopUp() {
        paymentSuccessPopUp =
            PaymentSuccessPopUp(object : PaymentSuccessPopUp.PaymentSuccessPopInterface {
                override fun onSuccessEmailUsInformationClick(Email: String) {
                }

            }, object : PaymentSuccessPopUp.DialogueDismiss {
                override fun onDialogueDismiss() {
                    startActivity(Intent(this@PackageDetailActivity, HomeHost::class.java))
                }

            })
        paymentSuccessPopUp.show(supportFragmentManager, "successPaymentPopUp")
    }

    override fun onPriceClick(index: Int, data: PlanPrice) {

    }

    private fun setEditTexts() {
        binding.quentityEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val mString = binding.quentityEt?.text.toString()
                if (mString.isNotEmpty()) {
                    val price = binding.quentityEt!!.text.toString()
                    val totalAmount = (fromAmount?.times(price.toDouble()))
                    quantity = price.toInt()
                    binding.tvFromAmount.text = "$${totalAmount?.toInt()?.formatDecimalSeparator()}"
                } else {
                    binding.tvFromAmount.text = "$${fromAmount?.formatDecimalSeparator()}"
                }
            }
        })
    }

    private fun Int.formatDecimalSeparator(): String {
        return toString().reversed().chunked(3).joinToString(",").reversed()
    }


    override fun onStart() {
        super.onStart()
//        val user = SharePreferenceHelper.getInstance(this).getUser()
//        if (user.is_guest == 0) {
//            getUserCardDetailApiCall(user.email!!)
//        }

    }
}

