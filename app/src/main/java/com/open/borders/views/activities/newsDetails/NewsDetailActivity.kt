package com.open.borders.views.activities.newsDetails

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.open.borders.R
import com.open.borders.databinding.ActivityNewsDetailBinding
import com.open.borders.extensions.*
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class NewsDetailActivity : BaseActivity(), WebViewClient.itemClickListener {
    private lateinit var binding: ActivityNewsDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        logScreenName("News Details")
        binding.toolbarId.llChangeLanguage.viewGone()
        binding.toolbarId.toolbarNameTv.text = getString(R.string.news)
        binding.toolbarId.backArrow.setOnClickListener{
            onBackPressed()
        }


        binding.shimmerFrameLayout?.viewVisible()
        if (intent.hasExtra("link")) {
            val mLoadUrl = intent.getStringExtra("link")
            val webSettings: WebSettings = binding.newsWebView!!.settings
            webSettings.javaScriptEnabled = true
            if (mLoadUrl != null) {
                binding.newsWebView?.loadUrl(mLoadUrl)
            }
        }
        binding.newsWebView?.webViewClient = WebViewClient(this)

//        setData()
//        binding.detailId.handleUrlClicks()
//        binding.detailId.movementMethod = LinkMovementMethod.getInstance()

//        binding.mImageView.setOnClickListener {
//            lifecycleScope.launch {
//                val image = intent.getStringExtra("image")
//                if (image != null) {
//                    SharePreferenceHelper.getInstance(this@NewsDetailActivity).saveFileName(image)
//                }
//            }
//            showFilePopup()
//        }

    }

    override fun onLoadCancel() {
        binding.shimmerFrameLayout?.viewGone()
        binding.newsWebView?.viewVisible()
    }

//    private fun setData(){
//        val date =  intent.getStringExtra("date")
//        val title =  intent.getStringExtra("title")
//        val detail =  intent.getStringExtra("detail")
//        val image = intent.getStringExtra("image")
//        binding.dateId.text = date?.toDate()?.formatTo("MMM dd, yyyy")
//        if (title != null) {
//            binding.titleId.setNewHtmlText(title)
//        }
//        if (detail != null) {
//            binding.detailId.setNewHtmlText(detail)
//        }
//
//        Glide.with(this)
//            .load(image)
//            .placeholder(R.drawable.dummy5)
//            .into(binding.mImageView)
//    }
}

private class WebViewClient(private val listener: itemClickListener) : android.webkit.WebViewClient()
{
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

    interface itemClickListener {
        fun onLoadCancel()
    }
}