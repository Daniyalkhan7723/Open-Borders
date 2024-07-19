package com.open.borders.extensions

import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.open.borders.thirdPartyModules.htmltextview.HtmlTextView


fun TextView.setHtmlText(source: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(Html.fromHtml(source).toString())
    }else{
        this.text = Html.fromHtml(Html.fromHtml(source).toString())
    }
}


fun TextView.setNewHtmlText(source: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(source)
    }else{
        this.text = Html.fromHtml(source)
    }
}


fun TextView.setSHtmlText(source: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(source, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM) // for 24 api and more
    } else {
        this.text = Html.fromHtml(Html.fromHtml(source).toString()) // or for older api
    }
}

/**
 * Searches for all URLSpans in current text replaces them with our own ClickableSpans
 * forwards clicks to provided function.
 */
fun HtmlTextView.handleUrlClicks(onClicked: ((String) -> Unit)? = null) {
    //create span builder and replaces current text with it
    text = SpannableStringBuilder.valueOf(text).apply {
        //search for all URL spans and replace all spans with our own clickable spans
        getSpans(0, length, URLSpan::class.java).forEach {
            //add new clickable span at the same position
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClicked?.invoke(it.url)
                    }
                },
                getSpanStart(it),
                getSpanEnd(it),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            //remove old URLSpan
            removeSpan(it)
        }
    }
    //make sure movement method is set
    movementMethod = LinkMovementMethod.getInstance()

}

