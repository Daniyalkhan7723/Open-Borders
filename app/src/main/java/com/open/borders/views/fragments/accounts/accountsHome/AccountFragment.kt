package com.open.borders.views.fragments.accounts.accountsHome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.customPopups.DeleteAccountConfirmationPopUp
import com.open.borders.customPopups.LogOutConfirmationPopUp
import com.open.borders.databinding.FragmentAccountBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.setupProgressDialog
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.koinDIModules.AccountViewModelModule
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.account.aboutUs.AboutUsActivity
import com.open.borders.views.activities.account.accountDetail.AccountDetailActivity
import com.open.borders.views.activities.account.paymentDetail.PaymentDetailActivity
import com.open.borders.views.activities.account.servicesHistory.ServicesHistoryActivity
import com.open.borders.views.activities.navigationHost.AuthenticationHost
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class AccountFragment : MainMVVMNavigationFragment<AccountViewModel>(AccountViewModel::class),
    LogOutConfirmationPopUp.LogoutInterface, DeleteAccountConfirmationPopUp.DeleteAccountInterface {

    private lateinit var binding: FragmentAccountBinding
    override fun registerModule(): Module {
        return AccountViewModelModule
    }

    override fun getLayoutResId() = R.layout.fragment_account

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        toolbarControl()
        attachViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        logScreenName("Account")
        val user = SharePreferenceHelper.getInstance(requireContext()).getUser()
        if (user.is_guest != 0) {
            binding.servicesHistoryId.viewGone()
            binding.accountDetailId.viewGone()
            binding.llLogout.viewGone()
            binding.llDeleteAccount.viewGone()
            binding.signUp.viewVisible()
        }

        binding.servicesHistoryId.setOnClickListener {
            val myIntent = Intent(activity, ServicesHistoryActivity::class.java)
            startActivity(myIntent)
        }

        binding.accountDetailId.setOnClickListener {
            val myIntent = Intent(activity, AccountDetailActivity::class.java)
            startActivity(myIntent)
        }

        binding.updateYourPayment?.setOnClickListener {
            if (user.is_guest != 0) {
                val popup = LogOutConfirmationPopUp(this, "updatePayment")
                popup.show(childFragmentManager, "LogoutConfirmationPopUp")
            } else {
                val myIntent = Intent(activity, PaymentDetailActivity::class.java)
                startActivity(myIntent)
            }
        }

        binding.llLogout.setOnClickListener {
            val popup = LogOutConfirmationPopUp(this, "logout")
            popup.show(childFragmentManager, "LogoutConfirmationPopUp")
        }

        binding.llDeleteAccount.setOnClickListener {
            val popup = DeleteAccountConfirmationPopUp(this)
            popup.show(childFragmentManager, "DeleteAccountPopUp")
        }

        binding.aboutUs?.setOnClickListener {
            val myIntent = Intent(activity, AboutUsActivity::class.java)
//            myIntent.putExtra("isFromAccount", true)
            startActivity(myIntent)
        }

        binding.signUp.setOnClickListener {
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTabType("")
            }
            val myIntent = Intent(activity, AuthenticationHost::class.java)
            startActivity(myIntent)
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }

        observe(viewModel.logoutLiveData) {
            if (it.status) {

                val questions = SharePreferenceHelper.getInstance(requireContext()).getQuestion()
                val qToken =
                    SharePreferenceHelper.getInstance(requireContext()).getQuestionAuthToken()
                val terms =
                    SharePreferenceHelper.getInstance(requireContext()).getTermAndCondition()
                val termsEs =
                    SharePreferenceHelper.getInstance(requireContext()).getTermAndConditionEs()
                mActivity.sharedPreference?.clearUserData()
                lifecycleScope.launch {
                    mActivity.sharedPreference?.isFirstTime(false)
                    mActivity.sharedPreference?.saveQuestionAuthToken(qToken.toString())
                    mActivity.sharedPreference?.saveTermsAndCondition(terms.toString())
                    mActivity.sharedPreference?.saveTermsAndConditionEs(termsEs.toString())
                    mActivity.sharedPreference?.saveQuestion(questions)
                }
                val intent = Intent(requireContext(), AuthenticationHost::class.java)
                startActivity(intent)
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }

        observe(viewModel.deleteAccountLiveData) {
            if (it.status) {
                val questions = SharePreferenceHelper.getInstance(requireContext()).getQuestion()

                val qToken =
                    SharePreferenceHelper.getInstance(requireContext()).getQuestionAuthToken()
                val terms =
                    SharePreferenceHelper.getInstance(requireContext()).getTermAndCondition()
                val termsEs =
                    SharePreferenceHelper.getInstance(requireContext()).getTermAndConditionEs()
                mActivity.sharedPreference?.clearUserData()
                lifecycleScope.launch {
                    mActivity.sharedPreference?.isFirstTime(false)
                    mActivity.sharedPreference?.saveQuestionAuthToken(qToken.toString())
                    mActivity.sharedPreference?.saveTermsAndCondition(terms.toString())
                    mActivity.sharedPreference?.saveTermsAndConditionEs(termsEs.toString())
                    mActivity.sharedPreference?.checkToast(false)
                    mActivity.sharedPreference?.saveQuestion(questions)

                }

                val intent = Intent(requireContext(), AuthenticationHost::class.java)
                intent.putExtra("deleteAccount", it.message)
                startActivity(intent)

            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }
    }

    override fun onPopUpYesClick(type: String) {
        if (type == "updatePayment") {
            val myIntent = Intent(activity, PaymentDetailActivity::class.java)
            startActivity(myIntent)
        } else {
            if (isNetworkAvailable(requireContext())) {
                viewModel.logOutUser()
            } else {
                CustomToasts.showToast(
                    requireActivity(),
                    Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                    false
                )
            }
        }

    }

    override fun onPopUpNoClick(type: String) {


    }


    override fun onPopUpDeleteAccountYesClick() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.deleteAccount()
        } else {
            CustomToasts.showToast(
                requireActivity(),
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        val homeActivity = (activity as HomeHost).binding.homeToolbarId
        homeActivity.toolbarNameTv.text = getString(R.string.account)
        homeActivity.backArrow.viewGone()

        homeActivity.llChangeLanguage.setOnClickListener {
            (activity as HomeHost).changeLanguageMenu()
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTabType("Account")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logScreenName("Account")
        toolbarControl()
    }

}