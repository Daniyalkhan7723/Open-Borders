package com.open.borders.koinDIModules

import com.open.borders.utils.SharedWebServices
import com.open.borders.backend.MyCustomApp
import com.open.borders.utils.MaterialDialogHelper
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

@InternalSerializationApi
val utilModule = module {

    single { MaterialDialogHelper() }
    single { SharedWebServices(get(), androidApplication() as MyCustomApp) }


}