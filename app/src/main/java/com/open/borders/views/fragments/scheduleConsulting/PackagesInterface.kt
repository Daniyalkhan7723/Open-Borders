package com.open.borders.views.fragments.scheduleConsulting

import androidx.core.app.ActivityOptionsCompat
import com.open.borders.models.responseModel.GetPackagesModel

interface PackagesInterface {
        fun onPackagesItemClick(data: GetPackagesModel, index: Int, option: ActivityOptionsCompat)
        fun onImageClick(index: Int, imageUrl: String)
}