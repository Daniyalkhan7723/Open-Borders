package com.open.borders.koinDIModules

import com.open.borders.views.activities.account.paymentDetail.PaymentDetailViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val PaymentDetailViewModelModule = module {
    viewModel {
        PaymentDetailViewModel(
            get(),
            get()
        )
    }
}