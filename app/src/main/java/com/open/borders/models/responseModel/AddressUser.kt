package com.open.borders.models.responseModel

import com.google.gson.annotations.SerializedName

data class AddressUser(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("street_address") var streetAddress: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("zip_code") var zipCode: String? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("country_name_code") var countryNameCode: String? = null
)