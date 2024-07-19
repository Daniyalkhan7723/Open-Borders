package com.open.borders.models.responseModel

import com.google.gson.annotations.SerializedName
import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.models.generalModel.GetProfileModel
import com.open.borders.models.generalModel.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GetPaymentResponse(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data") var data: PaymentDetailResponse? = PaymentDetailResponse()
)