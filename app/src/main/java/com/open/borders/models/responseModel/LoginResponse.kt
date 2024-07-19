package com.open.borders.models.responseModel


import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.models.generalModel.LoginModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("data")
    val data: LoginModel? = LoginModel()
): BaseResponse()