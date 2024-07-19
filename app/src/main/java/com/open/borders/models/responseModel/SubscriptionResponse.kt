package com.open.borders.models.responseModel


import com.open.borders.models.generalModel.SubscriptionModel
import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionResponse(
    @SerialName("data")
    val data: SubscriptionModel
): BaseResponse()