package com.open.borders.models.responseModel

import com.open.borders.models.utilsModels.BaseResponse

data class AcuityResponse(
    val data: List<List<AcuityModel>>
): BaseResponse()

data class AcuityModel(
    val slotsAvailable: Int,
    val time: String,
    var isSelected: Boolean = false
)

