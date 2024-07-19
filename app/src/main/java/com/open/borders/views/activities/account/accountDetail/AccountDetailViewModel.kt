package com.open.borders.views.activities.account.accountDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.generalModel.historyModels.HistoryResponse
import com.open.borders.models.responseModel.GetProfileResponse
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.activities.baseActivity.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import okhttp3.RequestBody

@InternalSerializationApi
class AccountDetailViewModel constructor(
    private val sharePreferenceHelper: SharePreferenceHelper,
    private val sharedWebServices: SharedWebServices
) : BaseViewModel() {

    var getProfileLiveData: MutableLiveData<GetProfileResponse> = MutableLiveData()
    var updateProfileLiveData: MutableLiveData<GetProfileResponse> = MutableLiveData()
    var getServicesHistoryLivaData: MutableLiveData<HistoryResponse> = MutableLiveData()

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

    fun updateProfile(
        first_name: RequestBody,
        last_name: RequestBody,
        email: RequestBody,
        phone_no: RequestBody,
        street_address: RequestBody,
        country: RequestBody,
        state: RequestBody,
        city: RequestBody,
        zip_code: RequestBody,
        country_name_code: RequestBody,
        firstNameUs: RequestBody,
        lastNameUs: RequestBody,
        emailUs: RequestBody
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.updateProfile(
                first_name,
                last_name,
                email,
                phone_no,
                street_address,
                country,
                state,
                city,
                zip_code,
                country_name_code,
                firstNameUs,
                lastNameUs,
                emailUs
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        updateProfileLiveData.value = it
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

    fun getServicesHistory() {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getServicesHistory().run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getServicesHistoryLivaData.value = it
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