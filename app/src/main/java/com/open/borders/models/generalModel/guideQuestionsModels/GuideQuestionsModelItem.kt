package com.open.borders.models.generalModel.guideQuestionsModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuideQuestionsModelItem(
    @SerialName("animated")
    var animated: Boolean? = null,
    @SerialName("arrowHeadType")
    var arrowHeadType: String? = null,
    @SerialName("data")
    var data: Data? = Data(),
    @SerialName("id")
    var id: String? = null,
    @SerialName("position")
    var position: Position? = null,
    @SerialName("source")
    var source: String? = null,
    @SerialName("sourceHandle")
    var sourceHandle: String? = null,
    @SerialName("style")
    var style: Style? = null,
    @SerialName("target")
    var target: String? = null,
    @SerialName("targetHandle")
    var targetHandle: String? = null,
    @SerialName("type")
    var type: String? = null,
)


