package com.open.borders.models.responseModel

import com.google.gson.annotations.SerializedName
import com.open.borders.models.generalModel.UserAddress

data class PaymentDetailResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone_no") var phoneNo: String? = null,
    @SerializedName("profile_image") var profileImage: String? = null,
    @SerializedName("email_verified_at") var emailVerifiedAt: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("stripe_id") var stripeId: String? = null,
    @SerializedName("pm_type") var pmType: String? = null,
    @SerializedName("pm_last_four") var pmLastFour: String? = null,
    @SerializedName("trial_ends_at") var trialEndsAt: String? = null,
    @SerializedName("blance_amount") var blanceAmount: Int? = null,
    @SerializedName("is_complete_profile") var isCompleteProfile: Int? = null,
    @SerializedName("stripe_payment_method") var stripePaymentMethod: String? = null,
    @SerializedName("card_token") var cardToken: String? = null,
    @SerializedName("is_guest") var isGuest: Int? = null,
    @SerializedName("language") var language: Int? = null,
    @SerializedName("country_code") var countryCode: String? = null,
    @SerializedName("user_address") var userAddress: AddressUser? = AddressUser()
)