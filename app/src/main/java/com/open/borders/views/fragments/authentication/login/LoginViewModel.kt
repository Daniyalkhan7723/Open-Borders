package com.open.borders.views.fragments.authentication.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.generalModel.User
import com.open.borders.models.responseModel.*
import com.open.borders.utils.Constants
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.fragments.baseFragment.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class LoginViewModel constructor(
    private val sharedWebServices: SharedWebServices,
    public val sharePreferenceHelper: SharePreferenceHelper
) : BaseViewModel() {

    var userLoginLiveData: MutableLiveData<LoginResponse> = MutableLiveData()
    var sendOTPCodeLiveData: MutableLiveData<SendOTPResponse> = MutableLiveData()
    var forgotPasswordLiveData: MutableLiveData<ForgetPasswordResponse> = MutableLiveData()
    var verifyOTPCodeLiveData: MutableLiveData<VerifyEmailResponse> = MutableLiveData()
    var recoverPasswordLiveData: MutableLiveData<RecoverPasswordResponse> = MutableLiveData()
    var guestLoginLiveData: MutableLiveData<GuestResponseModel> = MutableLiveData()

    fun loginUser(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.loginUser(
                email,
                password
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        userLoginLiveData.value = it
                        sharePreferenceHelper.saveUserData(it.data?.user ?: User())
//                        if (it.data?.user?.email_verified_at != null) {
//                            sharePreferenceHelper.saveUserLogIn(true)
//                        }
                        sharePreferenceHelper.saveUserLogIn(true)
                        sharePreferenceHelper.saveToken(it.data?.access_token.toString())
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

    fun guestLogin() {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.guestLogin().run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        guestLoginLiveData.value = it
                        sharePreferenceHelper.saveUserData(it.data?.user ?: User())
                        sharePreferenceHelper.saveToken(it.data?.access_token.toString())
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



    fun sendOTPCode(
        email: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.sendCode(
                email
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        sendOTPCodeLiveData.value = it

                    } else {
                        showSnackBarMessage(it.message)
                    }
                }
                onFailure {
                    Constants.SIGN_UP_FLAG = false
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun verifyCode(
        email: String,
        code: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.verifyCode(
                email, code
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        verifyOTPCodeLiveData.value = it

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

    fun forgetPassword(
        email: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.forgotPassword(
                email
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        forgotPasswordLiveData.value = it

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

    fun recoverPassword(
        email: String,
        password: String,
        password_confirmation: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.recoverPassword(
                email, password, password_confirmation
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        recoverPasswordLiveData.value = it

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