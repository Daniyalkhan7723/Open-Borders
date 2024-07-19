package com.open.borders.koinDIModules

import com.open.borders.views.fragments.accounts.accountsHome.AccountViewModel
import com.open.borders.views.fragments.authentication.login.LoginViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val AccountViewModelModule = module {
    viewModel {
        AccountViewModel(
            get()
        )
    }
}