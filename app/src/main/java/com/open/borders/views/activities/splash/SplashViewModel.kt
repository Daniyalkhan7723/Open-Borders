package com.open.borders.views.activities.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.generalModel.User
import com.open.borders.models.responseModel.*
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.activities.baseActivity.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class SplashViewModel constructor(
    private val sharedWebServices: SharedWebServices,
    private val sharePreferenceHelper: SharePreferenceHelper
) : BaseViewModel() {

    var getQuestionStateLiveData: MutableLiveData<QuestionStateResponse> = MutableLiveData()
    var questionnaireDelete: MutableLiveData<SignUpResponse> = MutableLiveData()
    var guestLoginLiveData: MutableLiveData<GuestResponseModel> = MutableLiveData()
    var questionAuthToken: MutableLiveData<GetAuthTokenResponse> = MutableLiveData()
    var getQuestions: MutableLiveData<QuestionerIncryptedResponse> = MutableLiveData()
    var sendLanguage: MutableLiveData<SendEmailDataResponse> = MutableLiveData()


    fun getQuestionState(
        user_id: String
    ) {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getQuestionState(
                user_id
            ).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        getQuestionStateLiveData.value = it

                    } else {
                        showSnackBarMessage(it.message)
                        Log.e("getQuestionStateLiveData", "${it.message}")
                    }
                }

                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    it.printStackTrace()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }

    fun deleteQuestioner(
        user_id: String
    ) {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.questionnaireDelete(
                user_id
            ).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        questionnaireDelete.value = it

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

    fun guestLogin() {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.guestLogin().run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        guestLoginLiveData.value = it
                        sharePreferenceHelper.saveUserData(it.data?.user ?: User())
                        sharePreferenceHelper.saveToken(it.data?.access_token.toString())
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

    fun getQuestionAuthToken() {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getAuthToken().run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status == true) {
                        questionAuthToken.value = it
                        sharePreferenceHelper.saveQuestionAuthToken(it.token.toString())

                    }
                }

                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }

    fun getQuestions(
        token: String
    ) {

        viewModelScope.launch {
            Log.d("cddcdcdc", "3")
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getQuestions(token).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status == true) {
                        getQuestions.value = it

                    }
                }

                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }


    fun sendLanguage(
        language: Int
    ) {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.sendLanguage(language).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        sendLanguage.value = it

                    }
                }

                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }
}