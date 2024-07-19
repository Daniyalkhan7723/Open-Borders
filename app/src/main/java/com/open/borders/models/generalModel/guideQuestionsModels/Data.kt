package com.open.borders.models.generalModel.guideQuestionsModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("child")
    var child: MutableList<Child>? = null,
    @SerialName("conditions")
    var conditions: ArrayList<Condition>? = null,
    @SerialName("actionLink")
    var actionLink: ArrayList<ActionLinkModel>? = null,
    @SerialName("description")
    var description: String? = null,
    @SerialName("description_ES")
    var description_ES: String? = null,
    @SerialName("isConditional")
    var isConditional: Boolean? = false,
    @SerialName("label")
    var label: String? = null,
    @SerialName("label_ES")
    var label_ES: String? = null,
    @SerialName("parents")
    var parents: ArrayList<String>? = null,
    @SerialName("summary")
    var summary: String? = null,
    @SerialName("summaryDescription")
    var summaryDescription: String? = null,
    var isSelected: Boolean = false
)