package com.open.borders.models.generalModel.historyModels


import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryResponse(
    @SerialName("data")
    val data: HistoryModel
): BaseResponse()