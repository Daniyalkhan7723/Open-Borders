package com.open.borders.models.responseModel


import com.open.borders.models.generalModel.questionsApiModel.GetQuestionsModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionerResponse(
    @SerialName("data")
    val data: GetQuestionsModel? = GetQuestionsModel(),
    @SerialName("status")
    val status: Boolean? = false
)