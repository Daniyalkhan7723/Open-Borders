package com.open.borders.koinDIModules

import com.open.borders.views.fragments.authentication.login.LoginViewModel
import com.open.borders.views.fragments.scheduleConsulting.DateAndTimeViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val DatesAndTimeViewModelModule = module {
    viewModel {
        DateAndTimeViewModel(
            get()
        )
    }
}