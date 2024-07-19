package com.open.borders.extensions

import com.open.borders.utils.Event

fun <T> T.wrapWithEvent() = Event(this)