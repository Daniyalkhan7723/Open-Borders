package com.open.borders.models.generalModel.historyModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryModel(
    @SerialName("consultation_booking_detail")
    val consultation_booking_detail: List<ConsultationBookingDetail>,
    @SerialName("purches_plan_history")
    val purches_plan_history: List<PurchesPlanHistory>
)