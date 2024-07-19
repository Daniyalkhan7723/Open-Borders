package com.open.borders.views.activities.account.accountDetail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.ActivityAccountDetailBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.setupProgressDialog
import com.open.borders.extensions.viewGone
import com.open.borders.koinDIModules.AccountDetailViewModelModule
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.views.activities.baseActivity.MainMVVMBaseActivity
import kotlinx.serialization.InternalSerializationApi
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import org.koin.core.module.Module
import com.open.borders.extensions.parseCountryCode

@InternalSerializationApi
class AccountDetailActivity :
    MainMVVMBaseActivity<AccountDetailViewModel>(AccountDetailViewModel::class) {
    private lateinit var binding: ActivityAccountDetailBinding
    private var phoneNumber = ""

    override fun registerModule(): Module {
        return AccountDetailViewModelModule
    }

    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityAccountDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logScreenName("Profile Details")
        binding.toolbarId.toolbarNameTv.text = getString(R.string.account_detail)
        binding.toolbarId.llChangeLanguage.viewGone()

        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
//        phoneNumberPicker()
        getProfileApiCall()
        backPress()

        binding.buttonDone.setOnClickListener {
            updateProfileApiCall()
        }

        binding.countryName.setDialogKeyboardAutoPopup(false)
        binding.countryCode.setDialogKeyboardAutoPopup(false)

    }

    //Get Profile Api Call
    private fun getProfileApiCall() {
        if (isNetworkAvailable(this)) {
            viewModel.getProfile()
        } else {
            CustomToasts.showToast(
                this,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }

    }

    //Update Profile Api Call
    private fun updateProfileApiCall() {
        val firstName = binding.fistNameEt.text.toString()
        val lastName = binding.lastNameEt.text.toString()
        val email = binding.emailEt.text.toString()
        val phoneNo = binding.etPhone.unMaskedText.toString()
        val streetAddress = binding.streetAddressEt.text.toString()
        val country = binding.countryName.selectedCountryName.toString()
        val countryName = binding.countryName.selectedCountryNameCode.toString()
        val state = binding.stateEt.text.toString()
        val city = binding.cityEt.text.toString()
        val zipCode = binding.zipEt.text.toString()
        val firstNameUs = binding.fistNameEtUS?.text.toString()
        val lastNameUs = binding.lastNameEtUs?.text.toString()
        val emailUs = binding.signupEmailTvUs?.text.toString()

        if (isNetworkAvailable(this)) {
            viewModel.updateProfile(
                firstName.toRequestBody(),
                lastName.toRequestBody(),
                email.toRequestBody(),
                phoneNo.toRequestBody(),
                streetAddress.toRequestBody(),
                country.toRequestBody(),
                state.toRequestBody(),
                city.toRequestBody(),
                zipCode.toRequestBody(),
                countryName.toRequestBody(),
                firstNameUs.toRequestBody(),
                lastNameUs.toRequestBody(),
                emailUs.toRequestBody()
            )
        } else {
            CustomToasts.showToast(
                this,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(this, msg, false)
            }
        }
        observe(viewModel.getProfileLiveData) {
            if (it.status) {
                binding.fistNameEt.setText(it.data?.user?.first_name)
                binding.lastNameEt.setText(it.data?.user?.last_name)
                binding.fistNameEtUS?.setText(it.data?.user?.petitioner_detail?.first_name)
                binding.lastNameEtUs?.setText(it.data?.user?.petitioner_detail?.last_name)
                binding.signupEmailTvUs?.setText(it.data?.user?.petitioner_detail?.email)
                phoneNumber = it.data?.user?.phone_no.toString()
                phoneNumberPicker()
                binding.emailEt.setText(it.data?.user?.email)
                binding.streetAddressEt.setText(it.data?.user?.user_address?.street_address)
                binding.cityEt.setText(it.data?.user?.user_address?.city)
                binding.stateEt.setText(it.data?.user?.user_address?.state)
                binding.zipEt.setText(it.data?.user?.user_address?.zip_code)
                binding.countryName.setCountryForNameCode(it.data?.user?.user_address?.country_name_code)
//
            }
            observe(viewModel.updateProfileLiveData) {
                if (it.status) {
                    CustomToasts.showToast(this, it.message, true)
//                    getProfileApiCall()
                } else {
                    CustomToasts.showToast(this, it.message, false)
                }
            }
        }
    }

    private fun backPress() {
        binding.toolbarId.backArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun phoneNumberPicker() {
//       var selectedCountryCode = binding.countryCode?.selectedCountryCode.toString()
        var code = if (phoneNumber.contains("+")) {
            (phoneNumber).parseCountryCode()
        } else {
            (phoneNumber).parseCountryCode()
        }
        val newNumber = phoneNumber.replace("+$code", "")
        binding.etPhone.setText(newNumber)
        try {
            if (code.contains("+")) {
                code = code.replace("+", "")
                binding.countryCode.setCountryForPhoneCode(code.toInt())
            } else {
                binding.countryCode.setCountryForPhoneCode(code.toInt())
            }
        } catch (e: Exception) {
            CustomToasts.showToast(this, e.toString(), false)
        }
    }

}
