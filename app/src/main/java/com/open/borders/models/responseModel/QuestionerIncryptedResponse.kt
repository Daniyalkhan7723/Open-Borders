package com.open.borders.models.responseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionerIncryptedResponse(
    @SerialName("status")
    val status: Boolean? = false,
    @SerialName("data")
    val data: String? = String()
)
