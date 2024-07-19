package com.open.borders.extensions

import android.util.Patterns
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*


fun isValidEmail(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}


fun String.parseCountryCode( ): String {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        // phone must begin with '+'
        val numberProto = phoneUtil.parse(this, "")
        numberProto.countryCode.toString()
    } catch (e: NumberParseException) {
        ""
    }
}

fun String.getCountryCode() =
    Locale.getISOCountries().find { Locale("", it.trim().lowercase()).displayCountry == this.trim().lowercase() }