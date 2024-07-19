package com.open.borders.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.open.borders.utils.Event
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.views.activities.baseActivity.BaseActivity
import com.open.borders.views.fragments.baseFragment.BaseFragment
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
fun BaseFragment.setupProgressDialog(
    showHideProgress: LiveData<Event<Boolean>>,
    dialogHelper: MaterialDialogHelper
) {
    var mDialog: MaterialDialog? = null
    showHideProgress.observe(this) {
        if (!it.consumed) it.consume()?.let { flag ->
            if (flag)
                mDialog?.show() ?: dialogHelper.showSimpleProgress(requireContext())
                    .also { dialog ->
                        mDialog = dialog
                    }
            else mDialog?.dismiss()
        }
    }
}

@InternalSerializationApi
fun BaseActivity.setupProgressDialog(
    showHideProgress: LiveData<Event<Boolean>>,
    dialogHelper: MaterialDialogHelper
) {
    var mDialog: MaterialDialog? = null
    showHideProgress.observe(this) {
        if (!it.consumed) it.consume()?.let { flag ->
            if (flag)
                mDialog?.show() ?: dialogHelper.showSimpleProgress(this)
                    .also { dialog ->
                        mDialog = dialog
                    }
            else mDialog?.dismiss()
        }
    }
}

