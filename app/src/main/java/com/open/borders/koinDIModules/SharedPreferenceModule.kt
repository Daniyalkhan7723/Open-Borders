package com.open.borders.koinDIModules


import com.open.borders.utils.SharePreferenceHelper
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

@InternalSerializationApi
val sharedPrefModule = module {
    single {
        SharePreferenceHelper.getInstance(androidApplication().applicationContext)
    }
}