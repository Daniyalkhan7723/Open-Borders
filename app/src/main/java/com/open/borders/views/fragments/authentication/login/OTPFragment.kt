package com.open.borders.views.fragments.authentication.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.FragmentOTPBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.setupProgressDialog
import com.open.borders.koinDIModules.LoginViewModelModule
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class OTPFragment : MainMVVMNavigationFragment<LoginViewModel>(LoginViewModel::class) {
    private lateinit var binding: FragmentOTPBinding
    private val navArgs: OTPFragmentArgs by navArgs()

    override fun getLayoutResId() = R.layout.fragment_o_t_p

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
        binding = FragmentOTPBinding.bind(view)
        binding.tvOtpEmail?.text = navArgs.email
        toolbarControl()
        initListener()
        sendCodeApiCall()
        resendCode()
    }

    private fun sendCodeApiCall() {
        //Calling Send Code Api
        if (isNetworkAvailable(requireContext())) {
            if (Constants.SIGN_UP_FLAG || Constants.LOG_IN_FLAG) {
                viewModel.sendOTPCode(navArgs.email.toString())
            } else {
                viewModel.forgetPassword(navArgs.email.toString())
            }
        } else {
            showAlertDialog(Constants.INTERNET_CONNECTION_ERROR_MESSAGE)
        }
    }


    private fun initListener() {
        binding.etCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null)
                    setOtpNumbers(otp = s.toString())
                else
                    setOtpNumbers(otp = "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.btnVerifyCode.setOnClickListener {
            val otpCode = binding.etCode.text.toString()
            if (isNetworkAvailable(requireContext())) {
                viewModel.verifyCode(navArgs.email.toString(), otpCode)
            } else {
                showAlertDialog(Constants.INTERNET_CONNECTION_ERROR_MESSAGE)
            }
        }
    }


    private fun setOtpNumbers(otp: String) {
        when {
            otp.isEmpty() -> {
                binding.tvDigit1.text = ""
                binding.tvDigit2.text = ""
                binding.tvDigit3.text = ""
                binding.tvDigit4.text = ""
            }
            otp.length == 1 -> {
                binding.tvDigit1.text = otp
                binding.tvDigit2.text = ""
                binding.tvDigit3.text = ""
                binding.tvDigit4.text = ""
            }
            otp.length == 2 -> {
                binding.tvDigit1.text = otp.substring(0, 1)
                binding.tvDigit2.text = otp.substring(1, 2)
                binding.tvDigit3.text = ""
                binding.tvDigit4.text = ""
            }
            otp.length == 3 -> {
                binding.tvDigit1.text = otp.substring(0, 1)
                binding.tvDigit2.text = otp.substring(1, 2)
                binding.tvDigit3.text = otp.substring(2, 3)
                binding.tvDigit4.text = ""
            }
            otp.length == 4 -> {
                binding.tvDigit1.text = otp.substring(0, 1)
                binding.tvDigit2.text = otp.substring(1, 2)
                binding.tvDigit3.text = otp.substring(2, 3)
                binding.tvDigit4.text = otp.substring(3, 4)
            }
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }

        observe(viewModel.sendOTPCodeLiveData) {
            if (it.status) {
                CustomToasts.basicToast(requireContext(), getString(R.string.otp_code_sent_successfully))
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }

        observe(viewModel.verifyOTPCodeLiveData) {
            if (it.status) {
                CustomToasts.showToast(requireActivity(), it.message, true)
                if (Constants.SIGN_UP_FLAG) {
                    findNavController().navigate(OTPFragmentDirections.actionOTPFragmentToLoginFragment())
                    Constants.SIGN_UP_FLAG = false
                } else if (Constants.LOG_IN_FLAG) {
                    val intent = Intent(requireContext(), HomeHost::class.java)
                    startActivity(intent)
                    Constants.LOG_IN_FLAG = false
                } else {
                    findNavController().navigate(
                        OTPFragmentDirections.actionOTPFragmentToChangePasswordFragment(navArgs.email)
                    )
                }
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }

        observe(viewModel.forgotPasswordLiveData) {
            if (it.status) {
                CustomToasts.showToast(requireActivity(), getString(R.string.otp_code_sent_successfully), true)
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        binding.authToolbarId.toolbarNameTv.text =  getString(R.string.enter_verification_code)
        binding.authToolbarId.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun resendCode() {
        binding.tvResendCode?.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                viewModel.sendOTPCode(navArgs.email.toString())
            } else {
                CustomToasts.showToast(
                    requireActivity(),
                    Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                    false
                )
            }
        }
    }
}