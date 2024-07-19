package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionAddUpdateModel(
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("current_summary")
    val currentSummary: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("last_question")
    val lastQuestion: String? = null,
    @SerialName("prev_selections")
    val prevSelections: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("user_id")
    val userId: String? = null
)