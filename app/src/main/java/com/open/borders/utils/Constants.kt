package com.open.borders.utils

import android.security.keystore.KeyProperties
import com.google.android.gms.wallet.WalletConstants
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.views.activities.baseActivity.SummeryModel

class Constants {
    companion object {
        val IMAGE_BASE_URL = "https://admin.openborders.io/public/"
        const val Bundle = "Bundle"
        val SERVER_NOT_RESPONDING_MESSAGE =
            "Server is not responding properly please try again later"
        val INTERNET_CONNECTION_ERROR_MESSAGE =
            "Please check your internet connection or try again later"
        val WENT_WRONG_MESSAGE =
            "Something went wrong please check your connection or try again later"
        var SIGN_UP_FLAG = false
        var LOG_IN_FLAG = false
        var isSelected = false
        var termsFromSignUp = false
        val language = "language"

        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

        const val stripePublishKey = "pk_live_qnJs6x0H3LdgCPIOsSIoIVjw"
//        const val stripePublishKey =
//            "pk_test_51JzZuXHlJ57SdeJaeSzuMD7oHsPQa05OesccvVzDKFDsjpVVQWyQIT1EyrI4FfAQNWZNzWfkhc6cuiz5kk6NVqhP00EnouzOCE"
//        const val stripePublishKey = "pk_test_M4kc0ugwOFFrcPtUyxdpgKMG"

        const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_PRODUCTION
//        const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

        val SUPPORTED_NETWORKS = listOf(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA"
        )

        val SUPPORTED_METHODS = listOf(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS"
        )

        const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "stripe"

        val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
            "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
            "stripe:version" to "2020-08-27",
            "stripe:publishableKey" to stripePublishKey
        )
    }
}