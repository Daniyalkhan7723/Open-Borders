package com.open.borders.koinDIModules

import android.content.Context
//import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.open.borders.backend.ApiService
import com.open.borders.backend.MyCustomApp.Companion.appContext
import kotlinx.serialization.InternalSerializationApi
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

//private const val BASE_URL = "https://cpdev.codingpixel.com/open-border-web/api/"
//private const val BASE_URL = "http://178.128.29.7/open-border-web/api/"
private const val BASE_URL = "https://admin.openborders.io/api/"
//not
//private const val BASE_URL = "https://open-borders-dev.herokuapp.com/api/"

private const val CACHE_FILE_SIZE: Long = 50 * 1000 * 1000 // 50 Mib

val retrofitModule = module {

    single { cacheFile(get()) }

    single { cache(get()) }

    single<Call.Factory> { okHttp(get()) }

    single { retrofit(get() /*, getProperty("server_url")*/) }

    single { get<Retrofit>().create(ApiService::class.java) }
}

private val interceptor: Interceptor
    get() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

private fun cacheFile(context: Context) = File(context.filesDir, "border_crossing_cache").also {
    if (!it.exists())
        it.mkdirs()
}


private fun retrofit(callFactory: Call.Factory) = Retrofit.Builder()
    .callFactory(callFactory)
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun cache(cacheFile: File) = Cache(cacheFile, CACHE_FILE_SIZE)

@OptIn(InternalSerializationApi::class)
private fun okHttp(cache: Cache): OkHttpClient {
    val tlsSpecs = listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT)
    return OkHttpClient.Builder()
//        .addInterceptor(ChuckerInterceptor(appContext))
        .addInterceptor(interceptor)
        .connectionSpecs(tlsSpecs)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .cache(cache)
        .build()
}