package com.open.borders.models.responseModel


import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.models.generalModel.QuestionStateModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionStateResponse(
    @SerialName("data")
    val data: QuestionStateModel? = null
) : BaseResponse()