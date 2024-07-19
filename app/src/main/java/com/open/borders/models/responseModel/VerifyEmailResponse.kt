package com.open.borders.models.responseModel


import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.models.generalModel.VerifyEmailModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailResponse(
    @SerialName("data")
    val data: VerifyEmailModel? = VerifyEmailModel(),
): BaseResponse()