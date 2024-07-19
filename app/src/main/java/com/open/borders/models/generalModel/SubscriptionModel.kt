package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionModel(
    @SerialName("client_secret")
    val client_secret: String,
    @SerialName("plan_detail")
    val plan_detail: PlanDetail
)