package com.open.borders.views.fragments.scheduleConsulting

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.gson.Gson
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.customPopups.*
import com.open.borders.databinding.ScheduleConsultingFragmentBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.ConsultationViewModelModule
import com.open.borders.models.generalModel.User
import com.open.borders.models.generalModel.paymentModels.PaymentInformation
import com.open.borders.models.generalModel.paymentModels.Token
import com.open.borders.models.responseModel.AcuityModel
import com.open.borders.models.responseModel.AppointmentTypeModel
import com.open.borders.models.responseModel.CalenderModel
import com.open.borders.models.responseModel.TimeZoneModelItem
import com.open.borders.thirdPartyModules.htmltextview.HtmlResImageGetter
import com.open.borders.thirdPartyModules.htmltextview.HtmlTextView
import com.open.borders.utils.*
import com.open.borders.views.activities.account.paymentDetail.PaymentDetailActivity
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.module.Module
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@InternalSerializationApi
class ScheduleConsultingFragment :
    MainMVVMNavigationFragment<ScheduleConsultingViewModel>(ScheduleConsultingViewModel::class),
    SelectTimeAndDateDialogInterface, ConsultationWithAdaptor.CalenderListener,
    ConsultationTypeAdaptor.ConsultationType, AcuityDateTimeAdaptor.OnAcuityDateTimeInterface {
    private lateinit var binding: ScheduleConsultingFragmentBinding
    private var language: String? = null
    private var cardLastFour: String = ""
    private var mCardHolder: String = ""
    private var mExpiryDate: String = ""
    private var isUpdate = false
    private var cardToken = ""
    var calenderList = mutableListOf<CalenderModel>()
    var acuityDateTimeList = mutableListOf<List<AcuityModel>>()
    var appointmentTypeList = mutableListOf<AppointmentTypeModel>()
    var typeList = mutableListOf<AppointmentTypeModel>()
    var timeZoneList = mutableListOf<TimeZoneModelItem>()
    var timeZoneListFinal: ArrayList<String> = ArrayList()
    private var mDescriptionTV: HtmlTextView? = null
    lateinit var user: User
    private var phoneNumber = ""
    private lateinit var paymentsClient: PaymentsClient
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
    private val LOAD_PAYMENT_DATA_REQUEST_CODE_For_Guest_Mode = 992
    private var consultationType: String? = null
    private var consultationWith: String? = null

    //    val activityResultViewModel: ActivityResultViewModel by viewModel()
    lateinit var paymentPopup: PaymentPopUp
    private var cDate: String? = null
    private var cTime: String? = null
    private var cType: String? = null
    private var cWith: String? = null
    private var cTypeId: String? = null
    private var cCalenderId: String? = null
    private var cTimeZone: String? = null
    private var cDateTime: String? = null
    private var cCurrentDateTime: String? = null
    private var cCurrentDateFinal: String? = null
    private var cCurrentDateCheck: String? = null
    private var isPrevious: Int = 0
    private var isFirstTime: Int = 1
    private var dateCheck = ""
    lateinit var paymentSuccessPopUp: PaymentSuccessPopUp
    private var stripeToken: String? = null
    private val stripe: Stripe by lazy {
        Stripe(
            requireContext(), Constants.stripePublishKey
        )
    }

    //    private val cTimeZoneFinal = TimeZone.getDefault()
    private var cTimeZoneFinal = ""
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var phoneNo = ""
    private var currentCountryCode = ""
    private var streetAddress = ""
    private var countryNameCode = ""
    private var city = ""
    private var state = ""
    private var zipCode = ""
    private var description = ""
    private var mPassword = ""
    private var confirm_password = ""
    private var is_Login = 0
    private val consultationWithAdaptor: ConsultationWithAdaptor by lazy {
        ConsultationWithAdaptor(requireContext(), this)
    }

    private val consultationTypeAdaptor: ConsultationTypeAdaptor by lazy {
        ConsultationTypeAdaptor(requireContext(), this)
    }

    private val getAcuityAdapter: AcuityDateTimeAdaptor by lazy {
        AcuityDateTimeAdaptor(requireContext(), this)
    }

    override fun registerModule(): Module {
        return ConsultationViewModelModule
    }

    override fun getLayoutResId() = R.layout.schedule_consulting_fragment

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        logScreenName("Consultation")
        toolbarControl()
        attachViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ScheduleConsultingFragmentBinding.bind(view)
        passwordEyeListener()
        language = tinyDB.getString(Constants.language)

//      binding.showPasswordCheck?.isChecked = false
        user = SharePreferenceHelper.getInstance(requireContext()).getUser()
        currentCountryCode = binding.countryCodeList?.selectedCountryCodeWithPlus.toString()
        mDescriptionTV = view.findViewById(R.id.tvConsultingCharges)
        when (language) {
            "es" -> {
                mDescriptionTV?.setHtml(
                    "<p><strong><span style=\"font-size:20px;\">Puede programar una consulta por teléfono o vídeo con uno de nuestros experimentados abogados de inmigración, que revisará su caso y responderá a todas sus preguntas.</span></strong><br>\n" + "</p>\n" + "<p><strong></strong>\n" + "</p>\n" + "<p><span style=\"font-size:18px;\">Durante más de 20 años, hemos estado ayudando a los clientes a comprender sus opciones y guiarlos hacia las mejores soluciones.</span>\n" + "</p>\n" + "<p>\uD83D\uDCE5 Puede enviarnos por correo electrónico los documentos para que los revisemos durante la consulta.<br>\n" + "</p>\n" + "<p><span style=\"font-size:16px;\">\uD83D\uDCB3 </span><strong><span style=\"font-size:16px;\">Cobramos una tarifa plana de 300 \$ por una consulta.</span></strong><br>\n" + "</p>"
                )
            }
            else -> {
                mDescriptionTV?.setHtml(
                    "<p><strong><span style=\"font-size:20px;\">You can schedule a consultation by phone or video with one of our experienced immigration attorneys, who will review your case and answer all your questions.</span></strong><br>\n" + "</p>\n" + "<p><strong></strong>\n" + "</p>\n" + "<p><span style=\"font-size:18px;\">For over 20 years, we have been helping clients understand their options and guide them to the best solutions.</span>\n" + "</p>\n" + "<p>\uD83D\uDCE5 You can e-mail us documents to review during the consultation.<br>\n" + "</p>\n" + "<p><span style=\"font-size:16px;\">\uD83D\uDCB3 </span><strong><span style=\"font-size:16px;\">We charge a flat fee of \$300 for a consultation.</span></strong><br>\n" + "</p>"
                )
            }
        }

        if (mActivity != null) {
            HtmlResImageGetter(mActivity)
        }

        binding.showPasswordCheck?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                is_Login = 1
                binding.passwordLayout?.viewVisible()
            } else {
                is_Login = 0
                binding.passwordLayout?.viewGone()
            }
        }

