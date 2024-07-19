package com.open.borders.models.generalModel.paymentModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentInformation(
    @SerialName("apiVersion")
    val apiVersion: Int,
    @SerialName("apiVersionMinor")
    val apiVersionMinor: Int,
    @SerialName("paymentMethodData")
    val paymentMethodData: PaymentMethodData,
    @SerialName("shippingAddress")
    val shippingAddress: ShippingAddress
)