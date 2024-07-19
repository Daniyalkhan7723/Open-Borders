package com.open.borders.views.activities

import android.os.Bundle
import com.open.borders.databinding.ActivityMainBinding
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.extensions.setEnglish
import com.open.borders.extensions.setSpanish
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvHello.setOnClickListener {
            if (this.getCurrentLanguage() == "es") {
                this.setEnglish()
            } else {
                this.setSpanish()
            }
        }
    }
}