package com.open.borders.views.activities.splash

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.ActivitySplashBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.SplashViewModelModule
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.models.generalModel.questionsApiModel.GetQuestionsModel
import com.open.borders.thirdPartyModules.htmltextview.HtmlResImageGetter
import com.open.borders.utils.Constants
import com.open.borders.utils.CryptoAES
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.MainMVVMBaseActivity
import com.open.borders.views.activities.navigationHost.HomeHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@SuppressLint("CustomSplashScreen")
@InternalSerializationApi
class SplashActivity : MainMVVMBaseActivity<SplashViewModel>(SplashViewModel::class) {
    private lateinit var sharePreferenceHelper: SharePreferenceHelper
    private lateinit var binding: ActivitySplashBinding
    override fun registerModule(): Module {
        return SplashViewModelModule
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharePreferenceHelper = SharePreferenceHelper.getInstance(this@SplashActivity)

        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
        viewModel.getQuestionAuthToken()
//        binding.splashImageId?.let {
//            Glide.with(this)
//                .load(R.drawable.open_border_gif_logo)
//                .into(it)
//        }

        //        deleteQuestionnaireApiCall()
        setSelectedLanguage()

        lifecycleScope.launch {
            delay(6000)
            binding.openBorderLogo?.viewGone()
            binding.openBorderLogos?.viewVisible()
//            binding.openBorderLogo?.setAnimation(R.raw.welcome_new)
            SharePreferenceHelper.getInstance(this@SplashActivity).saveTabType("")
//            binding.openBorderLogo?.animate()?.apply {

//            }?.start()
//            delay(2000)
            goLogin()
        }
    }

    private fun setSelectedLanguage() {
        if (tinyDB.getString(Constants.language) != null) {
            when (tinyDB.getString(Constants.language)) {
                "es" -> {
                    setSpanish()
                }
                else -> {
                    setEnglish()
                }
            }
        }
    }

    private fun goLogin() {
//        if (sharedPreference?.isFirstTimeTrue() == true) {
//            startActivity<SelectLanguageActivity>()
//        } else {
        if (sharedPreference?.isUserLoggedIn() == true) {
            binding.splashImage?.setImageResource(R.drawable.ic_splash_logo)
//            binding.openBorderLogo?.viewGone()
//            val sIntent = Intent(this, HomeHost::class.java)
//            val activityOption = ActivityOptions.makeSceneTransitionAnimation(this, binding.splashImage, "splashLottie")
//            startActivity(sIntent)
            getQuestionFromAPi(SharePreferenceHelper.getInstance(this).getQuestionAuthToken()!!)
        } else {
            viewModel.guestLogin()
        }
//        }
    }

    private fun deleteQuestionnaireApiCall() {
        //Get Question Api Call
        if (isNetworkAvailable(this)) {
            viewModel.deleteQuestioner(
                sharedPreference?.getUser()?.id.toString()
            )
        } else {
            CustomToasts.showToast(
                this,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(this, msg, false)
            }
        }

        observe(viewModel.questionnaireDelete) {
            if (it.status) {
                CustomToasts.showToast(this, it.message, true)
            } else {
                CustomToasts.showToast(this, it.message, false)
            }
        }

        observe(viewModel.guestLoginLiveData) {
            if (it.status) {
                getQuestionFromAPi(SharePreferenceHelper.getInstance(this).getQuestionAuthToken()!!)
//                val intent = Intent(this, HomeHost::class.java)
////                val activityOption = ActivityOptions.makeSceneTransitionAnimation(this, binding.openBorderLogo, "splashLottie")
//                startActivity(intent)
            }
        }

        observe(viewModel.questionAuthToken) {
            if (it.status == true) {
                lifecycleScope.launch {
                    Log.d("cddcdcdc", "dcdcdc")
                    SharePreferenceHelper.getInstance(this@SplashActivity)
                        .saveQuestionAuthToken(it.token.toString())
                }
            }
        }

        observe(viewModel.getQuestions) {
            if (it.status == true) {
                Log.d("cddcdcdc", "2")
                val encrypted = it.data
                var decrypted = ""
                var decryptedQuestions: GetQuestionsModel? = null
                try {
                    decrypted = CryptoAES.decrypt("open_border_8824714592", encrypted!!)
                    decryptedQuestions = Gson().fromJson(decrypted, GetQuestionsModel::class.java)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                sharePreferenceHelper.saveQuestion(decryptedQuestions!!)
                val intent = Intent(this, HomeHost::class.java)
//                val activityOption = ActivityOptions.makeSceneTransitionAnimation(this, binding.openBorderLogo, "splashLottie")
                startActivity(intent)
            }
        }
    }

    private fun getQuestionFromAPi(token: String) {
        if (isNetworkAvailable(this@SplashActivity)) {
            viewModel.getQuestions(token)
        } else {
            CustomToasts.showToast(
                this@SplashActivity,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }
}