package com.open.borders.models.generalModel
import kotlinx.serialization.Serializable

@Serializable
data class PetitionerDetail(
    val created_at: String,
    val email: String,
    val first_name: String,
    val id: Int,
    val is_us_citizen: Int,
    val last_name: String,
    val updated_at: String,
    val user_id: Int
)