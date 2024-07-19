package com.open.borders.models.responseModel

import com.open.borders.models.utilsModels.BaseResponse

data class BugReportResponse(
    val data: BugReportModel
): BaseResponse()

data class BugReportModel(
    val created_at: String,
    val description: String,
    val id: Int,
    val node_url: String,
    val updated_at: String,
    val user_id: Int
)

