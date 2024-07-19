package com.open.borders.models.responseModel
import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class AppointmentTypeResponse(
    @SerialName("data")
    val data: List<AppointmentTypeModel>
): BaseResponse()


@Serializable
data class AppointmentTypeModel(
    @SerialName("active")
    val active: Boolean,
    @SerialName("addonIDs")
    val addonIDs: List<Int>,
    @SerialName("calendarIDs")
    val calendarIDs: List<Int>,
    @SerialName("category")
    val category: String,
    @SerialName("classSize")
    val classSize: String,
    @SerialName("color")
    val color: String,
    @SerialName("description")
    val description: String,
    @SerialName("duration")
    val duration: Int,
    @SerialName("formIDs")
    val formIDs: List<Int>,
    @SerialName("id")
    val id: Int,
    @SerialName("image")
    val image: String,
    @SerialName("name")
    val name: String,
    @SerialName("paddingAfter")
    val paddingAfter: Int,
    @SerialName("paddingBefore")
    val paddingBefore: Int,
    @SerialName("price")
    val price: String,
    @SerialName("private")
    val private: Boolean,
    @SerialName("schedulingUrl")
    val schedulingUrl: String,
    @SerialName("type")
    val type: String,
    var isSelected: Boolean = false
)