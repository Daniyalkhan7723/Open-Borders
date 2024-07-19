package com.open.borders.views.activities.navigationHost

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.open.borders.R
import com.open.borders.databinding.ActivityAuthenticationHostBinding
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class AuthenticationHost : BaseActivity() {

    var navController: NavController? = null
    private var navHostFragment: NavHostFragment? = null
    private lateinit var binding: ActivityAuthenticationHostBinding
    private var doubleBackToExitPressedOnce = false
    private lateinit var sharePreferenceHelper: SharePreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharePreferenceHelper = SharePreferenceHelper.getInstance(this@AuthenticationHost)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.auth_nav_host_fragment_id) as NavHostFragment

        val inflater = navHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(R.navigation.authentication_graph)

        graph?.startDestination = R.id.loginFragment
        graph?.let {
            navHostFragment?.navController?.graph = it
        }
        navController = findNavController(R.id.auth_nav_host_fragment_id)

        when (intent.getIntExtra("fromGuestPopUp", 0)) {
            1 -> {
                navController!!.navigate(R.id.signUpFragment)
            }
            else -> {
                val bundle = Bundle()
                bundle.putString("deleteAccounting", intent.getStringExtra("deleteAccount"))
                navController!!.navigate(R.id.loginFragment,bundle)
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onKeyDown(keyCode: Int, event: KeyEvent?) = if (keyCode == KeyEvent.KEYCODE_BACK) {
        val currentDestination = navController?.currentDestination
        if (currentDestination != null && currentDestination.id == R.id.loginFragment) {
            doubleBackToExitPressedOnce = if (doubleBackToExitPressedOnce) {
                moveTaskToBack(true)
                false
            } else {
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
                true
            }
            true
        } else if (currentDestination != null && currentDestination.id != R.id.loginFragment) {
            navController?.navigateUp()
            true

        } else {
            lifecycleScope.launch {
                sharePreferenceHelper.checkToast(true)
            }
            super.onBackPressed()
            false
        }
    } else false

    override fun onBackPressed() = Unit


}