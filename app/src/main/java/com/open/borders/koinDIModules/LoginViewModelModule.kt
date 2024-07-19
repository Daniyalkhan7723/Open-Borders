package com.open.borders.koinDIModules

import com.open.borders.views.fragments.authentication.login.LoginViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val LoginViewModelModule = module {
    viewModel {
        LoginViewModel(
            get(),
            get()
        )
    }
}