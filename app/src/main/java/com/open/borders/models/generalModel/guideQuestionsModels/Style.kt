package com.open.borders.models.generalModel.guideQuestionsModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Style(
    @SerialName("background")
    val background: String? = null,
    @SerialName("border")
    val border: String? = null
)