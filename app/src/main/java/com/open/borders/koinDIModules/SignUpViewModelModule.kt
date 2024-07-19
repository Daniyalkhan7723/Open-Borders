package com.open.borders.koinDIModules

import com.open.borders.views.fragments.authentication.signUp.SignUpViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val signUpViewModelModule = module {
    viewModel {
        SignUpViewModel(
            get(),
            get()
        )
    }
}