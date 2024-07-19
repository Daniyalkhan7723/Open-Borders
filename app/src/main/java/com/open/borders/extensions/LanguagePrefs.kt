package com.open.borders.extensions

import android.content.Context
import android.content.res.Configuration
import com.open.borders.utils.Constants
import java.util.*


fun Context.setSpanish(): Configuration  {
    val lang = "es"
    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    resources.updateConfiguration(
        config,
        resources.displayMetrics
    )
    return config
}


fun Context.setEnglish(): Configuration {
        val lang = "en_US"
    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    resources.updateConfiguration(
        config,
        resources.displayMetrics
    )
    return config
}

fun Context.getCurrentLanguage(): String {
    val current = resources.configuration.locale
return current.language
}
