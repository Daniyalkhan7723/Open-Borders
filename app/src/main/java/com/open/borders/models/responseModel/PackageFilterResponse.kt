package com.open.borders.models.responseModel

import com.open.borders.models.utilsModels.BaseResponse

data class PackageFilterResponse(
    val data: List<PackageFilterModel>
): BaseResponse()

data class PackageFilterModel(
    val created_at: String,
    val description: String,
    val id: Int,
    val image: String,
    val name: String,
    val status: String,
    val updated_at: String
)