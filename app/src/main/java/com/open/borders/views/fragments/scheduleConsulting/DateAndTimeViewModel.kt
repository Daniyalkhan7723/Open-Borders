package com.open.borders.views.fragments.scheduleConsulting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.responseModel.*
import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.activities.baseActivity.SummeryModel
import com.open.borders.views.fragments.baseFragment.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class DateAndTimeViewModel constructor(
    private val sharedWebServices: SharedWebServices
) : BaseViewModel() {

    var getDatesLiveData: MutableLiveData<DateResponse> = MutableLiveData()
    var getTimeSlotLiveData: MutableLiveData<TimeSlotsResponse> = MutableLiveData()
    var sendEvaluationSummeryToEmailLiveData: MutableLiveData<SendEmailDataResponse> = MutableLiveData()
    var getTermsAndCoditionLiveData: MutableLiveData<TermsAndConditionResponse> = MutableLiveData()

    fun showHideProgressBar(show: Boolean) {
        _showHideProgressDialog.value = show.wrapWithEvent()
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


    fun getTimeSlots(
        date: String,
        appointmentTypeID: String,
        calendarID: String,
        timezone: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getTimeSlots(
                date,
                appointmentTypeID,
                calendarID,
                timezone
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getTimeSlotLiveData.value = it
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


    fun sendSummeryEvaluationData(
        data: String,
        is_english: Int
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.sendEvaluationDataToEmail(data, is_english).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                        sendEvaluationSummeryToEmailLiveData.value = it
                }
                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun getTermsAndCondition() {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getTermsAndCondition().run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    getTermsAndCoditionLiveData.value = it
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }

}