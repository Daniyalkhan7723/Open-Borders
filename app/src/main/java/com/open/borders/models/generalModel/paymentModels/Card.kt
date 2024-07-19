package com.open.borders.models.generalModel.paymentModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    @SerialName("address_city")
    val addressCity: String,
    @SerialName("address_country")
    val addressCountry: String,
    @SerialName("address_line1")
    val addressLine1: String,
    @SerialName("address_line1_check")
    val addressLine1Check: String,
    @SerialName("address_line2")
    val addressLine2: String,
    @SerialName("address_state")
    val addressState: String,
    @SerialName("address_zip")
    val addressZip: String,
    @SerialName("address_zip_check")
    val addressZipCheck: String,
    @SerialName("brand")
    val brand: String,
    @SerialName("country")
    val country: String,
    @SerialName("cvc_check")
    val cvcCheck: String,
    @SerialName("dynamic_last4")
    val dynamicLast4: String,
    @SerialName("exp_month")
    val expMonth: Int,
    @SerialName("exp_year")
    val expYear: Int,
    @SerialName("funding")
    val funding: String,
    @SerialName("id")
    val id: String,
    @SerialName("last4")
    val last4: String,
    @SerialName("metadata")
    val metadata: Metadata,
    @SerialName("name")
    val name: String,
    @SerialName("object")
    val objectX: String,
    @SerialName("tokenization_method")
    val tokenizationMethod: String
)