package com.open.borders.models.utilsModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BaseResponse {
    @SerialName("status")
    var status: Boolean = false

    @SerialName("message")
    val message: String = ""
}