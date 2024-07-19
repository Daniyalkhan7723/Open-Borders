package com.open.borders.koinDIModules

import com.open.borders.views.fragments.scheduleConsulting.ScheduleConsultingViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val ConsultationViewModelModule = module {
    viewModel {
        ScheduleConsultingViewModel(
            get(),
            get()
        )
    }
}