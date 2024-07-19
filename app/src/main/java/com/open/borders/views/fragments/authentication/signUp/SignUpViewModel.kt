package com.open.borders.views.fragments.authentication.signUp

import androidx.lifecycle.MutableLiveData
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.models.responseModel.SignUpResponse
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.fragments.baseFragment.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import androidx.lifecycle.viewModelScope as viewModelScope1

@InternalSerializationApi
class SignUpViewModel constructor(
    private val sharedWebServices: SharedWebServices,
    private val sharePreferenceHelper: SharePreferenceHelper
) : BaseViewModel() {

    var userSignUpLiveData: MutableLiveData<SignUpResponse> = MutableLiveData()

    fun signUpUser(
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
        state: String,
        zip_code: String,
        country_name_code: String,
        firstNameUs: String,
        lastNameUs: String,
        emailUs: String
    ) {
        viewModelScope1.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.signUpUser(
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
                state,
                zip_code,
                country_name_code,
                firstNameUs,
                lastNameUs,
                emailUs
            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        userSignUpLiveData.value = it
                        it.data?.user?.let { it1 -> sharePreferenceHelper.saveUserData(it1) }
                        sharePreferenceHelper.saveUserLogIn(true)

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
        state: String,
        zip_code: String,
        country_name_code: String,
        firstNameUs: String,
        lastNameUs: String,
        emailUs: String
    ) {
        viewModelScope1.launch {
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
                state,
                zip_code,
                country_name_code,
                firstNameUs,
                lastNameUs,
                emailUs

            ).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.status) {
                        userSignUpLiveData.value = it
                        it.data?.user?.let { it1 -> sharePreferenceHelper.saveUserData(it1) }
                        sharePreferenceHelper.saveUserLogIn(true)

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