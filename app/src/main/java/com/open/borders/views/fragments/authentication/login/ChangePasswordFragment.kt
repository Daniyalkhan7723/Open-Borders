package com.open.borders.views.fragments.authentication.login

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.FragmentChangePasswordBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.LoginViewModelModule
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class ChangePasswordFragment : MainMVVMNavigationFragment<LoginViewModel>(LoginViewModel::class) {

    private var password = ""
    private var confirm_password = ""

    private lateinit var binding: FragmentChangePasswordBinding
    private val navArgs: ChangePasswordFragmentArgs by navArgs()

    override fun registerModule(): Module {
        return LoginViewModelModule
    }

    override fun getLayoutResId() = R.layout.fragment_change_password

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChangePasswordBinding.bind(view)
        toolbarControl()
        setEditTexts()
        passwordEyeListener()

        binding.resetBtnId.setOnClickListener {
            validation()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private fun validation(): Boolean {

        password = binding.passwordEt.text.toString()
        confirm_password = binding.confirmPasswordEt.text.toString()

        if (password.isEmpty()) {
            binding.passwordEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorPasswordTv.viewVisible()
            binding.errorPasswordTv.text = "Please enter password"
            binding.passwordEt.requestFocus()
        } else if (password.length < 6) {
            binding.passwordEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorPasswordTv.viewVisible()
            binding.errorPasswordTv.text = "Please enter minimum 8 characters long password"
            binding.passwordEt.requestFocus()
        } else if (confirm_password.isEmpty()) {
            binding.confirmPasswordEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorConfirmPasswordTv.viewVisible()
            binding.errorConfirmPasswordTv.text = "Password and confirm password does not match"
            binding.confirmPasswordEt.requestFocus()
        } else if (confirm_password.length < 6) {
            binding.confirmPasswordEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorConfirmPasswordTv.viewVisible()
            binding.errorConfirmPasswordTv.text = "Password and confirm password does not match"
            binding.confirmPasswordEt.requestFocus()
        } else if (password != confirm_password) {
            binding.confirmPasswordEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorConfirmPasswordTv.viewVisible()
            binding.errorConfirmPasswordTv.text = "Password and confirm password does not match"
            binding.confirmPasswordEt.requestFocus()
        } else {
            if (isNetworkAvailable(requireContext())) {
                viewModel.recoverPassword(
                    navArgs.email.toString(),
                    password,
                    confirm_password
                )
            } else {
               CustomToasts.showToast(requireActivity(), Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false)
            }
        }
        return true
    }


    private fun setEditTexts() {
        binding.passwordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.passwordEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorPasswordTv.error = null
                binding.errorPasswordTv.viewGone()
            }
        })

        binding.confirmPasswordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.confirmPasswordEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorConfirmPasswordTv.error = null
                binding.errorConfirmPasswordTv.viewGone()
            }
        })
    }

    private fun passwordEyeListener() {
        binding.eyePassword.setOnClickListener() {
            hideShowPassword(
                binding.passwordEt,
                binding.eyePassword
            )
        }

        binding.confirmPasswordEye.setOnClickListener() {
            hideShowPassword(
                binding.confirmPasswordEt,
                binding.confirmPasswordEye
            )
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }

        observe(viewModel.recoverPasswordLiveData) {
            if (it.status) {
                findNavController().navigate(
                    ChangePasswordFragmentDirections.actionChangePasswordFragmentToLoginFragment()
                )
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl(){
        binding.authToolbarId.toolbarNameTv.text = "Change Password"
        binding.authToolbarId.backArrow.setOnClickListener{
            findNavController().navigateUp()
        }
    }

}