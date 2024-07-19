package com.open.borders.views.activities.account.paymentDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.responseModel.GetPaymentResponse
import com.open.borders.models.responseModel.GetProfileResponse
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.activities.baseActivity.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import okhttp3.RequestBody

@InternalSerializationApi
class PaymentDetailViewModel constructor(
    private val sharePreferenceHelper: SharePreferenceHelper,
    private val sharedWebServices: SharedWebServices
) : BaseViewModel() {
    var updatePaymentLiveData: MutableLiveData<GetPaymentResponse> = MutableLiveData()
    var addPaymentLiveData: MutableLiveData<GetPaymentResponse> = MutableLiveData()
    var getUserLiveData: MutableLiveData<GetPaymentResponse> = MutableLiveData()
    var getProfileLiveData: MutableLiveData<GetProfileResponse> = MutableLiveData()

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

    fun updatePaymentDetail(
        email: RequestBody, token: RequestBody, lastFour: RequestBody
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.updatePayment(email, token, lastFour).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status!!) {
                        updatePaymentLiveData.value = it
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

    fun addPaymentDetail(
        token: RequestBody,
        lastFour: RequestBody,
        cardHolder: RequestBody,
        expiryDate: RequestBody,
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.addPayment(token, lastFour, cardHolder, expiryDate).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status!!) {
                        addPaymentLiveData.value = it
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


}