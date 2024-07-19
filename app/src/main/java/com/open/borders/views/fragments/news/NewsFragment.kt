package com.open.borders.views.fragments.news

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.NewsFragmentBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.setupProgressDialog
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.koinDIModules.NewsViewModelModule
import com.open.borders.models.generalModel.newsModel.NewsResponseItem
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.PaginationListener
import com.open.borders.utils.PaginationListener.PAGE_START
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.activities.newsDetails.NewsDetailActivity
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class NewsFragment : MainMVVMNavigationFragment<NewsViewModel>(NewsViewModel::class),
    PreviousNewsAdapter.NewsInterface {

    private lateinit var binding: NewsFragmentBinding
    private var newsList = mutableListOf<NewsResponseItem>()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var mList: MutableList<NewsResponseItem>
    private var isLoadding = false
    private var isLasttPage = false
    var per_page = 10

    companion object {
        fun newInstance() = NewsFragment()
    }

    override fun getLayoutResId() = R.layout.news_fragment

    private val previousNewsAdapter: PreviousNewsAdapter by lazy {
        PreviousNewsAdapter(mActivity, this, ArrayList())
    }

    override fun registerModule(): Module {
        return NewsViewModelModule
    }

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        toolbarControl()
        attachViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NewsFragmentBinding.bind(view)
        logScreenName("News")
//        dummyData()
        mList = ArrayList()
        swipeRefresh()
        setAdapter()
        getNewsApiCall(per_page, 0)

        binding.preiousNewsRecyclerId.addOnScrollListener(object :
            PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoadding = true
                getNewsApiCall(per_page, viewModel.currentPage)
            }

            override fun isLastPage(): Boolean {
                return isLasttPage
            }

            override fun isLoading(): Boolean {
                return isLoadding
            }
        })
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
        binding.preiousNewsRecyclerId.let {
            it.setHasFixedSize(true)
            it.layoutManager = layoutManager
            it.adapter = previousNewsAdapter
        }
    }

    private fun getNewsApiCall(per_page: Int, offset: Int) {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getNews(per_page, offset)
        } else {
            CustomToasts.showToast(
                requireActivity(),
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }

        observe(viewModel.getNewsList) {
//            CustomToasts.showToast(requireActivity(), "News Fetched", true)
            binding.shimmerFrameLayout.viewGone()
            binding.preiousNewsRecyclerId.viewVisible()
            newsList = it.toMutableList()

            if (viewModel.currentPage != PAGE_START) {
                previousNewsAdapter.removeLoading()
            }
            previousNewsAdapter.addItems(it)

            //Check for last page
            if (viewModel.currentPage > 0) {
                previousNewsAdapter.addLoading()
            } else {
                isLasttPage = true
            }

            isLoadding = false
//            mList.addAll(newsList)
            Log.d("ppppppppp", mList.size.toString())
        }
    }

    override fun onNewsItemClick(index: Int, data: NewsResponseItem) {
        val intent = Intent(requireContext(), NewsDetailActivity::class.java)
        intent.putExtra("link", data.link)
        startActivity(intent)
    }

    override fun onImageClick(index: Int, imageUrl: String) {
        lifecycleScope.launch {
            SharePreferenceHelper.getInstance(requireContext()).saveFileName(imageUrl)
        }
        showFilePopup()
    }

    //    Swipe to refresh
    @RequiresApi(Build.VERSION_CODES.M)
    private fun swipeRefresh() {
        val swipe = binding.upCommingAppointmentSwifeRefreshLayout
        swipe.setOnRefreshListener {
            swipe.setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(
                    requireContext(), R.color.white
                )
            )
//            mList.clear()
            viewModel.currentPage = 0
            previousNewsAdapter.clearItems()
            getNewsApiCall(per_page, 0)
            binding.shimmerFrameLayout.viewVisible()
            swipe.isRefreshing = false
            isLasttPage = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        val homeActivity = (activity as HomeHost).binding.homeToolbarId
        homeActivity.toolbarNameTv.text = getString(R.string.news)
        homeActivity.backArrow.viewGone()

        homeActivity.llChangeLanguage.setOnClickListener {
            (activity as HomeHost).changeLanguageMenu()
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTabType("News")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logScreenName("News")
        toolbarControl()
    }
}