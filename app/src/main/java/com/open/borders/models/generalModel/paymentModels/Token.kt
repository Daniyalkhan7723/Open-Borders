package com.open.borders.models.generalModel.paymentModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerialName("card")
    val card: Card,
    @SerialName("client_ip")
    val clientIp: String,
    @SerialName("created")
    val created: Int,
    @SerialName("id")
    val id: String,
    @SerialName("livemode")
    val livemode: Boolean,
    @SerialName("object")
    val objectX: String,
    @SerialName("type")
    val type: String,
    @SerialName("used")
    val used: Boolean
)