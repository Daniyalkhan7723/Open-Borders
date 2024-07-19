package com.open.borders.views.activities.baseActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.open.borders.extensions.wrapWithEvent
import com.open.borders.utils.Event

open class BaseViewModel : ViewModel() {

    protected var _showHideProgressDialog = MutableLiveData<Event<Boolean>>()
    var showHideProgressDialog: LiveData<Event<Boolean>> = _showHideProgressDialog


    val snackBarMessage = MutableLiveData<Event<String>>()
    protected fun showSnackBarMessage(message: String) {
        snackBarMessage.value = Event(message)
    }
}