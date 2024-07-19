package com.open.borders.views.fragments.accounts.accountsHome

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.generalModel.User
import com.open.borders.models.responseModel.LoginResponse
import com.open.borders.models.responseModel.LogoutResponse
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.fragments.baseFragment.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class AccountViewModel constructor(
    private val sharedWebServices: SharedWebServices
): BaseViewModel() {

    var logoutLiveData: MutableLiveData<LogoutResponse> = MutableLiveData()
    var deleteAccountLiveData: MutableLiveData<LogoutResponse> = MutableLiveData()

    fun logOutUser() {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.logOut().run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        logoutLiveData.value = it
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

    fun deleteAccount() {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.deleteAccount().run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        deleteAccountLiveData.value = it
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

}