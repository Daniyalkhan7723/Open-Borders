package com.open.borders.views.activities.selectLanguage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.open.borders.databinding.ActivitySelectLanguageBinding
import com.open.borders.extensions.*
import com.open.borders.utils.Constants
import com.open.borders.utils.ContextUtils
import com.open.borders.views.activities.baseActivity.BaseActivity
import com.open.borders.views.activities.navigationHost.AuthenticationHost
import com.open.borders.views.activities.navigationHost.HomeHost
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import java.util.*

@InternalSerializationApi
class SelectLanguageActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (intent != null) {
            if (intent.getBooleanExtra("isFromAccount", false)) {
                binding.backArrow?.viewVisible()
                binding.backArrow?.setOnClickListener {
                    onBackPressed()
                }
            } else {
                binding.backArrow?.viewGone()
            }
        }

        when (getCurrentLanguage()) {
            "es" -> {
                binding.spanishSelectionIV.viewVisible()
            }
            else -> {
                binding.eglishSelectionIV.viewVisible()
            }
        }

        if (sharedPreference?.isFirstTimeTrue() == true) {
            selectLanguageNavigateToLogin()
        } else {
            selectLanguageNavigateToHome()
        }
    }

    private fun selectLanguageNavigateToHome() {
        binding.englishLayout.setOnClickListener {
            tinyDB.putString(Constants.language, "en_US")
            ContextUtils.updateLocale(applicationContext, Locale("en_US"))
            binding.eglishSelectionIV.viewVisible()
            binding.spanishSelectionIV.viewGone()
            firstTimeFalse()
            val intent = Intent(this, HomeHost::class.java)
            startActivity(intent)
        }

        binding.spanishLayout.setOnClickListener {
            tinyDB.putString(Constants.language, "es")
            ContextUtils.updateLocale(applicationContext, Locale("es"))
            binding.spanishSelectionIV.viewVisible()
            binding.eglishSelectionIV.viewGone()
            firstTimeFalse()
            val intent = Intent(this, HomeHost::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun selectLanguageNavigateToLogin() {
        binding.englishLayout.setOnClickListener {
            tinyDB.putString(Constants.language, "en_US")
            ContextUtils.updateLocale(applicationContext, Locale("en_US"))
            binding.eglishSelectionIV.viewVisible()
            binding.spanishSelectionIV.viewGone()
            firstTimeFalse()
            val intent = Intent(this, AuthenticationHost::class.java)
            startActivity(intent)

        }
        binding.spanishLayout.setOnClickListener {
            tinyDB.putString(Constants.language, "es")
            ContextUtils.updateLocale(applicationContext, Locale("es"))
            binding.spanishSelectionIV.viewVisible()
            binding.eglishSelectionIV.viewGone()
            firstTimeFalse()
            val intent = Intent(this, AuthenticationHost::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun firstTimeFalse() {
        lifecycleScope.launch {
            sharedPreference?.isFirstTime(false)
        }
    }
}