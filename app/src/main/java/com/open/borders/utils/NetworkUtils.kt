package com.open.borders.utils

import com.matrimony.sme.utils.CustomToasts
import com.open.borders.views.activities.navigationHost.HomeHost
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException

suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        val errorMessage = when (e) {
            is HttpException -> {
                val body = e.response()?.errorBody()
                getErrorMessage(body)
            }
            else -> {
                Constants.WENT_WRONG_MESSAGE
            }
        }
        Result.failure(Throwable(errorMessage, null))
    }
}

fun getErrorMessage(responseBody: ResponseBody?): String {
    return try {
        val jsonObject = JSONObject(responseBody!!.string())
        when {
            jsonObject.has("message") -> jsonObject.getString("message")
            else -> Constants.WENT_WRONG_MESSAGE
        }
    } catch (e: Exception) {
        e.toString()
    }
}

open class Event<out T>(private val content: T) {
    var consumed = false
        private set // Allow external read but not write

    /**
     * Consumes the content if it's not been consumed yet.
     * @return The unconsumed content or `null` if it was consumed already.
     */
    fun consume(): T? {
        return if (consumed) {
            null
        } else {
            consumed = true
            content
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (content != other.content) return false
        if (consumed != other.consumed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + consumed.hashCode()
        return result
    }
}