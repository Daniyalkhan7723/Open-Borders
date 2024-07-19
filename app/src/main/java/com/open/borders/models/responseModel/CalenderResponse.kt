package com.open.borders.models.responseModel

import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
data class CalenderResponse(
    @SerialName("data")
    val data: List<CalenderModel>
) : BaseResponse()


@Serializable
data class CalenderModel(
    @SerialName("description")
    val description: String,
    @SerialName("email")
    val email: String,
    @SerialName("id")
    val id: Int,
    @SerialName("image")
    val image: String,
    @SerialName("location")
    val location: String,
    @SerialName("name")
    val name: String,
    @SerialName("replyTo")
    val replyTo: String,
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("timezone")
    val timezone: String,
    var isSelected: Boolean = false
)