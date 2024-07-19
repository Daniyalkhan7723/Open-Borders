package com.open.borders.views.fragments.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.open.borders.models.generalModel.newsModel.NewsResponse
import com.open.borders.utils.PaginationListener.PAGE_SIZE
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.utils.SharedWebServices
import com.open.borders.views.fragments.baseFragment.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class NewsViewModel constructor(
    private val sharedWebServices: SharedWebServices,
    private val sharePreferenceHelper: SharePreferenceHelper
) : BaseViewModel() {

    var currentPage: Int = 0
    var getNewsList: MutableLiveData<NewsResponse> = MutableLiveData()
    var getNewsListFirstPage: MutableLiveData<NewsResponse> = MutableLiveData()

    fun getNews(per_page: Int, offset: Int) {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getNews(per_page, offset).run {
                onSuccess {

//                    Log.d("ppppppppp", it.size.toString())
                    if (it.size == 10) {
                        currentPage += 10
                        PAGE_SIZE = it.size
                        getNewsList.value = it
                    } else {
                        currentPage = -1
                        getNewsList.value = it
                    }


                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }
    fun getFirstPageNews(per_page: Int, offset: Int) {
        viewModelScope.launch {
//            _showHideProgressDialog.value = true.wrapWithEvent()
            sharedWebServices.getNews(per_page, offset).run {
                onSuccess {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    getNewsListFirstPage.value = it
                }
                onFailure {
//                    _showHideProgressDialog.value = false.wrapWithEvent()
                    showSnackBarMessage(it.message!!)
                }
            }
        }
    }
}