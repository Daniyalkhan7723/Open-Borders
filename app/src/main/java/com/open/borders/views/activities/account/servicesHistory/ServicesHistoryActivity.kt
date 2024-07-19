package com.open.borders.views.activities.account.servicesHistory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.ActivityServicesHistoryBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.koinDIModules.AccountDetailViewModelModule
import com.open.borders.models.generalModel.historyModels.ConsultationBookingDetail
import com.open.borders.models.generalModel.historyModels.PurchesPlanHistory
import com.open.borders.utils.Constants
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.account.accountDetail.AccountDetailViewModel
import com.open.borders.views.activities.baseActivity.MainMVVMBaseActivity
import com.open.borders.views.activities.packegeDetail.PackageDetailActivity
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.core.module.Module

@InternalSerializationApi
class ServicesHistoryActivity :
    MainMVVMBaseActivity<AccountDetailViewModel>(AccountDetailViewModel::class),
    ServicesHistoryAdapter.ServicesHistoryInterface,
    ConsultationHistoryAdapter.ConsultationHistoryInterface {

    private lateinit var binding: ActivityServicesHistoryBinding
    private var tabFlag: Boolean? = true

    private var consultationList = mutableListOf<ConsultationBookingDetail>()
    private var servicesList = mutableListOf<PurchesPlanHistory>()

    private val serviceHistoryAdapter: ServicesHistoryAdapter by lazy {
        ServicesHistoryAdapter(this, this)
    }

    private val consultationHistoryAdapter: ConsultationHistoryAdapter by lazy {
        ConsultationHistoryAdapter(this, this)
    }

    override fun registerModule(): Module {
        return AccountDetailViewModelModule
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        logScreenName("Services History")
        binding.toolbarId.toolbarNameTv.text = getString(R.string.services_history)
        binding.toolbarId.llChangeLanguage.viewGone()
        binding.toolbarId.backArrow.setOnClickListener {
            onBackPressed()
        }
        attachViewModel()
        getServicesHistory()
        swipeRefresh()
        listener()
        if (intent.hasExtra("fromServices")) {
            when (intent.getSerializableExtra("fromServices") as Int) {
                1 -> {
                    servicesTab()
                }
            }
        }
    }

    private fun setAdapter() {
        binding.servicesHistoryRecyclerId.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = serviceHistoryAdapter
        }
    }

    private fun setConsultationAdapter() {
        binding.servicesHistoryRecyclerId.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = consultationHistoryAdapter
        }
    }

    //Get Services History Api Call
    private fun getServicesHistory() {
        if (isNetworkAvailable(this)) {
            viewModel.getServicesHistory()
        } else {
            CustomToasts.showToast(
                this,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(this, msg, false)
            }
        }
        observe(viewModel.getServicesHistoryLivaData) {
            if (it.status) {
                consultationList = it.data.consultation_booking_detail.toMutableList()
                servicesList = it.data.purches_plan_history.toMutableList()

                binding.shimmerFrameLayout?.stopShimmer()
                binding.shimmerFrameLayout?.viewGone()

                if (tabFlag == true) {
                    if (!consultationList.isNullOrEmpty()) {
                        binding.servicesHistoryRecyclerId.viewVisible()
                        binding.noDataView?.viewGone()
                        consultationHistoryAdapter.getConsultationList = consultationList
                        setConsultationAdapter()
                    } else {
                        binding.servicesHistoryRecyclerId.viewGone()
                        binding.noDataView?.viewVisible()
                    }
                } else {
                    if (!servicesList.isNullOrEmpty()) {
                        binding.servicesHistoryRecyclerId.viewVisible()
                        binding.noDataView?.viewGone()
                        serviceHistoryAdapter.getServicesHistoryList = servicesList
                        setAdapter()
                    } else {
                        binding.servicesHistoryRecyclerId.viewGone()
                        binding.noDataView?.viewVisible()
                    }
                }
            } else {
                CustomToasts.showToast(this, it.message, false)
            }
        }
    }

    //Swipe to refresh
    @RequiresApi(Build.VERSION_CODES.M)
    private fun swipeRefresh() {
        val swipe = binding.swipeToRefresh
        swipe!!.setOnRefreshListener {
            swipe.setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(
                    this, R.color.white
                )
            )
            getServicesHistory()
            swipe.isRefreshing = false
        }
    }

    override fun onImageClick(index: Int, imageUrl: String) {
        lifecycleScope.launch {
            SharePreferenceHelper.getInstance(applicationContext).saveFileName(imageUrl)
        }
        showFilePopup()
    }

    override fun onItemClick(index: Int, data: PurchesPlanHistory, option: ActivityOptionsCompat) {
        val intent = Intent(
            this,
            PackageDetailActivity::class.java
        ).putExtra("myObject", data)
            .putExtra("fromFragment", 2)
        startActivity(intent, option.toBundle())
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun listener() {
        binding.consultations.setOnClickListener {
            tabFlag = true
            binding.consultations!!.background =
                ContextCompat.getDrawable(this, R.drawable.actionlink_bg)
            binding.consultations!!.setTextColor(ContextCompat.getColor(this, R.color.app_bg_color))

            binding.services!!.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg_color))
            binding.services!!.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.noDataView?.viewGone()
            servicesList.clear()
            serviceHistoryAdapter.notifyDataSetChanged()
            binding.shimmerFrameLayout?.viewVisible()
            binding.shimmerFrameLayout?.startShimmer()
            getServicesHistory()
        }


        binding.services.setOnClickListener {
            servicesTab()
        }

    }

    private fun servicesTab() {
        tabFlag = false
        binding.services!!.background =
            ContextCompat.getDrawable(this, R.drawable.actionlink_bg)
        binding.services!!.setTextColor(ContextCompat.getColor(this, R.color.app_bg_color))

        binding.consultations!!.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg_color))
        binding.consultations!!.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.noDataView?.viewGone()
        consultationList.clear()
        consultationHistoryAdapter.notifyDataSetChanged()
        binding.shimmerFrameLayout?.viewVisible()
        binding.shimmerFrameLayout?.startShimmer()
        getServicesHistory()

    }

}