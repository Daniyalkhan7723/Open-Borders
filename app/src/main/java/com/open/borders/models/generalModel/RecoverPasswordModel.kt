package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecoverPasswordModel(
    @SerialName("access_token")
    val accessToken: String? = "",
    @SerialName("expires_at")
    val expiresAt: String? = "",
    @SerialName("token_type")
    val tokenType: String? = "",
    @SerialName("user")
    val user: User? = User()
)