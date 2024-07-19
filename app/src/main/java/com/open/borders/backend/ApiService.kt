package com.open.borders.backend

import com.google.gson.JsonObject
import com.open.borders.models.generalModel.historyModels.HistoryResponse
import com.open.borders.models.generalModel.newsModel.NewsResponse
import com.open.borders.models.responseModel.*
import com.open.borders.models.utilsModels.BaseResponse
import com.open.borders.views.activities.baseActivity.SummeryModel
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("sign-up")
    suspend fun signUp(
        @Body body: JsonObject
    ): SignUpResponse

    @POST("update-guest-user")
    suspend fun updateGuestUserSignUp(
        @Body body: JsonObject
    ): SignUpResponse

    @POST("sign-in")
    suspend fun login(
        @Body body: JsonObject
    ): LoginResponse

    @POST("login-as-gust")
    suspend fun guestLogin(): GuestResponseModel

    @POST("send-otp")
    suspend fun sendOTP(
        @Body body: JsonObject
    ): SendOTPResponse

    @POST("verify-otp")
    suspend fun emailVerify(
        @Body body: JsonObject
    ): VerifyEmailResponse

    @POST("forget-password")
    suspend fun forgotPassword(
        @Body body: JsonObject
    ): ForgetPasswordResponse

    @POST("recover-password")
    suspend fun recoverPassword(
        @Body body: JsonObject
    ): RecoverPasswordResponse

    @Headers("Accept: application/json")
    @POST("questionnaire-state/store")
    suspend fun questionAddOrUpdateState(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): QuestionAddUpdateResponse

    @Headers("Accept: application/json")
    @POST("questionnaire-state/delete")
    suspend fun questionnaireDelete(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): SignUpResponse

    @Headers("Accept: application/json")
    @POST("payments/package/plan")
    suspend fun planDetails(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): GetPackageDetailsResponse

    @Headers("Accept: application/json")
    @POST("questionnaire-state/show")
    suspend fun getQuestionState(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): QuestionStateResponse

    @Headers("Accept: application/json")
    @POST("sign-out")
    suspend fun logOut(
        @Header("Authorization") token: String
    ): LogoutResponse

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("delete-account")
    suspend fun deleteAccount(
        @Header("Authorization") token: String,
        @Field("user_id") userId: Int
    ): LogoutResponse

    @Headers("Accept: application/json")
    @GET("get-profile")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): GetProfileResponse

    //    @Headers("Accept: application/json")
    @Multipart
    @POST("v1/get-user")
    suspend fun getUser(
        @Part("email") email: RequestBody,
    ): GetPaymentResponse

    @Headers("Accept: application/json")
    @Multipart
    @POST("payments/v1/add-card")
    suspend fun addCard(
        @Header("Authorization") token: String,
        @Part("stripe_token") stripeToken: RequestBody,
        @Part("pm_last_four") pmLastFour: RequestBody,
        @Part("card_holder_name") cardHolderName: RequestBody,
        @Part("expire_date") expiryDate: RequestBody,
    ): GetPaymentResponse


    @Multipart
    @POST("v1/update-card")
    suspend fun updateCard(
        @Part("email") email: RequestBody,
        @Part("stripe_token") stripeToken: RequestBody,
        @Part("pm_last_four") pmLastFour: RequestBody,
    ): GetPaymentResponse


    @Headers("Accept: application/json")
    @Multipart
    @POST("update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_no") phone_no: RequestBody,
        @Part("street_address") street_address: RequestBody,
        @Part("country") country: RequestBody,
        @Part("state") state: RequestBody,
        @Part("city") city: RequestBody,
        @Part("zip_code") zip_code: RequestBody,
        @Part("country_name_code") country_name_code: RequestBody,
        @Part("petitioner_firstname") petitioner_firstname: RequestBody,
        @Part("petitioner_lastname") petitioner_lastname: RequestBody,
        @Part("petitioner_email") petitioner_email: RequestBody,
    ): GetProfileResponse

    @GET()
    suspend fun getAuthToken(@Url url: String): GetAuthTokenResponse

    @Headers("Accept: application/json")
    @GET()
    suspend fun getQuestions(
        @Url url: String,
        @Header("Authorization") token: String,
    ): QuestionerIncryptedResponse

    @GET()
    suspend fun getNews(
        @Url url: String,
        @Query("per_page") per_page: Int,
        @Query("offset") offset: Int
    ): NewsResponse

    @Headers("Accept: application/json")
    @POST("consultation/v1/booking")
    suspend fun bookConsultation(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): ConsultationResponse


    @POST("v1/booking")
    suspend fun bookConsultationForGuestMode(
        @Body body: JsonObject
    ): ConsultationResponse

    @POST("booking")
    suspend fun bookConsultationForGuestModeForCreateAccount(
        @Body body: JsonObject
    ): SignUpResponse

    @Headers("Accept: application/json")
    @POST("payments/v1/subscription")
    suspend fun subscription(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): SubscriptionResponse


    @POST("v1/subscription")
    suspend fun subscriptionForGuestMode(
        @Body body: JsonObject
    ): SubscriptionResponse


    @POST("v1/subscription")
    suspend fun subscriptionForGuestModeForSignUp(
        @Body body: JsonObject
    ): SignUpResponse

    @Headers("Accept: application/json")
    @POST("payments/packages/plan")
    suspend fun getPackages(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): GetPackagesResponse

    @Headers("Accept: application/json")
    @GET("user/buy-plan-history")
    suspend fun getServicesHistory(
        @Header("Authorization") token: String
    ): HistoryResponse

    @POST("availability/times")
    suspend fun getTimeSlots(
        @Body body: JsonObject
    ): TimeSlotsResponse

    @POST("availability/dates")
    suspend fun getDates(
        @Body body: JsonObject
    ): DateResponse

    @Headers("Accept: application/json")
    @GET("calendars")
    suspend fun getCalender(
        @Header("Authorization") token: String
    ): CalenderResponse

    @Headers("Accept: application/json")
    @GET("appointment-types")
    suspend fun appointmentTypes(
        @Header("Authorization") token: String
    ): AppointmentTypeResponse

    @POST("availability/date-time")
    suspend fun getAcuityTimeAndDates(
        @Body body: JsonObject
    ): AcuityResponse

    @Headers("Accept: application/json")
    @POST("questionnaire-state/send-email")
    suspend fun sendEvaluationDataToEmail(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): SendEmailDataResponse

    @Headers("Accept: application/json")
    @GET("payments/packages/list")
    suspend fun getPackageFilter(
        @Header("Authorization") token: String,
    ): PackageFilterResponse

    @GET()
    suspend fun getTermsAndCondition(@Url url: String): TermsAndConditionResponse

    @Headers("Accept: application/json")
    @POST("update-language")
    suspend fun sendLanguage(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): SendEmailDataResponse

    @Headers("Accept: application/json")
    @POST("report-bugs")
    suspend fun bugReport(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): BugReportResponse

    @Headers("Accept: application/json")
    @GET("timezone-list")
    suspend fun timeZone(
        @Header("Authorization") token: String,
    ): TimeZoneResponse
}

