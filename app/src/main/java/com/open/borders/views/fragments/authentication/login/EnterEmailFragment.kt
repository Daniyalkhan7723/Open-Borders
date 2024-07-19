package com.open.borders.views.fragments.authentication.login

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.open.borders.R
import com.open.borders.databinding.FragmentEnterEmailBinding
import com.open.borders.extensions.isValidEmail
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.views.fragments.baseFragment.BaseFragment
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class EnterEmailFragment : BaseFragment() {

    private var email = ""

    private lateinit var binding: FragmentEnterEmailBinding

    override fun getLayoutResId() = R.layout.fragment_enter_email


    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEnterEmailBinding.bind(view)
        toolbarControl()
        setEditTexts()

        binding.sendEmailBtnId.setOnClickListener {
            validation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private fun validation(): Boolean {
        email = binding.emailEt.text.toString()

        if (email.isEmpty()) {
            binding.emailEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorEmailTv.viewVisible()
            binding.errorEmailTv.text = getString(R.string.please_enter_email)
            binding.emailEt.requestFocus()
        } else if (isValidEmail(email).not()) {
            binding.emailEt.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red_color)
            binding.errorEmailTv.viewVisible()
            binding.errorEmailTv.text = getString(R.string.please_enter_valid_email_address)
            binding.emailEt.requestFocus()
        } else {
            findNavController().navigate(
                EnterEmailFragmentDirections.actionEnterEmailFragmentToOTPFragment(email)
            )
        }
        return true
    }

    private fun setEditTexts() {
        binding.emailEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.emailEt.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.editText_line_color)
                binding.errorEmailTv.error = null
                binding.errorEmailTv.viewGone()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        binding.authToolbarId.toolbarNameTv.text = getString(R.string.enter_your_email)
        binding.authToolbarId.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}