package com.open.borders.models.responseModel

class TimeZoneResponse : ArrayList<TimeZoneModelItem>()

data class TimeZoneModelItem(
    val time_zone: String,
    val time_zone_value: String,
)