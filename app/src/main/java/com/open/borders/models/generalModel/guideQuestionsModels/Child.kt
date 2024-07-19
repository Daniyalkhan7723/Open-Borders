package com.open.borders.models.generalModel.guideQuestionsModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Child(
    @SerialName("id")
    val id: String? = null,
    @SerialName("summaryCatagory")
    val summaryCatagory: String? = null,
    @SerialName("summaryDescription")
    val summaryDescription: String? = null,
    @SerialName("summaryDescription_ES")
    val summaryDescription_ES: String? = null,
    @SerialName("targetNode")
    val targetNode: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("text_es")
    val text_ES: String? = null,
    var isSelected: Boolean = false
)