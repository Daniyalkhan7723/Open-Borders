package com.open.borders.thirdPartyModules.maskededittext

import com.open.borders.thirdPartyModules.maskededittext.IFormattedString
import com.open.borders.thirdPartyModules.maskededittext.Mask


class MaskedFormatter internal constructor() {
    internal var mMask: Mask? = null


    val maskString: String?
        get() = mMask?.formatString

    val maskLength: Int?
        get() = mMask?.size()

    init {
        mMask = null
    }

    constructor(fmtString: String) : this() {
        this.setMask(fmtString)
    }

    fun setMask(fmtString: String) {
        mMask = Mask(fmtString)
    }


    fun formatString(value: String): IFormattedString? {
        return mMask?.getFormattedString(value)
    }

}
