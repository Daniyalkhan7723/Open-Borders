package com.open.borders.models.responseModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAuthTokenResponse(
    @SerialName("status")
    val status: Boolean? = false,
    @SerialName("token")
    val token: String? = null
)