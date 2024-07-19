package com.open.borders.models.responseModel

import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.views.activities.baseActivity.SummeryModel

data class SendEmailResponse(
    val immigrationHistory: ArrayList<SummeryModel>,
    val factorsOptions: ArrayList<SummeryModel>,
    val inadmissibilty: ArrayList<SummeryModel>
)