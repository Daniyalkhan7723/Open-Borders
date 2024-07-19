package com.open.borders.extensions

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

fun String.toDate(dateFormat: String = "yyyy-MM-dd", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat)
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun LocalDate.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun String.isToDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
    val format = SimpleDateFormat(format)
    return try {
        format.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.toFormat(format: String = "yyyy-MM-dd"): String {
    val format = SimpleDateFormat(format)
    return try {
        format.format(this)
    } catch (e: Exception) {
        ""
    }
}

fun Date.toTimeFormat(format: String = "hh:mm a"): String {
    val format = SimpleDateFormat(format)
    return try {
        format.format(this)
    } catch (e: Exception) {
        ""
    }
}


fun String.newToDate(dateFormat: String = "yyyy-MM-dd'T'hh:mm:ssZ", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat)
    parser.timeZone = timeZone
    return parser.parse(this)
}

@SuppressLint("SimpleDateFormat")
fun String.isToNewDate(format: String = "yyyy-MM-dd'T'hh:mm:ssZ"): Date? {
    val mFormat = SimpleDateFormat(format)
    return try {
        mFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

@SuppressLint("SimpleDateFormat")
fun String.isToCDate(format: String = "yyyy-MM-dd"): Date? {
    val mFormat = SimpleDateFormat(format)
    return try {
        mFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun String.sDate(dateFormat: String = "yyyy-MM-dd'T'hh:mm:ssZ", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.sFormat(format: String = "hh:mm a"): String {
    val format = SimpleDateFormat(format)
    return try {
        format.format(this)
    } catch (e: Exception) {
        ""
    }
}

fun Date.mFormat(format: String = "MMMM dd"): String {
    val format = SimpleDateFormat(format)
    return try {
        format.format(this)
    } catch (e: Exception) {
        ""
    }
}


fun utcToLocalTimeZone(inputFormat : String,outputFormat : String,dateToConvert : String): String {
    var dateToReturn = dateToConvert
    val sdf = SimpleDateFormat(inputFormat)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    var gmt: Date? = null
    val sdfOutputToSend =
        SimpleDateFormat(outputFormat)
    sdfOutputToSend.timeZone = TimeZone.getDefault()


    try {
        gmt = sdf.parse(dateToConvert)
        dateToReturn = sdfOutputToSend.format(gmt)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return dateToReturn
}


fun getTime(
    originalString: String?,
    givenFormat: String = "yyyy-MM-dd'T'HH:mm:ss+SSSS",
    format: String
): String {
    var str = ""
    try {
        val simpleDateFormat = SimpleDateFormat(givenFormat, Locale.US  )
        val date = simpleDateFormat.parse(originalString ?: "")
        str = date?.let { SimpleDateFormat(format, Locale.getDefault()).format(it) }!!
    } catch (e: ParseException) {
        return ""
    }
    return str
}

fun String.sadDate(dateFormat: String = "yyyy-MM-dd'T'hh:mm:ssZ", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}


fun Int.formatDecimalSeparator(): String {
    return toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

