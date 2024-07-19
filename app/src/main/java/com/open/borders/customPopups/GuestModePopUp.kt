package com.open.borders.customPopups

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.PopupGuestModeBinding
import com.open.borders.extensions.hideShowPassword
import com.open.borders.extensions.isValidEmail
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible


class GuestModePopUp(
    private var listener: GuestModeCallBack,
    private var name: String?,
    private var goToLoginListener: GoToLogin
) : DialogFragment() {

    private lateinit var binding: PopupGuestModeBinding
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var phoneNo = ""
    private var currentCountryCode = ""
    private var firstNameUs = ""
    private var lastNameUs = ""
    private var emailUs = ""
    private var password = ""
    private var confirm_password = ""
    private var is_Login = 0
    private var countryNameCode = ""
    private var streetAddress = ""
    private var country = ""
    private var city = ""
    private var state = ""
    private var zipCode = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.popup_guest_mode, container, false)
        binding = PopupGuestModeBinding.bind(view)
        currentCountryCode = binding.countryList.selectedCountryCodeWithPlus.toString()


        binding.ivCancel?.setOnClickListener {
            dismiss()
        }
        countryNameCode = binding.countryName?.selectedCountryNameCode.toString()

        binding.title?.text = name
        setEditTexts()
        passwordEyeListener()


        binding.signUpTOLogin.setOnClickListener {
            goToLoginListener.onGoToLoginScreen()
        }


        binding.termsCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                is_Login = 1
                binding.passwordLayout?.viewVisible()
            } else {
                is_Login = 0
                binding.passwordLayout?.viewGone()
            }
        }

        binding.buttonSignUp.setOnClickListener {
            validation()
        }

        return view
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

        binding.lastNameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.lastNameEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorLastNameTv.error = null
                binding.errorLastNameTv.viewGone()
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

        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etPhone!!.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.erroretPhoneTv?.error = null
                binding.erroretPhoneTv?.viewGone()
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

        binding.streetAddressEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding!!.streetAddressEt?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorStreetTv?.error = null
                binding.errorStreetTv?.viewGone()
            }
        })

        binding.cityEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding!!.cityEt?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorCityTv?.error = null
                binding.errorCityTv?.viewGone()
            }
        })

        binding.stateEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding!!.stateEt?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorStateTv?.error = null
                binding.errorStateTv?.viewGone()
            }
        })

        binding.zipEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding!!.zipEt?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorZipTv?.error = null
                binding.errorZipTv?.viewGone()
            }
        })


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


    @SuppressLint("SetTextI18n")
    private fun validation(): Boolean {
        firstName = binding.fistNameEt.text.toString()
        lastName = binding.lastNameEt.text.toString()
        email = binding.signupEmailTv.text.toString()
        phoneNo = binding.etPhone.unMaskedText.toString()

        streetAddress = binding.streetAddressEt?.text.toString()
        country = binding.countryName?.selectedCountryName.toString()
        city = binding.cityEt?.text.toString()
        state = binding.stateEt?.text.toString()
        zipCode = binding.zipEt?.text.toString()

        firstNameUs = binding.fistNameEtUS?.text.toString()
        lastNameUs = binding.lastNameEtUs?.text.toString()
        emailUs = binding.signupEmailTvUs?.text.toString()
        password = binding.signupPasswordTv.text.toString()
        confirm_password = binding.signupConfirmPasswordTv.text.toString()

        countryNameCode = binding.countryName?.selectedCountryNameCode.toString()


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
        }
        if (email.isEmpty()) {
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
        } else if (phoneNo.isEmpty()) {
            binding.etPhone.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.erroretPhoneTv?.viewVisible()
            binding.erroretPhoneTv?.text = getString(R.string.please_enter_phoneNo)
            binding.etPhone.requestFocus()
        } else if (phoneNo.length < 10) {
            binding.etPhone.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.erroretPhoneTv?.viewVisible()
            binding.erroretPhoneTv?.text = getString(R.string.please_enter_phoneNo_characters)
            binding.etPhone.requestFocus()
        } else if (streetAddress.isEmpty()) {
            binding.streetAddressEt?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorStreetTv?.viewVisible()
            binding.errorStreetTv?.text = getString(R.string.please_enter_street)
            binding.streetAddressEt?.requestFocus()
        } else if (city.isEmpty()) {
            binding.cityEt?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorCityTv?.viewVisible()
            binding.errorCityTv?.text = getString(R.string.please_enter_cityName)
            binding.cityEt?.requestFocus()
        } else if (state.isEmpty()) {
            binding.stateEt?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorStateTv?.viewVisible()
            binding.errorStateTv?.text = getString(R.string.please_enter_state)
            binding.stateEt?.requestFocus()
        } else if (zipCode.isEmpty()) {
            binding.zipEt?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorZipTv?.viewVisible()
            binding.errorZipTv?.text = getString(R.string.please_enter_zip)
            binding.zipEt?.requestFocus()
        } else if (country.isEmpty()) {
            binding.countryName?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorCountry?.viewVisible()
            binding.errorCountry?.text = getString(R.string.please_enter_country)
            binding.countryName?.requestFocus()
        } else {
            if (binding.termsCheck.isChecked) {
                if (password.isEmpty()) {
                    binding.signupPasswordTv.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                    binding.errorPasswordTv.viewVisible()
                    binding.errorPasswordTv.text = getString(R.string.please_enter_password)
                    binding.signupPasswordTv.requestFocus()
                } else if (password.length < 8) {
                    binding.signupPasswordTv.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                    binding.errorPasswordTv.viewVisible()
                    binding.errorPasswordTv.text =
                        getString(R.string.please_enter_minimum_8_characters_long_password)
                    binding.signupPasswordTv.requestFocus()
                } else if (confirm_password.isEmpty()) {
                    binding.signupConfirmPasswordTv.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                    binding.errorConfirmPasswordTv.viewVisible()
                    binding.errorConfirmPasswordTv.text =
                        getString(R.string.password_and_confirm_password_does_not_match)
                    binding.signupConfirmPasswordTv.requestFocus()
                } else if (confirm_password.length < 8) {
                    binding.signupConfirmPasswordTv.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                    binding.errorConfirmPasswordTv.viewVisible()
                    binding.errorConfirmPasswordTv.text =
                        getString(R.string.password_and_confirm_password_does_not_match)
                    binding.signupConfirmPasswordTv.requestFocus()
                } else if (password != confirm_password) {
                    binding.signupConfirmPasswordTv.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.red_color)
                    binding.errorConfirmPasswordTv.viewVisible()
                    binding.errorConfirmPasswordTv.text =
                        getString(R.string.password_and_confirm_password_does_not_match)
                    binding.signupConfirmPasswordTv.requestFocus()

                } else {
                    listener.onCheckSubmit(
                        firstName,
                        lastName,
                        email,
                        currentCountryCode + binding.etPhone.unMaskedText.toString(),

                        streetAddress,
                        country,
                        city,
                        state,
                        zipCode,

                        countryNameCode,

                        firstNameUs,
                        lastNameUs,
                        emailUs,
                        password,
                        confirm_password,
                        is_Login
                    )
                }
            }
            else {
                listener.onCheckSubmit(
                    firstName,
                    lastName,
                    email,
                    currentCountryCode + binding.etPhone.unMaskedText.toString(),

                    streetAddress,
                    country,
                    city,
                    state,
                    zipCode,

                    countryNameCode,

                    firstNameUs,
                    lastNameUs,
                    emailUs,
                    "",
                    "",
                    0
                )
            }
        }
        return true
    }

    override fun onResume() {
        val window: Window? = dialog!!.window
        val size = Point()
        // Store dimensions of the screen in `size`
        // Store dimensions of the screen in `size`
        val display: Display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        // Set the width of the dialog proportional to 75% of the screen width
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((size.x * 0.90).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        // Call super onResume after sizing
        // Call super onResume after sizing
        super.onResume()
    }

    interface GuestModeCallBack {
        fun onCheckSubmit(
            fName: String,
            lName: String,
            email: String,
            phoneNumber: String,

            street_address: String,
            country: String,
            city: String,
            state: String,
            zip_code: String,
            countryNameCode: String,

            Usfname: String,
            UsLastName: String,
            UsEmail: String,
            password: String,
            confirmPassword: String,
            is_Login: Int
        )
    }

    interface GoToLogin {
        fun onGoToLoginScreen()
    }

    fun showToast(context: Context, message: String) {
        CustomToasts.showToast(requireActivity(), message, false)

    }

}