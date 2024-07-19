package com.open.borders.views.fragments.guide.guideQuestions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.responseModel.*
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.activities.baseActivity.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class GuideQuestionsViewModel constructor(
    private val sharedWebServices: SharedWebServices,
    private val sharePreferenceHelper: SharePreferenceHelper
) : BaseViewModel() {

    var questionAddOrUpdateLiveData: MutableLiveData<QuestionAddUpdateResponse> = MutableLiveData()
    var questionnaireDelete: MutableLiveData<SignUpResponse> = MutableLiveData()
    var questionAuthToken: MutableLiveData<GetAuthTokenResponse> = MutableLiveData()
//    var getQuestions: MutableLiveData<QuestionerResponse> = MutableLiveData()
    var getQuestions: MutableLiveData<QuestionerIncryptedResponse> = MutableLiveData()
    var planDetails: MutableLiveData<GetPackageDetailsResponse> = MutableLiveData()
    var getProfileLiveData: MutableLiveData<GetProfileResponse> = MutableLiveData()
    var getQuestionStateLiveData: MutableLiveData<QuestionStateResponse> = MutableLiveData()
    var bugReportLiveData: MutableLiveData<BugReportResponse> = MutableLiveData()


    fun questionUpdateOrAdd(
        user_id: String,
        prev_selections: String,
        last_question: String,
        questions_order: String,
        current_summary: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.questionAddUpdate(
                user_id,
                prev_selections,
                last_question,
                current_summary, questions_order
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        questionAddOrUpdateLiveData.value = it
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

    fun getPlanDetails(
        plan_id: String
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.planDetails(
                plan_id
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        planDetails.value = it
                        Log.e("Plan", "${planDetails?.value?.data?.description}")
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

    fun getQuestions(token: String) {
        viewModelScope.launch {
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

    fun getQuestionState(user_id: String) {
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

    fun bugReport(
        node_url : String,
        description: String,
        qLabel: String,
    ) {
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.bugReport(
                node_url,
                description,
                qLabel
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        bugReportLiveData.value = it

                    } else {
                        showSnackBarMessage(it.message)
                        Log.e("getQuestionStateLiveData", "${it.message}")
                    }
                }

                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    it.printStackTrace()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }
}