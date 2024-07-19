package com.open.borders.models.responseModel


import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteQuestionResponse(
    @SerialName("data")
    val data : String? = null,
): BaseResponse()