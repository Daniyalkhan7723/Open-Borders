package com.open.borders.views.fragments.scheduleConsulting

import androidx.lifecycle.MutableLiveData
import com.open.borders.views.fragments.baseFragment.BaseViewModel

class ActivityResultViewModel: BaseViewModel() {

    var resultData: MutableLiveData<String> = MutableLiveData()

    fun setResult(data: String){
        resultData.value = data
    }
}