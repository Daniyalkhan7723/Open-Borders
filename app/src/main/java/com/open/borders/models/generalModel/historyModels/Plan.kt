package com.open.borders.models.generalModel.historyModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    @SerialName("amount")
    val amount: Int,
    @SerialName("created_at")
    val created_at: String,
    @SerialName("description")
    val description: String,
    @SerialName("description_es")
    val description_es: String,
    @SerialName("duration")
    val duration: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("image")
    val image: String,
    @SerialName("package_id")
    val package_id: Int,
    @SerialName("plan_name")
    val plan_name: String,
    @SerialName("plan_name_es")
    val plan_name_es: String,
    @SerialName("plan_type")
    val plan_type: String,
    @SerialName("plan_type_es")
    val plan_type_es: String,
    @SerialName("recurring_period")
    val recurring_period: String,
    @SerialName("recurring_period_es")
    val recurring_period_es: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("status")
    val status: Int,
    @SerialName("stripe_price_id")
    val stripe_price_id: String,
    @SerialName("stripe_product_id")
    val stripe_product_id: String,
    @SerialName("updated_at")
    val updated_at: String
): java.io.Serializable