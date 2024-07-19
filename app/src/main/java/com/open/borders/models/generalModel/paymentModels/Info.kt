package com.open.borders.models.generalModel.paymentModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Info(
    @SerialName("billingAddress")
    val billingAddress: BillingAddress,
    @SerialName("cardDetails")
    val cardDetails: String,
    @SerialName("cardNetwork")
    val cardNetwork: String
)