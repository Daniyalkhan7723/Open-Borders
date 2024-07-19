package com.open.borders.views.fragments.scheduleConsulting

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.backend.MyCustomApp.Companion.appContext
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.responseModel.*
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.fragments.baseFragment.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import okhttp3.RequestBody

@InternalSerializationApi
class ScheduleConsultingViewModel constructor(
    private val sharedWebServices: SharedWebServices,
    private val sharePreferenceHelper: SharePreferenceHelper
) : BaseViewModel() {
    var bookConsultationList: MutableLiveData<ConsultationResponse> = MutableLiveData()
    var bookConsultationListForGuestModeForCreateAccount: MutableLiveData<SignUpResponse> =
        MutableLiveData()
    var bookConsultationListForGuestMode: MutableLiveData<ConsultationResponse> = MutableLiveData()
    var getPackagesList: MutableLiveData<GetPackagesResponse> = MutableLiveData()
    var subscriptionLivaData: MutableLiveData<SubscriptionResponse> = MutableLiveData()
    var subscriptionForGuestLivaData: MutableLiveData<SubscriptionResponse> = MutableLiveData()
    var subscriptionForGuestLivaDataForSignUp: MutableLiveData<SignUpResponse> = MutableLiveData()
    var getDatesLiveData: MutableLiveData<DateResponse> = MutableLiveData()
    var getAcuityDateAndTimeLiveDate: MutableLiveData<AcuityResponse> = MutableLiveData()
    var getCalenderLiveData: MutableLiveData<CalenderResponse> = MutableLiveData()
    var getAppointmentTypeLiveData: MutableLiveData<AppointmentTypeResponse> = MutableLiveData()
    var getPackageFilterLiveData: MutableLiveData<PackageFilterResponse> = MutableLiveData()
    var getTimeZoneList: MutableLiveData<TimeZoneResponse> = MutableLiveData()
    var getUserLiveData: MutableLiveData<GetPaymentResponse> = MutableLiveData()
    var getProfileLiveData: MutableLiveData<GetProfileResponse> = MutableLiveData()


    fun bookConsultation(
        user_id: String,
        date: String,
        consultation_time: String,
        consultation_id: String,
        consultation_with: String,
        stripe_token: String,
        amount: Double,
        datetime: String,
        appointmentTypeID: String,
        calendarID: String,
        phoneNo: String,
        email: String,
        street_address: String,
        city: String,
        state: String,
        zip_code: String,
        country: String,
        description: String,
        term_and_condition: Boolean,
        timezone: String,
        isCardSave: Boolean,
        lastFourDigits: String,
        is_other_payment_gateway: Boolean,
        cardHolder: String,
        expiryDate: String,

        ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.booConsultation(
                user_id,
                date,
                consultation_time,
                consultation_id,
                consultation_with,
                stripe_token,
                amount,
                datetime,
                appointmentTypeID,
                calendarID,
                phoneNo,
                email,
                street_address,
                city,
                state,
                zip_code,
                country,
                description,
                term_and_condition,
                timezone,
                isCardSave,
                lastFourDigits,
                is_other_payment_gateway,
                cardHolder,
                expiryDate
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        bookConsultationList.value = it
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }

    fun bookConsultationForGuest(
        user_id: String,
        date: String,
        consultation_time: String,
        consultation_id: String,
        consultation_with: String,
        stripe_token: String,
        amount: Double,
        datetime: String,
        appointmentTypeID: String,
        calendarID: String,
        phoneNo: String,
        email: String,
        street_address: String,
        city: String,
        state: String,
        zip_code: String,
        country: String,
        countryCode: String,
        description: String,
        term_and_condition: Boolean,
        timezone: String,
        mPassword: String,
        confirm_password: String,
        firstName: String,
        lastName: String,
        isLogin: Int
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            if (isLogin == 1) {
                sharedWebServices.bookConsultationForGuestModeForCreateAccount(
                    user_id,
                    date,
                    consultation_time,
                    consultation_id,
                    consultation_with,
                    stripe_token,
                    amount,
                    datetime,
                    appointmentTypeID,
                    calendarID,
                    phoneNo,
                    email,
                    street_address,
                    city,
                    state,
                    zip_code,
                    country,
                    countryCode,
                    description,
                    term_and_condition,
                    timezone,
                    mPassword,
                    confirm_password,
                    firstName,
                    lastName,
                    isLogin
                ).run {
                    onSuccess {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        if (it.status) {
                            bookConsultationListForGuestModeForCreateAccount.value = it
                            it.data?.user?.let { it1 -> sharePreferenceHelper.saveUserData(it1) }
                            sharePreferenceHelper.saveToken(it.data?.access_token.toString())
                            sharePreferenceHelper.saveUserLogIn(true)
                        } else {
                            Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()
//                            showSnackBarMessage(it.message)
                        }
                    }
                    onFailure {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        showSnackBarMessage(it.message!!)
                    }
                }
            } else {
                sharedWebServices.bookConsultationForGuestMode(
                    user_id,
                    date,
                    consultation_time,
                    consultation_id,
                    consultation_with,
                    stripe_token,
                    amount,
                    datetime,
                    appointmentTypeID,
                    calendarID,
                    phoneNo,
                    email,
                    street_address,
                    city,
                    state,
                    zip_code,
                    country,
                    countryCode,
                    description,
                    term_and_condition,
                    timezone,
                    mPassword,
                    confirm_password,
                    firstName,
                    lastName,
                    isLogin

                ).run {
                    onSuccess {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        if (it.status) {
                            bookConsultationListForGuestMode.value = it
                        } else {
                            Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()

//                            showSnackBarMessage(it.message)
                        }
                    }
                    onFailure {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        showSnackBarMessage(it.message!!)
                    }
                }
            }
        }
    }

    fun getPackages(
        package_id: String
    ) {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getPackages(
                package_id
            ).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getPackagesList.value = it
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun subscription(
        stripe_payment_method: String,
        plan_id: String,
        price_id: String,
        quantity: Int,
        isCardSave: Boolean,
        lastFourDigits: String,
        is_other_payment_gateway: Boolean,

        cardHolder: String,
        expiryDate: String,

        ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.subscription(
                stripe_payment_method,
                plan_id,
                price_id,
                quantity,
                isCardSave,
                lastFourDigits,
                is_other_payment_gateway,

                cardHolder,
                expiryDate
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        subscriptionLivaData.value = it
                    } else {
                        subscriptionLivaData.value = it
                        showSnackBarMessage(it.message)
                    }
                }

                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }

    fun subscriptionForGuestMode(
        uId: String,
        fName: String,
        lName: String,
        email: String,
        phoneNumber: String,

        street_address: String,
        country: String,
        city: String,
        state: String,
        zip_code: String,

        countryNameCode: String,

        Usfname: String,
        UsLastName: String,
        UsEmail: String,
        password: String,
        confirmPassword: String,
        is_Login: Int,
        stripe_payment_method: String,
        plan_id: String,
        price_id: String,
        quantity: Int,
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()

            if (is_Login == 1) {
                sharedWebServices.subscriptionForGuestModeForSignUp(
                    uId,
                    fName,
                    lName,
                    email,
                    phoneNumber,
                    street_address,
                    country,
                    city,
                    state,
                    zip_code,
                    countryNameCode,
                    Usfname,
                    UsLastName,
                    UsEmail,
                    password,
                    confirmPassword,
                    is_Login,
                    stripe_payment_method,
                    plan_id,
                    price_id,
                    quantity
                ).run {
                    onSuccess {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        if (it.status) {
                            subscriptionForGuestLivaDataForSignUp.value = it
                            it.data?.user?.let { it1 -> sharePreferenceHelper.saveUserData(it1) }
                            sharePreferenceHelper.saveToken(it.data?.access_token.toString())
                            sharePreferenceHelper.saveUserLogIn(true)

                        } else {
                            Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()
//                            showSnackBarMessage(it.message)
                        }
                    }
                    onFailure {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()
//                        showSnackBarMessage(it.message!!)
                    }
                }
            } else {
                sharedWebServices.subscriptionForGuestMode(
                    uId,
                    fName,
                    lName,
                    email,
                    phoneNumber,
                    street_address,
                    country,
                    city,
                    state,
                    zip_code,
                    countryNameCode,
                    Usfname,
                    UsLastName,
                    UsEmail,
                    password,
                    confirmPassword,
                    is_Login,
                    stripe_payment_method,
                    plan_id,
                    price_id,
                    quantity
                ).run {
                    onSuccess {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        if (it.status) {
                            subscriptionForGuestLivaData.value = it
                        } else {
                            Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()

//                            showSnackBarMessage(it.message)
                        }
                    }
                    onFailure {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()

//                        showSnackBarMessage(it.message!!)
                    }
                }
            }

        }
    }


    fun getDates(
        month: String,
        appointmentTypeID: String,
        calendarID: String,
        timezone: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getDates(
                month,
                appointmentTypeID,
                calendarID,
                timezone
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getDatesLiveData.value = it
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun getCalender() {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getCalender(
            ).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getCalenderLiveData.value = it
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun getAppointmentType() {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getAppointmentType(
            ).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getAppointmentTypeLiveData.value = it
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }

    //GetAcuityDateAndTime
    fun getAcuityDateAndTime(
        month: String,
        appointmentTypeID: String,
        calendarID: String,
        timezone: String,
        is_previous: Int
    ) {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getAcuityTimeDate(
                month,
                appointmentTypeID,
                calendarID,
                timezone,
                is_previous
            ).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getAcuityDateAndTimeLiveDate.value = it
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun getPackagesFilter() {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getPackageFilter().run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getPackageFilterLiveData.value = it
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun getTimeZone() {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getTimeZone().run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    getTimeZoneList.value = it
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }

    fun updateGuestUserSignUp(
        user_id: String,
        first_name: String,
        last_name: String,
        email: String,
        password: String,
        password_confirmation: String,
        phone_no: String,
        profile_image: String,
        street_address: String,
        country: String,
        city: String,
        zip_code: String,
        country_name_code: String,
        firstNameUs: String,
        lastNameUs: String,
        emailUs: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.updateGuestUserSignUp(
                user_id,
                first_name,
                last_name,
                email,
                password,
                password_confirmation,
                phone_no,
                profile_image,
                street_address,
                country,
                city,
                "",
                zip_code,
                country_name_code,
                firstNameUs,
                lastNameUs,
                emailUs

            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {


                    } else {
                        showSnackBarMessage(it.message)
                    }
                }

                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun getProfile() {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getProfile().run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getProfileLiveData.value = it
                        it.data?.user?.let { it1 -> sharePreferenceHelper.saveUserData(it1) }
                    } else {
                        showSnackBarMessage(it.message)
                    }
                }

                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    it.message?.let { it1 -> showSnackBarMessage(it1) }
                }
            }
        }
    }


    fun getUser(email: RequestBody) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getUser(email).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status!!) {
                        getUserLiveData.value = it
                    } else {
                        showSnackBarMessage(it.message!!)
                    }
                }
                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    it.message?.let { it1 -> showSnackBarMessage(it1) }
                }
            }
        }
    }


}