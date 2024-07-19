package com.open.borders.models.generalModel.paymentModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenizationData(
    @SerialName("token")
    val token: String,
    @SerialName("type")
    val type: String
)