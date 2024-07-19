package com.open.borders.models.generalModel.historyModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurchesPlanHistory(
    @SerialName("created_at")
    val created_at: String,
    @SerialName("id")
    val id: Int,
    @SerialName("plan")
    val plan: Plan,
    @SerialName("plan_id")
    val plan_id: String,
    @SerialName("stripe_ended_at")
    val stripe_ended_at: String,
    @SerialName("stripe_start_at")
    val stripe_start_at: String,
    @SerialName("total_amount")
    val total_amount: String
): java.io.Serializable