package com.open.borders.models.generalModel.historyModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultationBookingDetail(
    @SerialName("amount")
    val amount: Int,
    @SerialName("consultation_id")
    val consultation_id: Int,
    @SerialName("consultation_time")
    val consultation_time: String,
    @SerialName("consultation_with")
    val consultation_with: String,
    @SerialName("created_at")
    val created_at: String,
    @SerialName("date")
    val date: String,
    @SerialName("id")
    val id: Int,
    @SerialName("status")
    val status: String,
    @SerialName("transaction_id")
    val transaction_id: String,
    @SerialName("updated_at")
    val updated_at: String,
    @SerialName("user_id")
    val user_id: Int
)