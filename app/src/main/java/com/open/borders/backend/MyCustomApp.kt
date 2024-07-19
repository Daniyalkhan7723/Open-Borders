package com.open.borders.backend

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.open.borders.koinDIModules.*
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@InternalSerializationApi
class MyCustomApp : MultiDexApplication() {

    private val localeAppDelegate = LocaleHelperApplicationDelegate()

    companion object{
        public lateinit var appContext:Context
    }

    override fun onCreate() {
        super.onCreate()

        appContext=this
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyCustomApp)
            modules(
                listOf(
                    retrofitModule,
                    sharedPrefModule,
                    utilModule,
                    ActivityResultViewModelModule,
                    DatesAndTimeViewModelModule
                )
            )
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAppDelegate.onConfigurationChanged(this)
        Log.e("onConfiguration", "")
    }

    override fun getApplicationContext(): Context =
        LocaleHelper.onAttach(super.getApplicationContext())
}