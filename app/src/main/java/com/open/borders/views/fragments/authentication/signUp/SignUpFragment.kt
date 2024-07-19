package com.open.borders.views.fragments.authentication.signUp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.customPopups.TermsAndConditionPopUp
import com.open.borders.databinding.SignUpFragmentBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.signUpViewModelModule
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class SignUpFragment : MainMVVMNavigationFragment<SignUpViewModel>(SignUpViewModel::class) {

    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var firstNameUs = ""
    private var lastNameUs = ""
    private var emailUs = ""
    private var password = ""
    private var confirm_password = ""
    private var phoneNo = ""
    private var streetAddress = ""
    private var country = ""
    private var city = ""
    private var state = ""
    private var zipCode = ""
    private var currentCountryCode = ""
    private var countryNameCode = ""

    private lateinit var binding: SignUpFragmentBinding

    override fun getLayoutResId() = R.layout.sign_up_fragment

    override fun registerModule(): Module {
        return signUpViewModelModule
    }

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignUpFragmentBinding.bind(view)
        logScreenName("Sign Up")
        currentCountryCode = binding.countryList.selectedCountryCodeWithPlus.toString()
        binding.countryName.setDialogKeyboardAutoPopup(false)
        binding.countryList.setDialogKeyboardAutoPopup(false)
        country = binding.countryName.selectedCountryName.toString()
        countryNameCode = binding.countryName.selectedCountryNameCode.toString()

        setEditTexts()
        passwordEyeListener()

        binding.signUpTOLogin.setOnClickListener {
            findNavController().navigate(
                SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
            )
        }

        binding.buttonSignUp.setOnClickListener {
            validation()
        }

//        binding.reviewTermsAndCondition?.makeLinks(
//            Pair(getString(R.string.click_here), View.OnClickListener {
////                Constants.termsFromSignUp = true

        binding.termAndConditionLayout?.setOnClickListener {
            binding.termsCheck.isClickable = false
            binding.termsCheck.isChecked = false

            Constants.termsFromSignUp = true

            val popup = TermsAndConditionPopUp(mActivity, object :
                TermsAndConditionPopUp.TermAndCheckInterface {
                override fun onCheckClickYes() {

                    binding.termsCheck.isChecked = true
                }
            })
            popup.show(childFragmentManager, "")
        }
    }


    @SuppressLint("SetTextI18n")
    private fun validation(): Boolean {
        firstName = binding.fistNameEt.text.toString()
        lastName = binding.lastNameEt.text.toString()
        email = binding.signupEmailTv.text.toString()
        firstNameUs = binding.fistNameEtUS?.text.toString()
        lastNameUs = binding.lastNameEtUs?.text.toString()
        emailUs = binding.signupEmailTvUs?.text.toString()
        password = binding.signupPasswordTv.text.toString()
        confirm_password = binding.signupConfirmPasswordTv.text.toString()
        phoneNo = binding.etPhone.unMaskedText.toString()

        streetAddress = binding.streetAddressEt.text.toString()
        country = binding.countryName.selectedCountryName.toString()
        city = binding.cityEt.text.toString()
        state = binding.stateEt?.text.toString()
        zipCode = binding.zipEt.text.toString()
        countryNameCode = binding.countryName.selectedCountryNameCode.toString()


        if (firstName.isEmpty()) {
            binding.fistNameEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorFirstNameTv.viewVisible()
            binding.errorFirstNameTv.text = getString(R.string.please_enter_firstName)
            binding.fistNameEt.requestFocus()
        }

        if (lastName.isEmpty()) {
            binding.lastNameEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorLastNameTv.viewVisible()
            binding.errorLastNameTv.text = getString(R.string.please_enter_lastName)
            binding.lastNameEt.requestFocus()
        } else if (email.isEmpty()) {
            binding.signupEmailTv.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorEmailTv.viewVisible()
            binding.errorEmailTv.text = getString(R.string.please_enter_email)
            binding.signupEmailTv.requestFocus()
        } else if (isValidEmail(email).not()) {
            binding.signupEmailTv.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorEmailTv.viewVisible()
            binding.errorEmailTv.text = getString(R.string.please_enter_valid_email_address)
            binding.signupEmailTv.requestFocus()
        }

        else if (password.isEmpty()) {
            binding.signupPasswordTv.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorPasswordTv.viewVisible()
            binding.errorPasswordTv.text = getString(R.string.please_enter_password)
            binding.signupPasswordTv.requestFocus()
        }
        else if (password.length < 8) {
            binding.signupPasswordTv.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorPasswordTv.viewVisible()
            binding.errorPasswordTv.text =
                getString(R.string.please_enter_minimum_8_characters_long_password)
            binding.signupPasswordTv.requestFocus()
        }
        else if (confirm_password.isEmpty()) {
            binding.signupConfirmPasswordTv.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorConfirmPasswordTv.viewVisible()
            binding.errorConfirmPasswordTv.text =
                getString(R.string.password_and_confirm_password_does_not_match)
            binding.signupConfirmPasswordTv.requestFocus()
        }
        else if (confirm_password.length < 8) {
            binding.signupConfirmPasswordTv.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorConfirmPasswordTv.viewVisible()
            binding.errorConfirmPasswordTv.text =
                getString(R.string.password_and_confirm_password_does_not_match)
            binding.signupConfirmPasswordTv.requestFocus()
        }
        else if (password != confirm_password) {
            binding.signupConfirmPasswordTv.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorConfirmPasswordTv.viewVisible()
            binding.errorConfirmPasswordTv.text =
                getString(R.string.password_and_confirm_password_does_not_match)
            binding.signupConfirmPasswordTv.requestFocus()
        }
        else if (phoneNo.isEmpty()) {
            binding.etPhone.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.phoneErrorTv.viewVisible()
            binding.phoneErrorTv.text = getString(R.string.please_enter_phoneNo)
            binding.etPhone.requestFocus()
        }
        else if (phoneNo.length < 10) {
            binding.etPhone.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.phoneErrorTv.viewVisible()
            binding.phoneErrorTv.text = getString(R.string.please_enter_phoneNo_characters)
            binding.etPhone.requestFocus()
        }
        else if (streetAddress.isEmpty()) {
            binding.streetAddressEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorStreetTv.viewVisible()
            binding.errorStreetTv.text = getString(R.string.please_enter_street)
            binding.streetAddressEt.requestFocus()
        }
        else if (city.isEmpty()) {
            binding.cityEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorCityTv.viewVisible()
            binding.errorCityTv.text = getString(R.string.please_enter_cityName)
            binding.cityEt.requestFocus()
        }

        else if (state.isEmpty()) {
            binding.stateEt?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorStateTv?.viewVisible()
            binding.errorStateTv?.text = getString(R.string.please_enter_state)
            binding.stateEt?.requestFocus()
        }

        else if (zipCode.isEmpty()) {
            binding.zipEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorZipTv.viewVisible()
            binding.errorZipTv.text = getString(R.string.please_enter_zip)
            binding.zipEt.requestFocus()
        }
        if (country.isEmpty()) {
            binding.countryName.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorCountry.viewVisible()
            binding.errorCountry.text = getString(R.string.please_enter_country)
            binding.countryName.requestFocus()
        }
        else if (!binding.termsCheck.isChecked)
            CustomToasts.showToast(
                requireActivity(),
                getString(R.string.please_check_terms_and_conditions),
                false
            )
        else {
            val user = SharePreferenceHelper.getInstance(requireContext()).getUser()

            if (user.id != null) {
                if (isNetworkAvailable(requireContext())) {
                    viewModel.updateGuestUserSignUp(
                        user.id.toString(),
                        firstName,
                        lastName,
                        email,
                        password,
                        confirm_password,
                        currentCountryCode + binding.etPhone.unMaskedText.toString(),
                        "",
                        streetAddress,
                        country,
                        city,
                        state,
                        zipCode,
                        countryNameCode,
                        firstNameUs,
                        lastNameUs,
                        emailUs,
                    )
                }
                else {
                    CustomToasts.showToast(
                        requireActivity(),
                        Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                        false
                    )
                }
            }
            else {
                if (isNetworkAvailable(requireContext())) {
                    viewModel.signUpUser(
                        firstName,
                        lastName,
                        email,
                        password,
                        confirm_password,
                        currentCountryCode + binding.etPhone.unMaskedText.toString(),
                        "",
                        streetAddress,
                        country,
                        city,
                        state,
                        zipCode,
                        countryNameCode,
                        firstNameUs,
                        lastNameUs,
                        emailUs,
                    )
                } else {
                    CustomToasts.showToast(
                        requireActivity(),
                        Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                        false
                    )
                }
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

        observe(viewModel.userSignUpLiveData) {
            if (it.status) {
                CustomToasts.showToast(requireActivity(), it.message, true)
                Constants.SIGN_UP_FLAG = true
                val intent = Intent(requireContext(), HomeHost::class.java)
                startActivity(intent)
//                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToOTPFragment(email))
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }
    }

    private fun setEditTexts() {
//        val formatter = MaskedFormatter("your_mask")
//        binding.etPhone?.addTextChangedListener(MaskedWatcher(formatter, binding.etPhone!!))

        binding.fistNameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.fistNameEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorFirstNameTv.error = null
                binding.errorFirstNameTv.viewGone()
            }
        })

        binding.signupEmailTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.signupEmailTv.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorEmailTv.error = null
                binding.errorEmailTv.viewGone()
            }
        })

        binding.signupPasswordTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.signupPasswordTv.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorPasswordTv.error = null
                binding.errorPasswordTv.viewGone()
            }
        })

        binding.signupConfirmPasswordTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.signupConfirmPasswordTv.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorConfirmPasswordTv.error = null
                binding.errorConfirmPasswordTv.viewGone()
            }
        })

        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etPhone!!.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.phoneErrorTv?.error = null
                binding.phoneErrorTv?.viewGone()
            }
        })

        binding.streetAddressEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.streetAddressEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorStreetTv.error = null
                binding.errorStreetTv.viewGone()
            }
        })

        binding.cityEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.cityEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorCityTv.error = null
                binding.errorCityTv.viewGone()
            }
        })

        binding.zipEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.zipEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorZipTv.error = null
                binding.errorZipTv.viewGone()
            }
        })

//        binding.countryName?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                binding.countryEt!!.backgroundTintList =
//                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
//                binding.errorCountry?.error = null
//                binding.errorCountry?.viewGone()
//            }
//        })
    }

    private fun passwordEyeListener() {
        binding.signUpPasswordEye.setOnClickListener() {
            hideShowPassword(
                binding.signupPasswordTv,
                binding.signUpPasswordEye
            )
        }

        binding.signUpConfirmPasswordEye.setOnClickListener() {
            hideShowPassword(
                binding.signupConfirmPasswordTv,
                binding.signUpConfirmPasswordEye
            )
        }
    }

    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
//                    textPaint.color = textPaint.linkColor
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text

            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    override fun onResume() {
        super.onResume()
        logScreenName("Sign Up")
    }

}
