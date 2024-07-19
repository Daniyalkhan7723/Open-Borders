package com.open.borders.models.generalModel.paymentModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethodData(
    @SerialName("description")
    val description: String,
    @SerialName("info")
    val info: Info,
    @SerialName("tokenizationData")
    val tokenizationData: TokenizationData,
    @SerialName("type")
    val type: String
)