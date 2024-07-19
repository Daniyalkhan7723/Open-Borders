package com.open.borders.models.responseModel
import com.open.borders.models.utilsModels.BaseResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class GetPackagesResponse(
    @SerialName("data")
    val data: List<GetPackagesModel>? = listOf()
): BaseResponse()



@Serializable
data class GetPackageDetailsResponse(
    @SerialName("data")
    val data: GetPackagesModel? = GetPackagesModel(),
): BaseResponse()


@Serializable
data class GetPackagesModel(
    @SerialName("amount")
    val amount: Int? = 0,
    @SerialName("created_at")
    val created_at: String? = "",
    @SerialName("description")
    val description: String? = "",
    @SerialName("description_es")
    val description_es: String? = "",
    @SerialName("duration")
    val duration: Int? = 0,
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("image")
    val image: String? = "",
    @SerialName("package_id")
    val package_id: Int? = null,
    @SerialName("plan_name")
    val plan_name: String? = "",
    @SerialName("plan_name_es")
    val plan_name_es: String? = "",
    @SerialName("plan_prices")
    val plan_prices: List<PlanPrice>? = null,
    @SerialName("plan_type")
    val plan_type: String? = "",
    @SerialName("plan_type_es")
    val plan_type_es: String? = "",
    @SerialName("recurring_period")
    val recurring_period: String? = "",
    @SerialName("recurring_period_es")
    val recurring_period_es: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("status")
    val status: Int? = 0,
    @SerialName("stripe_price_id")
    val stripe_price_id: String? = "",
    @SerialName("updated_at")
    val updated_at: String? = "",
    @SerialName("is_quantity_enable")
    val is_quantity_enable: Int? = null
): java.io.Serializable

@Serializable
data class PlanPrice(
    @SerialName("created_at")
    val created_at: String,
    @SerialName("id")
    val id: Int,
    @SerialName("plan_id")
    val plan_id: Int,
    @SerialName("price")
    val price: Int,
    @SerialName("stripe_price_id")
    val stripe_price_id: String,
    @SerialName("updated_at")
    val updated_at: String
): java.io.Serializable