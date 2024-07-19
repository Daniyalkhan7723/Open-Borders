package com.open.borders.utils

import com.google.gson.JsonObject
import com.open.borders.backend.ApiService
import com.open.borders.backend.MyCustomApp
import com.open.borders.views.activities.baseActivity.SummeryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import okhttp3.MultipartBody
import okhttp3.RequestBody

@InternalSerializationApi
class SharedWebServices(
    private val apiServices: ApiService, private val app: MyCustomApp
) {
    private val dispatcher = Dispatchers.IO

    //    private val BASE_URL = "https://opb-server-dev-temp.herokuapp.com/api/"
//    private val BASE_URL = "https://openborder.codingpixels.us/api/"
    private val BASE_URL = "https://openborders.io/api/"

    private val NEWS_URL = "https://www.bordercrossinglaw.com/wp-json/wp/v2/posts?"
    private val TERMS_CONDITION_URL =
        "https://www.bordercrossinglaw.com/openborder/wp-json/wp/v2/pages/712"

    //User Registration call
    suspend fun signUpUser(
        first_name: String,
        last_name: String,
        email: String,
        password: String,
        password_confirmation: String,
        phone_no: String,
        profile_image: String,
        street_address: String,
        country: String,
        city: String,
        state: String,
        zip_code: String,
        country_name_code: String,
        firstNameUs: String,
        lastNameUs: String,
        emailUs: String
    ) = withContext(dispatcher) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("first_name", first_name)
        jsonObject.addProperty("last_name", last_name)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("password", password)
        jsonObject.addProperty("password_confirmation", password_confirmation)
        jsonObject.addProperty("phone_no", phone_no)
        jsonObject.addProperty("profile_image", profile_image)
        jsonObject.addProperty("street_address", street_address)
        jsonObject.addProperty("country", country)
        jsonObject.addProperty("city", city)
        jsonObject.addProperty("state", state)
        jsonObject.addProperty("zip_code", zip_code)
        jsonObject.addProperty("country_name_code", country_name_code)
        jsonObject.addProperty("petitioner_firstname", firstNameUs)
        jsonObject.addProperty("petitioner_lastname", lastNameUs)
        jsonObject.addProperty("petitioner_email", emailUs)

        safeApiCall {
            Result.success(apiServices.signUp(jsonObject))
        }
    }


    //User updateGuestUserSignUp call
    suspend fun updateGuestUserSignUp(
        user_id: String,
        first_name: String,
        last_name: String,
        email: String,
        password: String,
        password_confirmation: String,
        phone_no: String,
        profile_image: String,
        street_address: String,
        country: String,
        city: String,
        state: String,
        zip_code: String,
        country_name_code: String,
        firstNameUs: String,
        lastNameUs: String,
        emailUs: String
    ) = withContext(dispatcher) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", user_id)
        jsonObject.addProperty("first_name", first_name)
        jsonObject.addProperty("last_name", last_name)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("password", password)
        jsonObject.addProperty("password_confirmation", password_confirmation)
        jsonObject.addProperty("phone_no", phone_no)
        jsonObject.addProperty("profile_image", profile_image)

        jsonObject.addProperty("street_address", street_address)
        jsonObject.addProperty("country", country)
        jsonObject.addProperty("city", city)
        jsonObject.addProperty("state", state)
        jsonObject.addProperty("zip_code", zip_code)

        jsonObject.addProperty("country_name_code", country_name_code)
        jsonObject.addProperty("petitioner_firstname", firstNameUs)
        jsonObject.addProperty("petitioner_lastname", lastNameUs)
        jsonObject.addProperty("petitioner_email", emailUs)

        safeApiCall {
            Result.success(apiServices.updateGuestUserSignUp(jsonObject))
        }
    }


    //User Login call
    suspend fun loginUser(
        email: String, password: String
    ) = withContext(dispatcher) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("password", password)

        safeApiCall {
            Result.success(
                apiServices.login(jsonObject)
            )
        }
    }


    //User Guest Login call
    suspend fun guestLogin() = withContext(dispatcher) {
        safeApiCall {
            Result.success(apiServices.guestLogin())
        }
    }


    //Send Code call
    suspend fun sendCode(
        email: String
    ) = withContext(dispatcher) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", email)
        safeApiCall {
            Result.success(apiServices.sendOTP(jsonObject))
        }
    }


    //Verify Code call
    suspend fun verifyCode(
        email: String, code: String
    ) = withContext(dispatcher) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("code", code)
        safeApiCall {
            Result.success(apiServices.emailVerify(jsonObject))
        }
    }


    //Forgot Password call
    suspend fun forgotPassword(
        email: String
    ) = withContext(dispatcher) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", email)
        safeApiCall {
            Result.success(apiServices.forgotPassword(jsonObject))
        }
    }

    //Recover Password call
    suspend fun recoverPassword(
        email: String, password: String, password_confirmation: String
    ) = withContext(dispatcher) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("password", password)
        jsonObject.addProperty("password_confirmation", password_confirmation)
        safeApiCall {
            Result.success(apiServices.recoverPassword(jsonObject))
        }
    }

    //QuestionAddUpdate call
    suspend fun questionAddUpdate(
        user_id: String,
        prev_selections: String,
        last_question: String,
        current_summary: String,
        questions_order: String
    ) = withContext(dispatcher) {

        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", user_id)
        jsonObject.addProperty("prev_selections", prev_selections)
        jsonObject.addProperty("last_question", last_question)
        jsonObject.addProperty("current_summary", current_summary)
        jsonObject.addProperty("questions_order", questions_order)
        safeApiCall {
            Result.success(apiServices.questionAddOrUpdateState("Bearer $token", jsonObject))
        }
    }

    //QuestionnaireDelete call
    suspend fun questionnaireDelete(user_id: String) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", user_id)
        safeApiCall {
            Result.success(apiServices.questionnaireDelete("Bearer $token", jsonObject))
        }
    }

    //Plan Details call
    suspend fun planDetails(
        plan_id: String
    ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("plan_id", plan_id)
        safeApiCall {
            Result.success(apiServices.planDetails("Bearer $token", jsonObject))
        }
    }


    //QuestionState call
    suspend fun getQuestionState(
        user_id: String
    ) = withContext(dispatcher) {

        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", user_id)
        safeApiCall {
            Result.success(apiServices.getQuestionState("Bearer $token", jsonObject))
        }
    }


    //Logout APi call
    suspend fun logOut(
    ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(apiServices.logOut("Bearer $token"))
        }
    }


    //DeleteAccount APi call
    suspend fun deleteAccount(
    ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val userId = SharePreferenceHelper.getInstance(app).getUser().id
        safeApiCall {
            Result.success(apiServices.deleteAccount("Bearer $token", userId!!))
        }
    }


    //Get Profile call
    suspend fun getProfile() = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(apiServices.getProfile("Bearer $token"))
        }
    }

    //Get User Payment Details
    suspend fun getUser(email: RequestBody) = withContext(dispatcher) {
        safeApiCall {
            Result.success(apiServices.getUser(email))
        }
    }


    //Update payment call
    suspend fun updatePayment(email: RequestBody, token: RequestBody, lastFour: RequestBody) =
        withContext(dispatcher) {

            safeApiCall {
                Result.success(apiServices.updateCard(email, token, lastFour))
            }
        }

    suspend fun addPayment(
        paymentToken: RequestBody, lastFour: RequestBody, cardHolder: RequestBody,
        expiryDate: RequestBody
    ) =
        withContext(dispatcher) {
            val bearerToken = SharePreferenceHelper.getInstance(app).getToken()
            safeApiCall {
                Result.success(
                    apiServices.addCard(
                        "Bearer $bearerToken",
                        paymentToken,
                        lastFour,
                        cardHolder,
                        expiryDate
                    )
                )
            }
        }

    //Update Profile call
    suspend fun updateProfile(
        first_name: RequestBody,
        last_name: RequestBody,
        email: RequestBody,
        phone_no: RequestBody,
        street_address: RequestBody,
        country: RequestBody,
        state: RequestBody,
        city: RequestBody,
        zip_code: RequestBody,
        country_name_code: RequestBody,
        firstNameUs: RequestBody,
        lastNameUs: RequestBody,
        emailUs: RequestBody
    ) = withContext(dispatcher) {

        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(
                apiServices.updateProfile(
                    "Bearer $token",
                    first_name,
                    last_name,
                    email,
                    phone_no,
                    street_address,
                    country,
                    state,
                    city,
                    zip_code,
                    country_name_code,
                    firstNameUs,
                    lastNameUs,
                    emailUs
                )
            )
        }
    }

    //Get Profile call
    suspend fun getAuthToken() = withContext(dispatcher) {
        safeApiCall {
            Result.success(
                apiServices.getAuthToken(
                    BASE_URL + "v1/general/getAuthToken"
                )
            )
        }
    }

    //Get Profile call
    suspend fun getQuestions(token: String) = withContext(dispatcher) {
//        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(
                apiServices.getQuestions(
                    BASE_URL + "v1/general/getQuestionare", "Bearer $token"
                )
            )
        }
    }

    //Get News call
    suspend fun getNews(per_page: Int, offset: Int) = withContext(dispatcher) {
        safeApiCall {
            Result.success(apiServices.getNews(NEWS_URL, per_page, offset))
        }
    }

    //Consultation call
    suspend fun booConsultation(
        user_id: String,
        date: String,
        consultation_time: String,
        consultation_id: String,
        consultation_with: String,
        stripe_token: String,
        amount: Double,
        datetime: String,
        appointmentTypeID: String,
        calendarID: String,
        phoneNo: String,
        email: String,
        street_address: String,
        city: String,
        state: String,
        zip_code: String,
        country: String,
        description: String,
        term_and_condition: Boolean,
        timezone: String,
        isCardSave: Boolean,
        lastFourDigits: String,
        is_other_payment_gateway: Boolean,
        cardHolder: String,
        expiryDate: String,


        ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", user_id)
        jsonObject.addProperty("date", date)
        jsonObject.addProperty("consultation_time", consultation_time)
        jsonObject.addProperty("consultation_id", consultation_id)
        jsonObject.addProperty("consultation_with", consultation_with)
        jsonObject.addProperty("stripe_token", stripe_token)
        jsonObject.addProperty("amount", amount)
        jsonObject.addProperty("datetime", datetime)
        jsonObject.addProperty("appointmentTypeID", appointmentTypeID)
        jsonObject.addProperty("calendarID", calendarID)
        jsonObject.addProperty("phone_no", phoneNo)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("street_address", street_address)
        jsonObject.addProperty("city", city)
        jsonObject.addProperty("state", state)
        jsonObject.addProperty("zip_code", zip_code)
        jsonObject.addProperty("country", country)
        jsonObject.addProperty("description", description)
        jsonObject.addProperty("term_and_condition", term_and_condition)
        jsonObject.addProperty("timezone", timezone)
        jsonObject.addProperty("is_card_save", isCardSave)
        jsonObject.addProperty("pm_last_four", lastFourDigits)
        jsonObject.addProperty("card_holder_name", cardHolder)
        jsonObject.addProperty("expire_date", expiryDate)
        jsonObject.addProperty("is_web", 1)
        jsonObject.addProperty("is_other_payment_gateway", is_other_payment_gateway)
        safeApiCall {
            Result.success(apiServices.bookConsultation("Bearer $token", jsonObject))
        }
    }

    //Consultation call
    suspend fun bookConsultationForGuestMode(
        user_id: String,
        date: String,
        consultation_time: String,
        consultation_id: String,
        consultation_with: String,
        stripe_token: String,
        amount: Double,
        datetime: String,
        appointmentTypeID: String,
        calendarID: String,
        phoneNo: String,
        email: String,
        street_address: String,
        city: String,
        state: String,
        zip_code: String,
        country: String,
        countryCode: String,
        description: String,
        term_and_condition: Boolean,
        timezone: String,
        mPassword: String,
        confirm_password: String,
        firstName: String,
        lastName: String,
        isLogin: Int

    ) = withContext(dispatcher) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("first_name", firstName)
        jsonObject.addProperty("last_name", lastName)
        jsonObject.addProperty("user_id", user_id)
        jsonObject.addProperty("date", date)
        jsonObject.addProperty("consultation_time", consultation_time)
        jsonObject.addProperty("consultation_id", consultation_id)
        jsonObject.addProperty("consultation_with", consultation_with)
        jsonObject.addProperty("stripe_token", stripe_token)
        jsonObject.addProperty("amount", amount)
        jsonObject.addProperty("datetime", datetime)
        jsonObject.addProperty("appointmentTypeID", appointmentTypeID)
        jsonObject.addProperty("calendarID", calendarID)
        jsonObject.addProperty("phone_no", phoneNo)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("street_address", street_address)
        jsonObject.addProperty("city", city)
        jsonObject.addProperty("state", state)
        jsonObject.addProperty("zip_code", zip_code)
        jsonObject.addProperty("country", country)
        jsonObject.addProperty("country_name_code", countryCode)
        jsonObject.addProperty("description", description)
        jsonObject.addProperty("term_and_condition", term_and_condition)
        jsonObject.addProperty("timezone", timezone)
        jsonObject.addProperty("is_login", isLogin)
        jsonObject.addProperty("password", mPassword)
        jsonObject.addProperty("password_confirmation", confirm_password)
        jsonObject.addProperty("is_web", 1)


        safeApiCall {
            Result.success(apiServices.bookConsultationForGuestMode(jsonObject))
        }
    }

    suspend fun bookConsultationForGuestModeForCreateAccount(
        user_id: String,
        date: String,
        consultation_time: String,
        consultation_id: String,
        consultation_with: String,
        stripe_token: String,
        amount: Double,
        datetime: String,
        appointmentTypeID: String,
        calendarID: String,
        phoneNo: String,
        email: String,
        street_address: String,
        city: String,
        state: String,
        zip_code: String,
        country: String,
        countryCode: String,
        description: String,
        term_and_condition: Boolean,
        timezone: String,
        mPassword: String,
        confirm_password: String,
        firstName: String,
        lastName: String,
        isLogin: Int


    ) = withContext(dispatcher) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("first_name", firstName)
        jsonObject.addProperty("last_name", lastName)
        jsonObject.addProperty("user_id", user_id)
        jsonObject.addProperty("date", date)
        jsonObject.addProperty("consultation_time", consultation_time)
        jsonObject.addProperty("consultation_id", consultation_id)
        jsonObject.addProperty("consultation_with", consultation_with)
        jsonObject.addProperty("stripe_token", stripe_token)
        jsonObject.addProperty("amount", amount)
        jsonObject.addProperty("datetime", datetime)
        jsonObject.addProperty("appointmentTypeID", appointmentTypeID)
        jsonObject.addProperty("calendarID", calendarID)
        jsonObject.addProperty("phone_no", phoneNo)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("street_address", street_address)
        jsonObject.addProperty("city", city)
        jsonObject.addProperty("state", state)
        jsonObject.addProperty("zip_code", zip_code)
        jsonObject.addProperty("country", country)
        jsonObject.addProperty("country_name_code", countryCode)
        jsonObject.addProperty("description", description)
        jsonObject.addProperty("term_and_condition", term_and_condition)
        jsonObject.addProperty("timezone", timezone)
        jsonObject.addProperty("is_login", isLogin)
        jsonObject.addProperty("is_web", 1)
        jsonObject.addProperty("password", mPassword)
        jsonObject.addProperty("password_confirmation", confirm_password)

        safeApiCall {
            Result.success(apiServices.bookConsultationForGuestModeForCreateAccount(jsonObject))
        }
    }

    //Subscription call
    suspend fun subscription(
        stripe_payment_method: String,
        plan_id: String,
        price_id: String,
        quantity: Int,
        isCardSave: Boolean,
        lastFourDigits: String,
        is_other_payment_gateway: Boolean,
        cardHolder: String,
        expiryDate: String,

        ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("stripe_payment_method", stripe_payment_method)
        jsonObject.addProperty("plan_id", plan_id)
        jsonObject.addProperty("price_id", price_id)
        jsonObject.addProperty("quantity", quantity)
        jsonObject.addProperty("is_web", 1)
        jsonObject.addProperty("is_card_save", isCardSave)
        jsonObject.addProperty("pm_last_four", lastFourDigits)
        jsonObject.addProperty("card_holder_name", cardHolder)
        jsonObject.addProperty("expire_date", expiryDate)
        jsonObject.addProperty("is_other_payment_gateway", is_other_payment_gateway)
        safeApiCall {
            Result.success(apiServices.subscription("Bearer $token", jsonObject))
        }
    }

    //fixme 2
    //Subscription call
    suspend fun subscriptionForGuestMode(
        uId: String,
        fName: String,
        lName: String,
        email: String,
        phoneNumber: String,
        street_address: String,
        country: String,
        city: String,
        state: String,
        zip_code: String,
        countryNameCode: String,
        Usfname: String,
        UsLastName: String,
        UsEmail: String,
        password: String,
        confirmPassword: String,
        is_Login: Int,
        stripe_payment_method: String,
        plan_id: String,
        price_id: String,
        quantity: Int
    ) = withContext(dispatcher) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", uId)
        jsonObject.addProperty("stripe_payment_method", stripe_payment_method)
        jsonObject.addProperty("plan_id", plan_id)
        jsonObject.addProperty("price_id", price_id)
        jsonObject.addProperty("quantity", quantity)
        jsonObject.addProperty("first_name", fName)
        jsonObject.addProperty("last_name", lName)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("phone_no", phoneNumber)

        jsonObject.addProperty("is_web", 1)

        jsonObject.addProperty("street_address", street_address)
        jsonObject.addProperty("country", country)
        jsonObject.addProperty("city", city)
        jsonObject.addProperty("state", state)
        jsonObject.addProperty("zip_code", zip_code)

        jsonObject.addProperty("country_name_code", countryNameCode)

        jsonObject.addProperty("petitioner_email", UsEmail)
        jsonObject.addProperty("petitioner_firstname", Usfname)
        jsonObject.addProperty("petitioner_lastname", UsLastName)
        jsonObject.addProperty("is_us_citizen", 1)
        jsonObject.addProperty("password", password)
        jsonObject.addProperty("password_confirmation", confirmPassword)
        jsonObject.addProperty("is_login", is_Login)

        safeApiCall {
            Result.success(apiServices.subscriptionForGuestMode(jsonObject))
        }
    }

    //fixme
    suspend fun subscriptionForGuestModeForSignUp(
        uId: String,
        fName: String,
        lName: String,
        email: String,
        phoneNumber: String,

        street_address: String,
        country: String,
        city: String,
        state: String,
        zip_code: String,

        countryNameCode: String,


        Usfname: String,
        UsLastName: String,
        UsEmail: String,
        password: String,
        confirmPassword: String,
        is_Login: Int,
        stripe_payment_method: String,
        plan_id: String,
        price_id: String,
        quantity: Int
    ) = withContext(dispatcher) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", uId)
        jsonObject.addProperty("stripe_payment_method", stripe_payment_method)
        jsonObject.addProperty("plan_id", plan_id)
        jsonObject.addProperty("price_id", price_id)
        jsonObject.addProperty("quantity", quantity)
        jsonObject.addProperty("first_name", fName)
        jsonObject.addProperty("last_name", lName)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("phone_no", phoneNumber)
        jsonObject.addProperty("is_web", 1)
        jsonObject.addProperty("street_address", street_address)
        jsonObject.addProperty("country", country)
        jsonObject.addProperty("city", city)
        jsonObject.addProperty("state", state)
        jsonObject.addProperty("zip_code", zip_code)

        jsonObject.addProperty("country_name_code", countryNameCode)
        jsonObject.addProperty("petitioner_email", UsEmail)
        jsonObject.addProperty("petitioner_firstname", Usfname)
        jsonObject.addProperty("petitioner_lastname", UsLastName)
        jsonObject.addProperty("is_us_citizen", 1)
        jsonObject.addProperty("password", password)
        jsonObject.addProperty("password_confirmation", confirmPassword)
        jsonObject.addProperty("is_login", is_Login)

        safeApiCall {
            Result.success(apiServices.subscriptionForGuestModeForSignUp(jsonObject))
        }
    }

    //Get Packages call
    suspend fun getPackages(
        package_id: String
    ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("package_id", package_id)
        safeApiCall {
            Result.success(apiServices.getPackages("Bearer $token", jsonObject))
        }
    }

    //Get Services History
    suspend fun getServicesHistory() = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(apiServices.getServicesHistory("Bearer $token"))
        }
    }


    //GetDates call
    suspend fun getDates(
        month: String, appointmentTypeID: String, calendarID: String, timezone: String
    ) = withContext(dispatcher) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("month", month)
        jsonObject.addProperty("appointmentTypeID", appointmentTypeID)
        jsonObject.addProperty("calendarID", calendarID)
        jsonObject.addProperty("timezone", timezone)

        safeApiCall {
            Result.success(apiServices.getDates(jsonObject))
        }
    }


    //GetTimeSlot call
    suspend fun getTimeSlots(
        date: String, appointmentTypeID: String, calendarID: String, timezone: String
    ) = withContext(dispatcher) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("date", date)
        jsonObject.addProperty("appointmentTypeID", appointmentTypeID)
        jsonObject.addProperty("calendarID", calendarID)
        jsonObject.addProperty("timezone", timezone)

        safeApiCall {
            Result.success(apiServices.getTimeSlots(jsonObject))
        }
    }


    //Get Calender
    suspend fun getCalender() = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(apiServices.getCalender("Bearer $token"))
        }
    }


    //Get Appointment Type
    suspend fun getAppointmentType() = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(apiServices.appointmentTypes("Bearer $token"))
        }
    }

    //GetTimeDateAcuity call
    suspend fun getAcuityTimeDate(
        month: String,
        appointmentTypeID: String,
        calendarID: String,
        timezone: String,
        is_previous: Int,
    ) = withContext(dispatcher) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("month", month)
        jsonObject.addProperty("appointmentTypeID", appointmentTypeID)
        jsonObject.addProperty("calendarID", calendarID)
        jsonObject.addProperty("timezone", timezone)
        jsonObject.addProperty("is_previous", is_previous)

        safeApiCall {
            Result.success(apiServices.getAcuityTimeAndDates(jsonObject))
        }
    }


    //sendDataToEmail call
    suspend fun sendEvaluationDataToEmail(
        data: String, is_english: Int
    ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("data", data)
        jsonObject.addProperty("is_english  ", is_english)
        safeApiCall {
            Result.success(apiServices.sendEvaluationDataToEmail("Bearer $token", jsonObject))
        }
    }

    //Get Package Filter
    suspend fun getPackageFilter() = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(apiServices.getPackageFilter("Bearer $token"))
        }
    }

    //Get Terms And Condition call
    suspend fun getTermsAndCondition() = withContext(dispatcher) {
        safeApiCall {
            Result.success(apiServices.getTermsAndCondition(TERMS_CONDITION_URL))
        }
    }

    //sendLanguage call
    suspend fun sendLanguage(
        language: Int
    ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("language", language)
        safeApiCall {
            Result.success(apiServices.sendLanguage("Bearer $token", jsonObject))
        }
    }

    //BugReport call
    suspend fun bugReport(
        node_url: String, description: String, qLabel: String
    ) = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        val jsonObject = JsonObject()
        jsonObject.addProperty("node_url", node_url)
        jsonObject.addProperty("description", description)
        jsonObject.addProperty("current_node", qLabel)
        safeApiCall {
            Result.success(apiServices.bugReport("Bearer $token", jsonObject))
        }
    }

    //GetTime Zone
    suspend fun getTimeZone() = withContext(dispatcher) {
        val token = SharePreferenceHelper.getInstance(app).getToken()
        safeApiCall {
            Result.success(apiServices.timeZone("Bearer $token"))
        }
    }
}