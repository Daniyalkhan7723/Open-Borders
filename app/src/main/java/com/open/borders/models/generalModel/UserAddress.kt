package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAddress(
    @SerialName("city")
    val city: String? = null,
    @SerialName("country")
    val country: String? = null,
    @SerialName("created_at")
    val created_at: String? = null,
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("state")
    val state: String? = null,
    @SerialName("street_address")
    val street_address: String? = "",
    @SerialName("updated_at")
    val updated_at: String? = null,
    @SerialName("user_id")
    val user_id: Int? = null,
    @SerialName("zip_code")
    val zip_code: String? = null,
    @SerialName("country_name_code")
    val country_name_code: String? = null
)