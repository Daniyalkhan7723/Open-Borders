package com.open.borders.models.responseModel

import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class ConsultationResponse(
    @SerialName("data")
    val data: ConsultationModel? = ConsultationModel()
): BaseResponse()


@Serializable
data class ConsultationModel(
    @SerialName("consultation")
    val consultation: Consultation? = Consultation(),
    @SerialName("consultation_id")
    val consultation_id: String? = "",
    @SerialName("consultation_time")
    val consultation_time: String? = "",
    @SerialName("consultation_with")
    val consultation_with: String? = "",
    @SerialName("created_at")
    val created_at: String? = "",
    @SerialName("date")
    val date: String? = "",
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("updated_at")
    val updated_at: String? = "",
    @SerialName("user_id")
    val user_id: String? = "",
    @SerialName("transaction_id")
    val transaction_id: String? = ""
)

@Serializable
data class Consultation(
    @SerialName("created_at")
    val createdAt: String? = "",
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("price")
    val price: Int? = 0,
    @SerialName("status")
    val status: String? = "",
    @SerialName("type")
    val type: String? = "",
    @SerialName("updated_at")
    val updatedAt: String? = ""
)