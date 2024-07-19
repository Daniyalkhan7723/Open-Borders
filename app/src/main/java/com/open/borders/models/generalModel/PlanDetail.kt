package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlanDetail(
    @SerialName("amount")
    val amount: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("description")
    val description: String,
    @SerialName("duration")
    val duration: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("image")
    val image: String,
    @SerialName("package_id")
    val packageId: Int,
    @SerialName("plan_name")
    val plan_name: String,
    @SerialName("plan_type")
    val planType: String,
    @SerialName("recurring_period")
    val recurringPeriod: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("status")
    val status: Int,
    @SerialName("stripe_price_id")
    val stripe_price_id: String,
    @SerialName("stripe_product_id")
    val stripe_product_id: String,
    @SerialName("updated_at")
    val updatedAt: String
)