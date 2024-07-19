package com.open.borders.views.fragments.authentication.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.LoginFragmentBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.LoginViewModelModule
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class LoginFragment : MainMVVMNavigationFragment<LoginViewModel>(LoginViewModel::class) {
    private var email = ""
    private var password = ""
    private var type: String? = null
    private lateinit var sharePreferenceHelper: SharePreferenceHelper
    private lateinit var binding: LoginFragmentBinding

    override fun getLayoutResId() = R.layout.login_fragment

    override fun registerModule(): Module {
        return LoginViewModelModule
    }

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        logScreenName("Sign In")
        setEditTexts()

        sharePreferenceHelper = SharePreferenceHelper.getInstance(mContext)


        type = arguments?.getString("deleteAccounting")
        if (type != null) {
            if (!viewModel.sharePreferenceHelper.isToast()) {
                CustomToasts.showToast(requireActivity(), type!!, false)
            }
        }

        binding.loginToSignupTv.setOnClickListener {
            lifecycleScope.launch {
                sharePreferenceHelper.checkToast(true)
            }
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            )
        }

        binding.guestButton.setOnClickListener {
            lifecycleScope.launch {
                sharePreferenceHelper.checkToast(true)
            }

            if (isNetworkAvailable(requireContext())) {
                viewModel.guestLogin()
            } else {
                CustomToasts.showToast(
                    requireActivity(),
                    Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                    false
                )
            }
        }

        binding.forgetPasswordTv.setOnClickListener {
            lifecycleScope.launch {
                sharePreferenceHelper.checkToast(true)
            }

            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToEnterEmailFragment()
            )
        }

        binding.buttonLogin.setOnClickListener {
            validation()
        }

        binding.loginEye.setOnClickListener {
            hideShowPassword(
                binding.loginPasswordEt,
                binding.loginEye
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private fun validation(): Boolean {
        email = binding.loginEmailEt.text.toString()
        password = binding.loginPasswordEt.text.toString()

        if (email.isEmpty()) {
            binding.loginEmailEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorEmailTv.viewVisible()
            binding.errorEmailTv.text = getString(R.string.please_enter_email)
            binding.loginEmailEt.requestFocus()
        } else if (isValidEmail(email).not()) {
            binding.loginEmailEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorEmailTv.viewVisible()
            binding.errorEmailTv.text = getString(R.string.please_enter_valid_email_address)
            binding.loginEmailEt.requestFocus()
        }
        else if (password.isEmpty()) {
            binding.loginPasswordEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorPasswordTv.viewVisible()
            binding.errorPasswordTv.text = getString(R.string.please_enter_password)
            binding.loginPasswordEt.requestFocus()
        } /*
         else if (password.length < 6) {
            binding.loginPasswordEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorPasswordTv.viewVisible()
            binding.errorPasswordTv.text = "Please enter valid credentials"
            binding.loginPasswordEt.requestFocus()
        }*/ else {
            if (isNetworkAvailable(requireContext())) {
                viewModel.loginUser(
                    email,
                    password
                )
            } else {
                CustomToasts.showToast(
                    requireActivity(),
                    Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                    false
                )
            }
        }
        return true
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }
        observe(viewModel.userLoginLiveData) {
            if (it.status) {
//                if (it.data?.user?.email_verified_at != null) {
//                    val intent = Intent(requireContext(), HomeHost::class.java)
//                    startActivity(intent)
//                }
//                else {
//                    Constants.LOG_IN_FLAG = true
//                    findNavController().navigate(
//                        LoginFragmentDirections.actionLoginFragmentToOTPFragment(email)
//                    )
//                }
                val intent = Intent(requireContext(), HomeHost::class.java)
                startActivity(intent)
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }

        observe(viewModel.guestLoginLiveData) {
            if (it.status) {
                val intent = Intent(requireContext(), HomeHost::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setEditTexts() {
        binding.loginEmailEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.loginEmailEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorEmailTv.error = null
                binding.errorEmailTv.viewGone()
            }
        })

        binding.loginPasswordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.loginPasswordEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorPasswordTv.error = null
                binding.errorPasswordTv.viewGone()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        logScreenName("Sign In")
    }

}