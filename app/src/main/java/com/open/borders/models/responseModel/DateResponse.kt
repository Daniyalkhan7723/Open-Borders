package com.open.borders.models.responseModel
import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class DateResponse(
    @SerialName("data")
    val data: List<DateModel>
): BaseResponse()


@Serializable
data class DateModel(
    @SerialName("date")
    val date: String
)