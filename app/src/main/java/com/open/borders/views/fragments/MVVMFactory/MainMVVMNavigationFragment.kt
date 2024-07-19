package com.open.borders.views.fragments.MVVMFactory

import androidx.lifecycle.ViewModel
import com.open.borders.views.fragments.baseFragment.BaseFragment
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition
import kotlin.reflect.KClass

@InternalSerializationApi
abstract class MainMVVMNavigationFragment<out VM : ViewModel>(
    viewModelClass: KClass<VM>,
    viewmodelParameters: ParametersDefinition? = null
) : BaseFragment() {
    protected open val viewModel: VM by viewModel(
        clazz = viewModelClass,
        parameters = viewmodelParameters
    )

}