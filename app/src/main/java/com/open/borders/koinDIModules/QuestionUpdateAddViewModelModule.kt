package com.open.borders.koinDIModules

import com.open.borders.views.fragments.authentication.signUp.SignUpViewModel
import com.open.borders.views.fragments.guide.guideQuestions.GuideQuestionsViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalSerializationApi
val QuestionUpdateAddViewModelModule = module {
    viewModel {
        GuideQuestionsViewModel(
            get(),
            get()
        )
    }
}