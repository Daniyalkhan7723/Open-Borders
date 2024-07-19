package com.open.borders.models.generalModel.questionsApiModel

import com.open.borders.models.generalModel.guideQuestionsModels.Data
import com.open.borders.models.generalModel.guideQuestionsModels.Position
import com.open.borders.models.generalModel.guideQuestionsModels.Style
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    @SerialName("animated")
    val animated: Boolean? = false,
    @SerialName("arrowHeadType")
    val arrowHeadType: String? = null,
    @SerialName("data")
    val data: Data? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("position")
    val position: Position? = null,
    @SerialName("source")
    val source: String? = null,
    @SerialName("sourceHandle")
    val sourceHandle: String? = null,
    @SerialName("style")
    val style: Style? = null,
    @SerialName("target")
    val target: String? = null,
    @SerialName("targetHandle")
    val targetHandle: String? = null,
    @SerialName("type")
    val type: String? = null
)