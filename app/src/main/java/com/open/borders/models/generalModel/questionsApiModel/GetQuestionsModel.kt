package com.open.borders.models.generalModel.questionsApiModel


import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetQuestionsModel(
    @SerialName("createdAt")
    val createdAt: String? = "",
    @SerialName("_id")
    val id: String? = "",
    @SerialName("questions")
    val questions: List<GuideQuestionsModelItem>? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = "",
    @SerialName("updateIndicator")
    val updateIndicator: Boolean? = false,
    @SerialName("__v")
    val v: Int? = 0
)