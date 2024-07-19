package com.open.borders.models.responseModel

import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.models.generalModel.GetProfileModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetProfileResponse(
    @SerialName("data")
    val data: GetProfileModel? = GetProfileModel()
): BaseResponse()