package com.open.borders.models.responseModel


import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.models.generalModel.QuestionAddUpdateModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionAddUpdateResponse(
    @SerialName("data")
    val data: QuestionAddUpdateModel? = QuestionAddUpdateModel()
): BaseResponse()