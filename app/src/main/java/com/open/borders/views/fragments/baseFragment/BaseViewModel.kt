package com.open.borders.views.fragments.baseFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.open.borders.utils.Event
import kotlinx.coroutines.Dispatchers

open class BaseViewModel : ViewModel() {

    val dispatcher = Dispatchers.Default

    protected var _showHideProgressDialog = MutableLiveData<Event<Boolean>>()
    val showHideProgressDialog : LiveData<Event<Boolean>> get() = _showHideProgressDialog


    val snackBarMessage = MutableLiveData<Event<String>>()
    protected fun showSnackBarMessage(message: String) {
        snackBarMessage.value = Event(message)
    }

}