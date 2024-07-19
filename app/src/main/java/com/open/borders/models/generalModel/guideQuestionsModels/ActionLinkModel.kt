package com.open.borders.models.generalModel.guideQuestionsModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionLinkModel(
    @SerialName("id")
    val id: String,
    @SerialName("link")
    val link: String,
    @SerialName("title")
    val title: String,
    @SerialName("title_ES")
    val title_ES: String
)