package com.open.borders.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.open.borders.models.generalModel.User
import com.open.borders.models.generalModel.questionsApiModel.GetQuestionsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharePreferenceHelper private constructor(private val sharedPreferences: SharedPreferences) {

    private val dispatcher = Dispatchers.Default

    companion object {

        const val USER_SHARED_PREFERENCE = "open_border_preferences"
        private const val IS_LOGGED_IN = "SharedPreferenceHelper_is_logged_in"
        private const val IS_TOAST = "SharedPreferenceHelper_is_toast"
        private const val USER = "user"
         private const val Questions = "questions"
        private const val TOKEN = "token"
        private const val FIRST_TIME = "firstTime"
        private const val FILE_NAME = "fileName"
        private const val TAB_NAME = "tabName"
        private const val FIRST_TIME_TRUE = "false"
        private const val QUESTION_AUTH_TOKEN = "auth_toke"
        private const val LAST_QUESTION = "last_question"
        private const val TERMS = "terms"
        private const val TERMS_ES = "terms_es"

        private var INSTANCE: SharePreferenceHelper? = null

        fun getInstance(context: Context): SharePreferenceHelper {
            val sharedPreference =
                context.getSharedPreferences(USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharePreferenceHelper(sharedPreference).also { INSTANCE = it }
            }
        }
    }


    private suspend fun put(key: String, value: String) = withContext(dispatcher) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    private suspend fun put(key: String, value: Boolean) = withContext(dispatcher) {
        sharedPreferences.edit {
            putBoolean(key, value)
        }
    }

    suspend fun saveToken(token: String) {
        put(TOKEN, token)
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }

    suspend fun saveUserLogIn(isLogin: Boolean) {
        put(IS_LOGGED_IN, isLogin)
    }


    suspend fun checkToast(isToast: Boolean) {
        put(IS_TOAST, isToast)
    }

    fun isToast(): Boolean {
        return sharedPreferences.getBoolean(IS_TOAST, false)
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun saveUserData(user: User) {
        sharedPreferences.edit(commit = true) {
            putString(USER, Gson().toJson(user))
        }
    }


    fun saveQuestion(user: GetQuestionsModel) {
        sharedPreferences.edit(commit = true) {
            putString(Questions, Gson().toJson(user))
        }
    }

    fun getQuestion(): GetQuestionsModel {
        return Gson().fromJson(
            sharedPreferences.getString(Questions, ""),
            GetQuestionsModel::class.java
        ) ?: GetQuestionsModel()
    }

    fun getUser(): User {
        return Gson().fromJson(
            sharedPreferences.getString(USER, ""),
            User::class.java
        ) ?: User()
    }

    fun clearUserData() {
        sharedPreferences.edit(commit = true) {
            clear()
        }
    }

    suspend fun isFirstTime(isFirstTime: Boolean) {
        put(FIRST_TIME, isFirstTime)
    }

    fun isFirstTimeTrue(): Boolean {
        return sharedPreferences.getBoolean(FIRST_TIME, true)
    }

    suspend fun saveFileName(filename: String) {
        put(FILE_NAME, filename)
    }

    fun getFileName(): String? {
        return sharedPreferences.getString(FILE_NAME, null)
    }

    suspend fun saveTabType(tabType: String) {
        put(TAB_NAME, tabType)
    }

    fun getTabType(): String? {
        return sharedPreferences.getString(TAB_NAME, null)
    }

    suspend fun saveFirstTimeTrue(firstTime: String) {
        put(FIRST_TIME_TRUE, firstTime)
    }

    fun getFirstTimeTrue(): String? {
        return sharedPreferences.getString(FIRST_TIME_TRUE, null)
    }

    suspend fun saveQuestionAuthToken(authToken: String) {
        put(QUESTION_AUTH_TOKEN, authToken)
    }

    fun getQuestionAuthToken(): String? {
        return sharedPreferences.getString(QUESTION_AUTH_TOKEN, null)
    }

    suspend fun saveLastQuestion(lastQuestion: String) {
        put(LAST_QUESTION, lastQuestion)
    }

    fun getLastQuestion(): String? {
        return sharedPreferences.getString(LAST_QUESTION, null)
    }

    suspend fun saveTermsAndCondition(term: String) {
        put(TERMS, term)
    }

    fun getTermAndCondition(): String? {
        return sharedPreferences.getString(TERMS, null)
    }

    suspend fun saveTermsAndConditionEs(termEs: String) {
        put(TERMS_ES, termEs)
    }

    fun getTermAndConditionEs(): String? {
        return sharedPreferences.getString(TERMS_ES, null)
    }

}