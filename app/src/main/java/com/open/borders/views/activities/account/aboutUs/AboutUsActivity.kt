package com.open.borders.views.activities.account.aboutUs

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDelegate
import com.open.borders.R
import com.open.borders.databinding.ActivityAboutUsBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class AboutUsActivity : BaseActivity(), WebViewClient.AListener {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        logScreenName("About Us")
        binding.toolbarId.llChangeLanguage.viewGone()
        binding.toolbarId.toolbarNameTv.text = getString(R.string.about_us)
        binding.toolbarId.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.shimmerFrameLayout.viewVisible()

        val webSettings: WebSettings = binding.mWebView!!.settings
        webSettings.javaScriptEnabled = true
        binding.mWebView.loadUrl("https://bordercrossinglaw.com/about")

        binding.mWebView.webViewClient = WebViewClient(this)
    }

    override fun onLoadCancel() {
        binding.shimmerFrameLayout.viewGone()
        binding.mWebView.viewVisible()
    }
}


private class WebViewClient(private val listener: AListener) : android.webkit.WebViewClient() {
    override fun onPageStarted(
        view: WebView,
        url: String,
        favicon: Bitmap?
    ) {
        super.onPageStarted(view, url, favicon)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String
    ): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        listener.onLoadCancel()
    }

    interface AListener {
        fun onLoadCancel()
    }
}