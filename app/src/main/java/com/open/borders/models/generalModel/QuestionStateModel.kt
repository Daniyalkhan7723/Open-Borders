package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionStateModel(
    @SerialName("created_at")
    val created_at: String? = "",
    @SerialName("current_summary")
    val current_summary: String? = "",
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("last_question")
    val last_question: String? = "",
    @SerialName("prev_selections")
    val prev_selections: String? = "",
    @SerialName("questions_order")
    val questions_order: String? = "",
    @SerialName("status")
    val status: Int? = 0,
    @SerialName("updated_at")
    val updated_at: String? = "",
    @SerialName("user_id")
    val user_id: Int? = 0
): java.io.Serializable