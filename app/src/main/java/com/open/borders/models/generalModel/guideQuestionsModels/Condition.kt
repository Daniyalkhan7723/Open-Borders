package com.open.borders.models.generalModel.guideQuestionsModels


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    @SerialName("condition")
    val condition: String? = null,
    @SerialName("conditionDescription")
    var conditionDescription: String? = null,
    @SerialName("conditionDescription_ES")
    var conditionDescription_ES: String? = null,
    @SerialName("conditionLabel")
    var conditionLabel: String? = null,
    @SerialName("conditionOptions")
    var conditionOptions: MutableList<Child>? = ArrayList(),
    @SerialName("id")
    val id: String? = null,
    @SerialName("actionLink")
    var actionLink: ArrayList<ActionLinkModel>? = null,
)