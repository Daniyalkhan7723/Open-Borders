package com.open.borders.views.activities.baseActivity

import androidx.lifecycle.ViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition
import kotlin.reflect.KClass

@InternalSerializationApi
abstract class MainMVVMBaseActivity<out VM : ViewModel>(
    viewModelClass: KClass<VM>,
    viewModelParameters: ParametersDefinition? = null
) : BaseActivity() {


    protected open val viewModel: VM by viewModel(
        clazz = viewModelClass,
        parameters = viewModelParameters
    )
}