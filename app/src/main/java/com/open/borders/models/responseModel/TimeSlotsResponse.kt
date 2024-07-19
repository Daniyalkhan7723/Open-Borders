package com.open.borders.models.responseModel
import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class TimeSlotsResponse(
    @SerialName("data")
    val data: List<TimeSlotModel>
): BaseResponse()


@Serializable
data class TimeSlotModel(
    @SerialName("slotsAvailable")
    val slotsAvailable: Int,
    @SerialName("time")
    val time: String,
    var isSelected: Boolean = false
)