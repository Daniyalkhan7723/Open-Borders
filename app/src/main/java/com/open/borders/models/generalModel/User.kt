package com.open.borders.models.generalModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("date_of_birth")
    val date_of_birth: String? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("status")
    val status: Int? = null,
    @SerialName("is_blocked")
    val is_blocked: Int? = null,
    @SerialName("created_at")
    val created_at: String? = null,
    @SerialName("email_verified_at")
    val email_verified_at: String? = null,
    @SerialName("first_name")
    val first_name: String? = null,
    @SerialName("last_name")
    val last_name: String? = null,
    @SerialName("phone_no")
    val phone_no: String? = null,
    @SerialName("profile_image")
    val profile_image: String? = null,
    @SerialName("updated_at")
    val updated_at: String? = null,
    @SerialName("is_guest")
    val is_guest: Int? = null,
    @SerialName("apple_pay_token")
    val apple_pay_token: String? = null,
    @SerialName("blance_amount")
    val blance_amount: Float? = null,
    @SerialName("card_token")
    val card_token: String? = null,
    @SerialName("google_pay_token")
    val google_pay_token: String? = null,
    @SerialName("is_complete_profile")
    val is_complete_profile: Int? = null,
    @SerialName("pm_last_four")
    val pm_last_four: String? = null,
    @SerialName("pm_type")
    val pm_type: String? = null,
    @SerialName("stripe_id")
    val stripe_id: String? = null,
    @SerialName("stripe_payment_method")
    val stripe_payment_method: String? = null,
    @SerialName("stripe_token")
    val stripe_token: String? = null,
    @SerialName("trial_ends_at")
    val trial_ends_at: String? = null,
    @SerialName("user_address")
    val user_address: UserAddress? = null,
    @SerialName("petitioner_detail")
    val petitioner_detail: PetitionerDetail? = null,

    @SerialName("card_holder_name ")
    val card_holder_name: String? = null,
    @SerialName("expire_date ")
    val expire_date: String? = null,
    )