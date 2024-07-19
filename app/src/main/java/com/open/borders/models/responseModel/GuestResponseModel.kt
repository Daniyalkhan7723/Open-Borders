package com.open.borders.models.responseModel
import com.open.borders.models.generalModel.User
import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class GuestResponseModel(
    @SerialName("data")
    val data: GuestModel
): BaseResponse()


@Serializable
data class GuestModel(
    @SerialName("access_token")
    val access_token: String,
    @SerialName("token_type")
    val token_type: String,
    @SerialName("user")
    val user: User
)