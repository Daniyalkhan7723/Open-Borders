package com.open.borders.models.generalModel.paymentModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BillingAddress(
    @SerialName("address1")
    val address1: String,
    @SerialName("address2")
    val address2: String,
    @SerialName("address3")
    val address3: String,
    @SerialName("administrativeArea")
    val administrativeArea: String,
    @SerialName("countryCode")
    val countryCode: String,
    @SerialName("locality")
    val locality: String,
    @SerialName("name")
    val name: String,
    @SerialName("postalCode")
    val postalCode: String,
    @SerialName("sortingCode")
    val sortingCode: String
)