package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginModel(
    @SerialName("access_token")
    val access_token: String? = null,
    @SerialName("expires_at")
    val expiresAt: String? = null,
    @SerialName("token_type")
    val tokenType: String? = null,
    @SerialName("user")
    val user: User? = User()
)