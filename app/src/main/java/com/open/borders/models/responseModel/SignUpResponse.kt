package com.open.borders.models.responseModel


import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.models.generalModel.SignUpModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    @SerialName("data")
    val data: SignUpModel? = SignUpModel()
): BaseResponse()