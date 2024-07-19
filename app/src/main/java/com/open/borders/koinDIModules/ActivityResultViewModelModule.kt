package com.open.borders.koinDIModules

import com.open.borders.views.activities.account.accountDetail.AccountDetailViewModel
import com.open.borders.views.activities.splash.SplashViewModel
import com.open.borders.views.fragments.authentication.signUp.SignUpViewModel
import com.open.borders.views.fragments.guide.guideQuestions.GuideQuestionsViewModel
import com.open.borders.views.fragments.scheduleConsulting.ActivityResultViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val ActivityResultViewModelModule = module {
    viewModel {
        ActivityResultViewModel()
    }
}