//        binding.termAndConditionLayout?.setOnClickListener {
//            if (!binding.showPasswordCheck!!.isChecked) {
//                binding.showPasswordCheck?.isChecked = true
//                is_Login = 1
//                binding.passwordLayout?.viewVisible()
//            } else {
//                binding.showPasswordCheck?.isChecked = false
//                is_Login = 1
//                binding.passwordLayout?.viewGone()
//            }
//        }
//        callPackagesApi()

        getTimeZone()
        getCalenderApiCall()
        if (user.is_guest == 0) {
            setData()
        }

        setEditTexts()
        setClickListeners()
//        populateAndUpdateTimeZone()
//        getAppointmentTypeApiCall()
//        consultantSelection()
//        radioCheck()
//        swipeRefresh()

        cCurrentDateTime = LocalDateTime.now().toString()
        cCurrentDateCheck = cCurrentDateTime!!.toDate().formatTo("yyyy-MM-dd")
        cCurrentDateFinal = cCurrentDateTime!!.toDate().formatTo("yyyy-MM-dd")
        Log.e("CurrentDate", "Current Date is : $cCurrentDateFinal")

        if (cCurrentDateFinal == cCurrentDateCheck) {
            binding.previousTv.viewGone()
            binding.moreTimeTv.viewGone()
        }

        paymentsClient = PaymentsUtil.createPaymentsClient(requireActivity())
        possiblyShowGooglePayButton()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners() {
        binding.buttonStartConsultation.setOnClickListener {
            firstName = binding.fistNameEt.text.toString()
            lastName = binding.lastNameEt.text.toString()
            email = binding.emailEt.text.toString()
            phoneNo = binding.etPhone?.unMaskedText.toString()
            streetAddress = binding.streetAddressEt.text.toString()
            countryNameCode = binding.countryName?.selectedCountryName.toString()
            city = binding.cityEt.text.toString()
            state = binding.stateEt.text.toString()
            zipCode = binding.zipEt.text.toString()
            description = binding.descriptionEt.text.toString()
            mPassword = binding.signupPasswordTv?.text.toString()
            confirm_password = binding.signupConfirmPasswordTv?.text.toString()

            if (cWith.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), "Please select consultant", false)
            } else if (cType.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), "Please select consultation type", false)
            } else if (cDate.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), "Date is required", false)
            } else if (cTime.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), "Time is required", false)
            } else if (firstName.isEmpty()) {
                binding.fistNameEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.errorFirstNameTv.viewVisible()
                binding.errorFirstNameTv.text = getString(R.string.please_enter_firstName)
                binding.fistNameEt.requestFocus()
            } else if (lastName.isEmpty()) {
                binding.lastNameEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.errorLastNameTv?.viewVisible()
                binding.errorLastNameTv?.text = getString(R.string.please_enter_lastName)
                binding.lastNameEt.requestFocus()
            } else if (email.isEmpty()) {
                binding.emailEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.emailErrorTv.viewVisible()
                binding.emailErrorTv.text = getString(R.string.please_enter_email)
                binding.emailEt.requestFocus()
            } else if (isValidEmail(email).not()) {
                binding.emailEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.emailErrorTv.viewVisible()
                binding.emailErrorTv.text = getString(R.string.please_enter_valid_email_address)
                binding.emailEt.requestFocus()
            } else if (phoneNo.isEmpty()) {
                binding.etPhone?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.phoneErrorTv.viewVisible()
                binding.phoneErrorTv.text = getString(R.string.please_enter_phoneNo)
                binding.etPhone?.requestFocus()
            } else if (phoneNo.length < 10) {
                binding.etPhone?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.phoneErrorTv.viewVisible()
                binding.phoneErrorTv.text = getString(R.string.please_enter_phoneNo_characters)
                binding.etPhone?.requestFocus()
            } else if (streetAddress.isEmpty()) {
                binding.streetAddressEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.errorStreetTv.viewVisible()
                binding.errorStreetTv.text = getString(R.string.please_enter_street)
                binding.streetAddressEt.requestFocus()
            } else if (city.isEmpty()) {
                binding.cityEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.errorCityTv.viewVisible()
                binding.errorCityTv.text = getString(R.string.please_enter_cityName)
                binding.cityEt.requestFocus()
            } else if (state.isEmpty()) {
                binding.stateEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.errorStateTv.viewVisible()
                binding.errorStateTv.text = getString(R.string.please_enter_state)
                binding.stateEt.requestFocus()
            } else if (zipCode.isEmpty()) {
                binding.zipEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.errorZipTv.viewVisible()
                binding.errorZipTv.text = getString(R.string.please_enter_zip)
                binding.zipEt.requestFocus()
            } else if (countryNameCode.isEmpty()) {
                binding.countryName?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                binding.errorCountry.viewVisible()
                binding.errorCountry.text = getString(R.string.please_enter_country)
                binding.countryName?.requestFocus()
            } else if (description.isEmpty()) {
                CustomToasts.showPositiveToast(
                    requireActivity(), getString(R.string.details_required), true
                )
            } else if (!binding.termsCheck.isChecked) CustomToasts.showToast(
                requireActivity(), getString(R.string.please_check_terms_and_conditions), false
            )
            else {
                if (user.is_guest == 1) {
                    if (binding.showPasswordCheck?.isChecked!!) {
                        if (mPassword.isEmpty()) {
                            binding.signupPasswordTv?.backgroundTintList =
                                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                            binding.errorPasswordTv?.viewVisible()
                            binding.errorPasswordTv?.text =
                                getString(R.string.please_enter_password)
                            binding.signupPasswordTv?.requestFocus()
                        } else if (mPassword.length < 8) {
                            binding.signupPasswordTv?.backgroundTintList =
                                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                            binding.errorPasswordTv?.viewVisible()
                            binding.errorPasswordTv?.text =
                                getString(R.string.please_enter_minimum_8_characters_long_password)
                            binding.signupPasswordTv?.requestFocus()
                        } else if (confirm_password.isEmpty()) {
                            binding.signupConfirmPasswordTv?.backgroundTintList =
                                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                            binding.errorConfirmPasswordTv?.viewVisible()
                            binding.errorConfirmPasswordTv?.text =
                                getString(R.string.password_and_confirm_password_does_not_match)
                            binding.signupConfirmPasswordTv?.requestFocus()
                        } else if (confirm_password.length < 8) {
                            binding.signupConfirmPasswordTv?.backgroundTintList =
                                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                            binding.errorConfirmPasswordTv?.viewVisible()
                            binding.errorConfirmPasswordTv?.text =
                                getString(R.string.password_and_confirm_password_does_not_match)
                            binding.signupConfirmPasswordTv?.requestFocus()
                        } else if (mPassword != confirm_password) {
                            binding.signupConfirmPasswordTv?.backgroundTintList =
                                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                            binding.errorConfirmPasswordTv?.viewVisible()
                            binding.errorConfirmPasswordTv?.text =
                                getString(R.string.password_and_confirm_password_does_not_match)
                            binding.signupConfirmPasswordTv?.requestFocus()
                        } else {
                            paymentPopup =
                                PaymentPopUp(false, object : PaymentPopUp.PaymentPopUpInterface {
                                    override fun onGooglePayClick() {
                                        requestPaymentForGuestMode()
                                    }

                                    override fun onCardPayClick(
                                        token: String,
                                        isCardSave: Boolean,
                                        cardNumber: String,
                                        cardHolder: String,
                                        expiryDate: String
                                    ) {
                                        bookConsultation(
                                            token, "GuestMode", isCardSave, "", false, "", ""
                                        )
                                    }
                                }, object : PaymentPopUp.PaymentError {
                                    override fun onPaymentError(e: String) {
                                        CustomToasts.failureToast(requireContext(), e)
                                    }
                                }, isUpdate, object : PaymentPopUp.GoToUpdatePaymentScreen {
                                    override fun onNavigateToUpdateScreen() {
                                        paymentPopup.dismiss()
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                PaymentDetailActivity::class.java
                                            )
                                        )
                                    }
                                }, cardLastFour, mCardHolder, mExpiryDate
                                )
                            paymentPopup.show(childFragmentManager, "")
                        }
                    } else {
                        paymentPopup =
                            PaymentPopUp(false, object : PaymentPopUp.PaymentPopUpInterface {
                                override fun onGooglePayClick() {
                                    requestPaymentForGuestMode()
                                }

                                override fun onCardPayClick(
                                    token: String,
                                    isCardSave: Boolean,
                                    lastFourDigits: String,
                                    cardHolder: String,
                                    expiryDate: String
                                ) {
                                    bookConsultation(
                                        token, "GuestMode", isCardSave, "", false, "", ""
                                    )
                                }
                            }, object : PaymentPopUp.PaymentError {
                                override fun onPaymentError(e: String) {
                                    CustomToasts.failureToast(requireContext(), e)
                                }
                            }, isUpdate, object : PaymentPopUp.GoToUpdatePaymentScreen {
                                override fun onNavigateToUpdateScreen() {
                                    paymentPopup.dismiss()
                                    startActivity(
                                        Intent(
                                            requireContext(), PaymentDetailActivity::class.java
                                        )
                                    )
                                }
                            }, cardLastFour, mCardHolder, mExpiryDate
                            )
                        paymentPopup.show(childFragmentManager, "")
                    }
                } else {
                    getUserCardDetailApiCall()
                }
            }
        }

        binding.tvTime.setOnClickListener {
            if (cWith.isNullOrEmpty() || cWith == "null") {
                CustomToasts.showToast(requireActivity(), "Please select consultant", false)
            } else if (cType.isNullOrEmpty() || cType == "null") {
                CustomToasts.showToast(requireActivity(), "Please select consultation type", false)
            } else if (cDate.isNullOrEmpty() || cDate == "null") {
                CustomToasts.showToast(requireActivity(), "Date is required", false)
            } else {
                val popup = SelectTimePopup(
                    mActivity,
                    this,
                    cDate.toString(),
                    cCalenderId.toString(),
                    cTypeId.toString(),
                    cTimeZone.toString()
                )
                popup.show(childFragmentManager, "SelectTimePopup")
            }
        }

        binding.tvDate.setOnClickListener {
            if (cWith.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), "Please select consultant", false)
            } else if (cType.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), "Please select consultation type", false)
            } else {
                val popup = SelectDatePopup(this, cCalenderId.toString(), cTypeId.toString())
                popup.show(childFragmentManager, "SelectDatePopup")
            }
        }

        binding.moreTimeTv.setOnClickListener {
            isPrevious = 0
            binding.noDataView?.viewGone()
            binding.accountDetailLayout.viewGone()
            binding.acuityRecyclerView.viewGone()
            binding.shimmerFrameLayout2.viewVisible()
            binding.previousTv.viewVisible()
            if (!acuityDateTimeList.isNullOrEmpty()) {
                val date = acuityDateTimeList.last()
                val cal = Calendar.getInstance()
                cal.time = date.last().time.toDate()
                cal.add(Calendar.DAY_OF_MONTH, 1)
                cCurrentDateFinal = cal.time.toFormat()
                getAcuityDateAndTime()
            } else {
                val cal = Calendar.getInstance()
                cal.time = cCurrentDateTime?.toDate()!!
                cal.add(Calendar.MONTH, 1)
//                cal.time = nextMonth.toDate()
                cal.set(Calendar.DATE, 1)
                cCurrentDateFinal = cal.time.toFormat()
                Log.e("MoreFinalDate", cCurrentDateFinal!!)
                getAcuityDateAndTime()
            }
        }

        binding.previousTv.setOnClickListener {
            isPrevious = 1
            binding.noDataView?.viewGone()
            binding.accountDetailLayout.viewGone()
            binding.acuityRecyclerView.viewGone()
            binding.shimmerFrameLayout2.viewVisible()
            if (!acuityDateTimeList.isNullOrEmpty()) {
                val date = acuityDateTimeList.first()[0].time
                cCurrentDateFinal = date.toDate().formatTo("yyyy-MM-dd")
            }
//            val cal = Calendar.getInstance()
//            cal.time = date.first().time.toDate()
//            cal.add(Calendar.DAY_OF_MONTH, -7)
//            Log.e("Previous Date", "${cal.time}")
//            // Set up our formatter with a custom pattern
//            cCurrentDateFinal = cal.time.toFormat()
//            Log.e("Final Date", "${cCurrentDateFinal}")
            getAcuityDateAndTime()
        }
    }

    //Get UserDetail Api Call
    private fun getUserCardDetailApiCall() {
        if (isNetworkAvailable(requireActivity())) {
//            viewModel.getUser(email.toRequestBody())
            viewModel.getProfile()
        } else {
            CustomToasts.showToast(
                requireActivity(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }

    override fun onStart() {
        super.onStart()
        logScreenName("Consultation")
    }

    private fun setAdapter() {
        binding.consultationWithRecyclerView.let {
            it?.layoutManager = LinearLayoutManager(requireContext())
            it?.adapter = consultationWithAdaptor
        }
    }

    private fun passwordEyeListener() {
        binding.signUpPasswordEye?.setOnClickListener() {
            hideShowPassword(
                binding.signupPasswordTv!!, binding.signUpPasswordEye!!
            )
        }

        binding.signUpConfirmPasswordEye?.setOnClickListener() {
            hideShowPassword(
                binding.signupConfirmPasswordTv!!, binding.signUpConfirmPasswordEye!!
            )
        }
    }

    private fun setConsultationAdapter() {
        binding.consultationTypeRecyclerView.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = consultationTypeAdaptor
        }
    }

    private fun setAcuityAdapter() {
        binding.acuityRecyclerView.let {
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            it.adapter = getAcuityAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSaveDateClick(date: String, timeZone: String) {
        cTime = ""
        binding.tvTime.text = "Time"
        cDate = date
        cTimeZone = timeZone
        if (!cDate.isNullOrEmpty()) {
            binding.tvDate.text = cDate
        }
    }

    override fun onSaveTimeClick(time: String, dateTime: String) {
        cTime = time
        cDateTime = dateTime
        if (cTime!!.isNotEmpty()) {
            binding.tvTime.text = cTime
        }
    }

    override fun onContinueConsultationClick(type: String, consultationWith: String) {
        cType = type
        cWith = consultationWith
        requestPayment()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
//                binding.shimmerFrameLayout?.viewGone()
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }

        observe(viewModel.bookConsultationList) {
            if (it.status) {
                calenderList.forEach { item ->
                    item.isSelected = false
                }
                purchaseEventPlan("true")
                consultationWithAdaptor.notifyDataSetChanged()
                cDate = ""
                cTime = ""
                cType = ""
                cWith = ""
                cDateTime = ""
                cTypeId = ""
                cCalenderId = ""
                streetAddress = ""
                city = ""
                state = ""
                zipCode = ""
                countryNameCode = ""
                description = ""
                (activity as HomeHost).activityResultViewModel.resultData.value = ""
                binding.consultationTypeTv.viewGone()
                binding.consultationTypeRecyclerView.viewGone()
                binding.timeSlotLayout.viewGone()
                binding.acuityRecyclerView.viewGone()
                binding.accountDetailLayout.viewGone()
                binding.acuityRecyclerView.viewGone()

                paymentSuccessPopUp()
                paymentPopup.dismiss()
                CustomToasts.showToast(requireActivity(), it.message, true)
//                lifecycleScope.launch {
//                    delay(1000)
//                    startActivity(
//                        Intent(
//                            requireContext(),
//                            ServicesHistoryActivity::class.java
//                        )
//                    )
//                }
            }
            (activity as HomeHost).activityResultViewModel.resultData.value = null
        }

        observe(viewModel.bookConsultationListForGuestMode) {
            if (it.status) {
                calenderList.forEach { item ->
                    item.isSelected = false
                }
                purchaseEventPlan("true")
                consultationWithAdaptor.notifyDataSetChanged()
                cDate = ""
                cTime = ""
                cType = ""
                cWith = ""
                cDateTime = ""
                cTypeId = ""
                cCalenderId = ""
                streetAddress = ""
                city = ""
                state = ""
                zipCode = ""
                countryNameCode = ""
                description = ""
                (activity as HomeHost).activityResultViewModel.resultData.value = ""
                binding.consultationTypeTv.viewGone()
                binding.consultationTypeRecyclerView.viewGone()
                binding.timeSlotLayout.viewGone()
                binding.acuityRecyclerView.viewGone()
                binding.accountDetailLayout.viewGone()
                binding.acuityRecyclerView.viewGone()
                paymentSuccessPopUp()
                paymentPopup.dismiss()
                CustomToasts.showToast(requireActivity(), it.message, true)
//                lifecycleScope.launch {
//                    delay(1000)
//                    startActivity(
//                        Intent(
//                            requireContext(),
//                            ServicesHistoryActivity::class.java
//                        )
//                    )
//                }
            }
            (activity as HomeHost).activityResultViewModel.resultData.value = null
        }

        observe(viewModel.bookConsultationListForGuestModeForCreateAccount) {
            if (it.status) {
                calenderList.forEach { item ->
                    item.isSelected = false
                }
                purchaseEventPlan("true")
                consultationWithAdaptor.notifyDataSetChanged()
                cDate = ""
                cTime = ""
                cType = ""
                cWith = ""
                cDateTime = ""
                cTypeId = ""
                cCalenderId = ""
                streetAddress = ""
                city = ""
                state = ""
                zipCode = ""
                countryNameCode = ""
                description = ""
                (activity as HomeHost).activityResultViewModel.resultData.value = ""
                binding.consultationTypeTv.viewGone()
                binding.consultationTypeRecyclerView.viewGone()
                binding.timeSlotLayout.viewGone()
                binding.acuityRecyclerView.viewGone()
                binding.accountDetailLayout.viewGone()
                binding.acuityRecyclerView.viewGone()

                paymentSuccessPopUp()
                paymentPopup.dismiss()
                CustomToasts.showToast(requireActivity(), it.message, true)
//                lifecycleScope.launch {
//                    delay(1000)
//                    startActivity(
//                        Intent(
//                            requireContext(),
//                            ServicesHistoryActivity::class.java
//                        )
//                    )
//                }

            }

            (activity as HomeHost).activityResultViewModel.resultData.value = null
        }

        observe(viewModel.getCalenderLiveData) {
            if (it.status) {
                binding.shimmerFrameLayout.viewGone()
                binding.consultationWithRecyclerView.viewVisible()
                calenderList = it.data.toMutableList()
                consultationWithAdaptor.calenderList = calenderList
//                setAdapter() //Code is commented when client requested to load his profile directly
                getAndSetupAppointmentTypeDetails() //
            }
        }

        observe(viewModel.getAppointmentTypeLiveData) {
            if (it.status) {
                binding.shimmerFrameLayout1.viewGone()
                binding.consultationTypeRecyclerView.viewVisible()
                appointmentTypeList = it.data.toMutableList()
                typeList.clear()

                if (cCalenderId == "2420243") {
                    appointmentTypeList.forEach {
                        if (it.id.toString() == "16496767") {
                            typeList.add(it)
                            consultationTypeAdaptor.consultationTypeList = typeList
//                            setConsultationAdapter()
                            getAcuityDateAndTimeDetails()
                        }
                    }
                } else {
                    appointmentTypeList.forEach {
                        if (it.id.toString() == "16496767" || it.id.toString() == "22885774") {
                            typeList.add(it)
                        }
                    }
                    consultationTypeAdaptor.consultationTypeList = typeList
                    getAcuityDateAndTimeDetails()
//                    setConsultationAdapter()
                }
            }
        }

        //fixme
        //This is for google pay result from activity
        observe((activity as HomeHost).activityResultViewModel.resultData) {
            if (!it.isNullOrBlank()) {
                val paymentData = Gson().fromJson(it, PaymentInformation::class.java)
                val token = paymentData.paymentMethodData.tokenizationData.token
                val tokenData = Gson().fromJson(token, Token::class.java)
                val finalToken = tokenData.id
                Log.e("token", finalToken)
                if (user.is_guest == 1) {
                    bookConsultation(finalToken, "GuestMode", false, "", true, "", "")
                } else {
                    bookConsultation(finalToken, "LoginMode", false, cardLastFour, true, "", "")
                }
            } else {
                CustomToasts.showToast(
                    requireActivity(), getString(R.string.toast_error), true
                )
            }
        }
        observe(viewModel.getAcuityDateAndTimeLiveDate) {
            if (it.status) {
                acuityDateTimeList.clear()
                getAcuityAdapter.acuityDateTimeList.clear()
                binding.noDataView?.viewGone()
                binding.moreTimeTv.viewVisible()
                binding.shimmerFrameLayout2.viewGone()
                binding.acuityRecyclerView.viewVisible()
                acuityDateTimeList = it.data.toMutableList()
                if (!acuityDateTimeList.isNullOrEmpty()) {
                    if (isPrevious == 1) {
                        acuityDateTimeList = it.data.toMutableList()
                        acuityDateTimeList.reverse()
//                        getAcuityAdapter.acuityDateTimeList = acuityDateTimeList
                        getAcuityAdapter.updateList(acuityDateTimeList)
                        setAcuityAdapter()
                        val date = acuityDateTimeList.first()[0].time
                        val forDateCheck = date.toDate().formatTo("yyyy-MM-dd")
                        if (forDateCheck == dateCheck) {
                            binding.previousTv.viewGone()
                        }
                        isPrevious = 0
                    } else {
//                        acuityDateTimeList = it.data.toMutableList()
//                        getAcuityAdapter.acuityDateTimeList = acuityDateTimeList
                        getAcuityAdapter.updateList(acuityDateTimeList)
                        setAcuityAdapter()
                    }
                } else {
                    binding.previousTv.viewGone()
                    binding.acuityRecyclerView.viewGone()
                    binding.noDataView?.viewVisible()
                }

                if (!acuityDateTimeList.isNullOrEmpty()) {
                    if (isFirstTime == 1) {
                        val date = acuityDateTimeList.first()[0].time
                        dateCheck = date.toDate().formatTo("yyyy-MM-dd")
                        isFirstTime++
                    }
                }
            }
        }

        observe(viewModel.getTimeZoneList) {
            timeZoneList = it.toMutableList()
            populateTimeZone()
        }

        observe(viewModel.getProfileLiveData) {
            if (it.data?.user?.pm_last_four != "" && it.data?.user?.pm_last_four != null) {
                isUpdate = true
                cardToken = it.data.user.card_token!!
                cardLastFour = it.data.user.pm_last_four
                mCardHolder = it.data.user.card_holder_name!!
                mExpiryDate = it.data.user.expire_date!!
            }
            //fixme
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
                        bookConsultation(
                            cardToken,
                            "LoginMode",
                            isCardSave,
                            cardLastFour,
                            false,
                            mCardHolder,
                            mExpiryDate
                        )
                    } else {
                        var lastFourDigits = cardNo.substring(cardNo.length - 4)
                        bookConsultation(
                            token,
                            "LoginMode",
                            isCardSave,
                            lastFourDigits,
                            false,
                            cardHolder,
                            expiryDate
                        )
                    }
                }

            }, object : PaymentPopUp.PaymentError {
                override fun onPaymentError(e: String) {
                    CustomToasts.failureToast(requireContext(), e)
                }
            }, isUpdate, object : PaymentPopUp.GoToUpdatePaymentScreen {
                override fun onNavigateToUpdateScreen() {
                    paymentPopup.dismiss()
                    startActivity(
                        Intent(
                            requireContext(), PaymentDetailActivity::class.java
                        )
                    )
                }
            }, cardLastFour, mCardHolder, mExpiryDate
            )
            paymentPopup.show(childFragmentManager, "")
        }
    }

    private fun requestPayment() {
        val price = 300.00
        val priceCents = Math.round(price * PaymentsUtil.CENTS.toLong())
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents)
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(request),
            requireActivity(),
            LOAD_PAYMENT_DATA_REQUEST_CODE
        )
    }

    private fun requestPaymentForGuestMode() {
        val price = 300.00
        val priceCents = Math.round(price * PaymentsUtil.CENTS.toLong())
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents)
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(request),
            requireActivity(),
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
            binding.buttonStartConsultation.viewVisible()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.Unfortunately_google_pay_is_not_available_on_this_device),
                Toast.LENGTH_LONG
            ).show()
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            // Value passed in AutoResolveHelper
//            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
//                when (resultCode) {
//                    RESULT_OK ->
//                        data?.let { intent ->
//                            try {
//                                val paymentData = PaymentData.getFromIntent(intent)
//                                handlePaymentSuccess(paymentData!!)
//                            } catch (e: Throwable) {
//                                e.localizedMessage
//                            }
//                        }
//
//                    AppCompatActivity.RESULT_CANCELED -> {
////                        CustomToasts.showToast(this, "Payment Cancelled", false)
//                    }
//
//                    AutoResolveHelper.RESULT_ERROR -> {
//                        AutoResolveHelper.getStatusFromIntent(data)?.let {
//                            handleError(it.statusCode)
//                        }
//                    }
//                }
//            }
//            LOAD_PAYMENT_DATA_REQUEST_CODE_For_Guest_Mode -> {
//                when (resultCode) {
//                    RESULT_OK ->
//                        data?.let { intent ->
//                            try {
//                                val paymentData = PaymentData.getFromIntent(intent)
//                                handlePaymentSuccessForGuestMode(paymentData!!)
//                            } catch (e: Throwable) {
//                                e.localizedMessage
//                            }
//                        }
//
//                    AppCompatActivity.RESULT_CANCELED -> {
////                        CustomToasts.showToast(this, "Payment Cancelled", false)
//                    }
//
//                    AutoResolveHelper.RESULT_ERROR -> {
//                        AutoResolveHelper.getStatusFromIntent(data)?.let {
//                            handleError(it.statusCode)
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        //Get Stripe Payment Method Token pm_token
        val params = PaymentMethodCreateParams.createFromGooglePay(JSONObject(paymentData.toJson()))

        stripe.createPaymentMethod(params, callback = object : ApiResultCallback<PaymentMethod> {
            override fun onSuccess(result: PaymentMethod) {
                // handle success

                stripeToken = result.id ?: ""
                Log.e("sToken", stripeToken.toString())

                if (!stripeToken.isNullOrBlank()) {
//                        bookConsultation(stripeToken!!, "LoginMode",)
                } else {
                    CustomToasts.showToast(
                        requireActivity(), Constants.WENT_WRONG_MESSAGE, false
                    )
                }
            }

            override fun onError(e: Exception) {
                CustomToasts.showToast(requireActivity(), e.toString(), false)
            }
        })
    }

    private fun handlePaymentSuccessForGuestMode(paymentData: PaymentData) {
        //Get Stripe Payment Method Token pm_token
        val params = PaymentMethodCreateParams.createFromGooglePay(JSONObject(paymentData.toJson()))

        stripe.createPaymentMethod(params, callback = object : ApiResultCallback<PaymentMethod> {
            override fun onSuccess(result: PaymentMethod) {
                // handle success
                stripeToken = result.id ?: ""
                Log.e("sToken", stripeToken.toString())

                if (!stripeToken.isNullOrBlank()) {

//                        bookConsultation(stripeToken!!, "GuestMode")

                } else {
                    CustomToasts.showToast(
                        requireActivity(), Constants.WENT_WRONG_MESSAGE, false
                    )
                }
            }

            override fun onError(e: Exception) {
                CustomToasts.showToast(requireActivity(), e.toString(), false)
            }
        })
    }


    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }


    override fun onImageClick(index: Int, imageUrl: String) {
        when (index) {
            0 -> {
                val uri = Uri.parse("https://bordercrossinglaw.com/erickvalencia")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            else -> {
                val uri = Uri.parse("https://bordercrossinglaw.com/shahidhaque")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }

    //Swipe to refresh
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun swipeRefresh() {
//        val swipe = binding.swipeToRefresh
//        swipe!!.setOnRefreshListener {
//            swipe.setProgressBackgroundColorSchemeColor(
//                ContextCompat.getColor(
//                    requireContext(), R.color.white
//                )
//            )
//            callPackagesApi()
//            swipe.isRefreshing = false
//        }
//    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        val homeActivity = (activity as HomeHost).binding.homeToolbarId
        homeActivity.toolbarNameTv.text = getString(R.string.consultation)
        homeActivity.backArrow.viewGone()
        homeActivity.llChangeLanguage.setOnClickListener {
            (activity as HomeHost).changeLanguageMenu()
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTabType("Consultation")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarControl()
    }

//    private fun consultantSelection(){
//        val selectionImage1 = binding.selectImage1
//        val selectionImage2 = binding.selectImage2
//
//        binding.consultant1?.setOnClickListener{
//            selectionImage1?.viewVisible()
//            selectionImage2?.viewGone()
//            consultationWith = binding.consultantTv1?.text.toString()
//        }
//
//
//        binding.consultant2?.setOnClickListener{
//            selectionImage1?.viewGone()
//            selectionImage2?.viewVisible()
//            consultationWith = binding.consultantTv2?.text.toString()
//        }
//    }

//    private fun radioCheck() {
//        binding.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
//            consultationType = if (R.id.radioPhoneCall == checkedId) {
//                "1"
//            } else {
//                "2"
//            }
//        }
//    }

    private fun getCalenderApiCall() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getCalender()
        } else {
            CustomToasts.showToast(
                requireActivity(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }

    private fun getAppointmentTypeApiCall() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getAppointmentType()
        } else {
            CustomToasts.showToast(
                requireActivity(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onNameClick(index: Int, item: CalenderModel) {
        if (user.is_guest == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cCurrentDateTime = LocalDateTime.now().toString()
            }
            cCurrentDateFinal = cCurrentDateTime!!.toDate().formatTo("yyyy-MM-dd")
            calenderList.forEach { item ->
                item.isSelected = false
            }
            calenderList[index].isSelected = true
            consultationWithAdaptor.notifyDataSetChanged()
            cWith = item.name
            cCalenderId = item.id.toString()

            cDate = ""
            cTime = ""
            binding.tvDate.text = getString(R.string.date)
            binding.tvTime.text = getString(R.string.time)

            binding.noDataView?.viewGone()
            binding.timeSlotLayout.viewGone()
            binding.timeslotTv.viewGone()
            binding.shimmerFrameLayout2.viewGone()
            binding.acuityRecyclerView.viewGone()
            binding.accountDetailLayout.viewGone()
            binding.consultationTypeTv.viewVisible()
            binding.shimmerFrameLayout1.viewVisible()
            binding.consultationTypeRecyclerView.viewGone()
            typeList.clear()
            getAppointmentTypeApiCall()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cCurrentDateTime = LocalDateTime.now().toString()
            }
            cCurrentDateFinal = cCurrentDateTime!!.toDate().formatTo("yyyy-MM-dd")
            calenderList.forEach { item ->
                item.isSelected = false
            }
            calenderList[index].isSelected = true
            consultationWithAdaptor.notifyDataSetChanged()
            cWith = item.name
            cCalenderId = item.id.toString()

            cDate = ""
            cTime = ""
            binding.tvDate.text = getString(R.string.date)
            binding.tvTime.text = getString(R.string.time)

            binding.noDataView?.viewGone()
            binding.timeSlotLayout.viewGone()
            binding.timeslotTv.viewGone()
            binding.shimmerFrameLayout2.viewGone()
            binding.acuityRecyclerView.viewGone()
            binding.accountDetailLayout.viewGone()
            binding.consultationTypeTv.viewVisible()
            binding.shimmerFrameLayout1.viewVisible()
            binding.consultationTypeRecyclerView.viewGone()
            typeList.clear()
            getAppointmentTypeApiCall()
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onTypeClick(index: Int, item: AppointmentTypeModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cCurrentDateTime = LocalDateTime.now().toString()
        }
        cCurrentDateFinal = cCurrentDateTime!!.toDate().formatTo("yyyy-MM-dd")
        typeList.forEach {
            it.isSelected = false
        }
        typeList[index].isSelected = true
        consultationTypeAdaptor.notifyDataSetChanged()
        cType = "1"
        cTypeId = item.id.toString()
        binding.noDataView?.viewGone()
        binding.acuityRecyclerView.viewGone()
        binding.timeSlotLayout.viewVisible()
        binding.timeslotTv.viewVisible()
        binding.accountDetailLayout.viewGone()
        binding.shimmerFrameLayout2.viewVisible()
        getAcuityDateAndTime()

//        cDate = ""
//        cTime = ""
//        binding.tvTime.text = getString(R.string.time)
//        binding.tvDate.text = getString(R.string.date)
    }

    private fun getAcuityDateAndTime() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getAcuityDateAndTime(
                cCurrentDateFinal.toString(),
                cTypeId.toString(),
                cCalenderId.toString(),
                cTimeZoneFinal,
                isPrevious
            )
        } else {
            CustomToasts.failureToastCenterTop(
                requireContext(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE
            )
        }
    }

    private fun setData() {
        val userData = SharePreferenceHelper.getInstance(requireContext()).getUser()
        binding.fistNameEt.setText(userData.first_name.toString())
        binding.lastNameEt.setText(userData.last_name.toString())
        binding.emailEt.setText(userData.email.toString())
//        phoneNumber = .data?.user?.phone_no.toString()
        phoneNumber = userData.phone_no.toString()
        phoneNumberPicker()
        binding.streetAddressEt.setText(userData.user_address?.street_address.toString())
        countryNameCode =
            binding.countryName?.setCountryForNameCode(userData.user_address?.country_name_code)
                .toString()
        binding.lastNameEt.setText(userData.last_name.toString())
        binding.emailEt.setText(userData.email.toString())
        binding.phoneNoEt?.setText(userData.phone_no.toString())
        binding.streetAddressEt.setText(userData.user_address?.street_address.toString())
        binding.countryEt?.setText(userData.user_address?.country.toString())
        binding.stateEt.setText(userData.user_address?.state.toString())
        binding.cityEt.setText(userData.user_address?.city.toString())
        binding.zipEt.setText(userData.user_address?.zip_code.toString())

    }

    private fun setEditTexts() {
        binding.fistNameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.fistNameEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorFirstNameTv.error = null
                binding.errorFirstNameTv.viewGone()
            }
        })

        binding.emailEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.emailEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.emailErrorTv.error = null
                binding.emailErrorTv.viewGone()
            }
        })

        binding.etPhone?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etPhone!!.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.phoneErrorTv.error = null
                binding.phoneErrorTv.viewGone()
            }
        })

        binding.signupPasswordTv?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.signupPasswordTv?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorPasswordTv?.error = null
                binding.errorPasswordTv?.viewGone()
            }
        })

        binding.signupConfirmPasswordTv?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.signupConfirmPasswordTv?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorConfirmPasswordTv?.error = null
                binding.errorConfirmPasswordTv?.viewGone()
            }
        })

