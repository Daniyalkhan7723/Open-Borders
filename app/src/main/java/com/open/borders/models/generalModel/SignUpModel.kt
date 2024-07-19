package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpModel(
    @SerialName("access_token")
    val access_token: String? = null,
    @SerialName("token_type")
    val token_type: String? = null,
    @SerialName("user")
    val user: User? = null

)