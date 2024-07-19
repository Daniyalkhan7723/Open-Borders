package com.open.borders.models.generalModel.guideQuestionsModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Position(
    @SerialName("x")
    val x: Double? = null,
    @SerialName("y")
    val y: Double? = null
)