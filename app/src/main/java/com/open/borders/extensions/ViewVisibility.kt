package com.open.borders.extensions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.open.borders.R
import com.open.borders.utils.Constants


fun View.viewVisible(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}

fun View.viewGone(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun View.setVisibility(i: Boolean) {
    this.visibility = if (i) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

public fun Context.startActivity(javaClass: Any, bundle: Bundle = Bundle()) {
    var intent = Intent(this, javaClass as Class<*>)
    intent.putExtra(Constants.Bundle, bundle)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

public fun Fragment.startActivity(javaClass: Any, bundle: Bundle = Bundle()) {
    var intent = Intent(requireContext(), javaClass as Class<*>)
    intent.putExtra(Constants.Bundle, bundle)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    startActivity(intent)
}

fun View.viewInVisible() {
    this.visibility = View.INVISIBLE
}

fun View.viewVisibility(): Int {
    return this.visibility
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}

fun View.isInVisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

//Hide show password
fun hideShowPassword(editText: EditText, toggle: ImageView) {
    if (editText.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
        toggle.setImageResource(R.drawable.eye_icon)
        editText.transformationMethod = HideReturnsTransformationMethod.getInstance();
    } else {
        toggle.setImageResource(R.drawable.eye_icon_hide)
        editText.transformationMethod = PasswordTransformationMethod.getInstance();
    }
    editText.setSelection(editText.text.toString().length)
}


internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))