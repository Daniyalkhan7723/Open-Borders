package com.open.borders.koinDIModules

import com.open.borders.views.activities.account.accountDetail.AccountDetailViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val AccountDetailViewModelModule = module {
    viewModel {
        AccountDetailViewModel(
            get(),
            get()
        )
    }
}