//
//        binding.phoneNoEt?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                binding.phoneNoEt!!.backgroundTintList =
//                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
//                binding.phoneErrorTv?.error = null
//                binding.phoneErrorTv?.viewGone()
//            }
//        })

        binding.streetAddressEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.streetAddressEt!!.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorStreetTv?.error = null
                binding.errorStreetTv?.viewGone()
            }
        })

        binding.cityEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.cityEt!!.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorCityTv?.error = null
                binding.errorCityTv?.viewGone()
            }
        })

        binding.zipEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.zipEt!!.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorZipTv?.error = null
                binding.errorZipTv?.viewGone()
            }
        })

//        binding.countryName?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                binding.countryEt!!.backgroundTintList =
//                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
//                binding.errorCountry?.error = null
//                binding.errorCountry?.viewGone()
//            }
//        })

        binding.descriptionEt.addTextChangedListener(object : TextWatcher {
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onTimeClick(index: Int, childIndex: Int, time: String, timeDate: String) {
        cDate = timeDate.toDate().formatTo("yyyy-MM-dd")
        cTime = time
        cDateTime = timeDate
        binding.accountDetailLayout?.viewVisible()

        if (user.is_guest == 1) {
            binding.termsCheck?.isChecked = true
            binding.termsCheck.isEnabled = false
            binding.showPasswordCheck?.viewVisible()
//            binding.passwordLayout?.viewVisible()
        }

//        CustomToasts.successToastCenterTop(requireContext(), "$time clicked")

        acuityDateTimeList.forEach { parentList ->
            parentList.forEach {
                it.isSelected = false
            }
        }
        acuityDateTimeList[index][childIndex].isSelected = true
        getAcuityAdapter.notifyDataSetChanged()
    }

    private fun phoneNumberPicker() {
//       var selectedCountryCode = binding.countryCode?.selectedCountryCode.toString()
        var code = if (phoneNumber.contains("+")) {
            (phoneNumber).parseCountryCode()
        } else {
            (phoneNumber).parseCountryCode()
        }
        var newNumber = phoneNumber.replace("+$code", "")
        binding.etPhone?.setText(newNumber)
        try {
            if (code.contains("+")) {
                code = code.replace("+", "")
                binding.countryCodeList?.setCountryForPhoneCode(code.toInt())
            } else {
                binding.countryCodeList?.setCountryForPhoneCode(code.toInt())
            }
        } catch (e: Exception) {
//            CustomToasts.showToast(requireActivity(), e.toString(), false)
            null
        }
    }

    private fun refreshCurrentFragment() {
        val navController = findNavController()
        val id = navController.currentDestination?.id
        navController.popBackStack(id!!, true)
        navController.navigate(id)
    }

//    private fun populateAndUpdateTimeZone() {
//        //populate spinner with all timezones
//        val idArray = timeZoneListFinal
//        val mAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            idArray
//        )
//        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.timeZoneSpinner?.adapter = mAdapter
//
//        // now set the spinner to default timezone from the time zone settings
//        for (i in 0 until mAdapter.count) {
//            if (mAdapter.getItem(i)?.equals(TimeZone.getDefault().id) == true) {
//                binding.timeZoneSpinner?.setSelection(i)
//            }
//        }
//        cTimeZoneFinal = binding.timeZoneSpinner?.selectedItem.toString()
//
//        binding.timeZoneSpinner?.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
//                    binding.acuityRecyclerView.viewGone()
//                    binding.shimmerFrameLayout2.viewVisible()
//                    cTimeZoneFinal = binding.timeZoneSpinner?.selectedItem.toString()
//                    getAcuityDateAndTime()
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//                }
//
//            }
//    }

    private fun getTimeZone() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getTimeZone()
        } else {
            CustomToasts.showToast(
                requireActivity(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }

    private fun populateTimeZone() {
        val mAdapter = TimeZoneCustomSpinnerAdapter(requireContext(), timeZoneList)
        cTimeZoneFinal = TimeZone.getDefault().toString()
        binding.timeZoneSpinner?.adapter = mAdapter

        for (i in 0 until mAdapter.count) {
            if (mAdapter.getItem(i).toString().contains(TimeZone.getDefault().id)) {
                binding.timeZoneSpinner?.setSelection(i)
                cTimeZoneFinal = TimeZone.getDefault().id
            }
        }

        binding.timeZoneSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                    val data = timeZoneList[index]
                    cTimeZoneFinal = data.time_zone_value
                    binding.acuityRecyclerView.viewGone()
                    binding.accountDetailLayout.viewGone()
                    binding.shimmerFrameLayout2.viewVisible()
                    getAcuityDateAndTime()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

    private fun bookConsultation(
        token: String,
        type: String,
        isCardSave: Boolean,
        lastFourDigits: String,
        is_other_payment_gateway: Boolean,

        cardHolder: String,
        expiryDate: String,
    ) {
        val userId = mActivity.sharedPreference?.getUser()?.id
        if (isNetworkAvailable(requireContext())) {
            if (type == "GuestMode") {
                viewModel.bookConsultationForGuest(
                    userId.toString(),
                    cDate.toString(),
                    cTime.toString(),
                    cType.toString(),
                    cWith.toString(),
                    token,
                    300.00,
                    cDateTime.toString(),
                    cTypeId.toString(),
                    cCalenderId.toString(),
                    currentCountryCode + binding.etPhone?.unMaskedText.toString(),
                    email,
                    streetAddress,
                    city,
                    state,
                    zipCode,
                    countryNameCode,
                    binding.countryName?.selectedCountryNameCode.toString(),
                    description,
                    true,
                    cTimeZoneFinal,
                    mPassword,
                    confirm_password,
                    firstName,
                    lastName,
                    is_Login
                )
            } else {
                viewModel.bookConsultation(
                    userId.toString(),
                    cDate.toString(),
                    cTime.toString(),
                    cType.toString(),
                    cWith.toString(),
                    token,
                    300.00,
                    cDateTime.toString(),
                    cTypeId.toString(),
                    cCalenderId.toString(),
                    currentCountryCode + binding.etPhone?.unMaskedText.toString(),
                    email,
                    streetAddress,
                    city,
                    state,
                    zipCode,
                    countryNameCode,
                    description,
                    true,
                    cTimeZoneFinal,
                    isCardSave,
                    lastFourDigits,
                    is_other_payment_gateway,
                    cardHolder,
                    expiryDate
                )

            }
        } else {
            CustomToasts.showToast(
                requireActivity(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false
            )
        }
    }

    private fun paymentSuccessPopUp() {
        paymentSuccessPopUp =
            PaymentSuccessPopUp(object : PaymentSuccessPopUp.PaymentSuccessPopInterface {
                override fun onSuccessEmailUsInformationClick(Email: String) {
                }

            }, object : PaymentSuccessPopUp.DialogueDismiss {
                override fun onDialogueDismiss() {
//                    val user = SharePreferenceHelper.getInstance(requireContext()).getUser()
//                if (user.is_guest == 0) {
//                    paymentSuccessPopUp.dismiss()
//                }else{
//                    (requireContext() as HomeHost).guideTab()
//                    (requireContext() as HomeHost).navigateToGuide()
//                }
                    binding.shimmerFrameLayout.viewVisible()
                    getTimeZone()
                    Log.e("paymentSuccessPopUp", "OnnResume")
                    getCalenderApiCall()
                    paymentSuccessPopUp.dismiss()

                }

            })
        paymentSuccessPopUp.show(childFragmentManager, "successPaymentPopUp")
    }

    private fun getAndSetupAppointmentTypeDetails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cCurrentDateTime = LocalDateTime.now().toString()
        }
        cCurrentDateFinal = cCurrentDateTime!!.toDate().formatTo("yyyy-MM-dd")
        calenderList.forEach { item ->
            item.isSelected = false
        }
        calenderList.first().isSelected = true
        var item = calenderList.first()
        cWith = item.name
        cCalenderId = item.id.toString()

        cDate = ""
        cTime = ""
        binding.tvDate.text = getString(R.string.date)
        binding.tvTime.text = getString(R.string.time)

        binding.noDataView?.viewGone()
        binding.timeSlotLayout.viewGone()
        binding.timeslotTv.viewGone()
        binding.shimmerFrameLayout2.viewGone()
        binding.acuityRecyclerView.viewGone()
        binding.accountDetailLayout.viewGone()
//        binding.consultationTypeTv.viewVisible()
        binding.shimmerFrameLayout1.viewVisible()
        binding.consultationTypeRecyclerView.viewGone()
        typeList.clear()
        getAppointmentTypeApiCall()
    }

    private fun getAcuityDateAndTimeDetails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cCurrentDateTime = LocalDateTime.now().toString()
        }
        cCurrentDateFinal = cCurrentDateTime!!.toDate().formatTo("yyyy-MM-dd")
        typeList.forEach {
            it.isSelected = false
        }
        typeList.first().isSelected = true
        var item = typeList.first()
        cType = "1"
        cTypeId = item.id.toString()
        binding.noDataView?.viewGone()
        binding.acuityRecyclerView.viewGone()
        binding.timeSlotLayout.viewVisible()
        binding.timeslotTv.viewVisible()
        binding.accountDetailLayout.viewGone()
        binding.shimmerFrameLayout2.viewVisible()
        getAcuityDateAndTime()
    }
}