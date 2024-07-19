package com.open.borders.views.fragments.flatFee

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.FlatFeeFragmentBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.setupProgressDialog
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.koinDIModules.ConsultationViewModelModule
import com.open.borders.models.responseModel.GetPackagesModel
import com.open.borders.models.responseModel.PackageFilterModel
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.activities.packegeDetail.PackageDetailActivity
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import com.open.borders.views.fragments.scheduleConsulting.PackagesAdapter
import com.open.borders.views.fragments.scheduleConsulting.PackagesInterface
import com.open.borders.views.fragments.scheduleConsulting.ScheduleConsultingViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class FlatFeeFragment :
    MainMVVMNavigationFragment<ScheduleConsultingViewModel>(ScheduleConsultingViewModel::class),
    PackagesInterface {
    private lateinit var binding: FlatFeeFragmentBinding

    private var flatFreeList = mutableListOf<GetPackagesModel>()
    private var flatFeeFiltersList = mutableListOf<PackageFilterModel>()

    private var language: String? = null


    override fun getLayoutResId() = R.layout.flat_fee_fragment

    private val getPackagesAdapter: PackagesAdapter by lazy {
        PackagesAdapter(mActivity, this)
    }

    override fun registerModule(): Module {
        return ConsultationViewModelModule
    }

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FlatFeeFragmentBinding.bind(view)
        logScreenName("Services")

        language = tinyDB.getString(Constants.language)

        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        binding.myweb.loadUrl("file:///android_asset/sample.html")
        binding.myweb.settings.javaScriptEnabled = true
        binding.myweb.settings.displayZoomControls = false
        binding.myweb.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)


        binding.myweb?.setOnTouchListener(OnTouchListener { v, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://bordercrossinglaw.com/reviews/")
                    context?.startActivity(intent)
                }
//                MotionEvent.ACTION_DOWN->{
//                    Toast.makeText(requireContext(),"fvfvfvf2",Toast.LENGTH_SHORT).show()
//                }
            }
            true
        })


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.myweb?.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        when (language) {
            "es" -> {
                val text =
                    "Ofrecemos todos nuestros servicios a través de tarifas planas asequibles, con opciones de pago mensual para ayudarlo a financiar el proceso.\n\n\uD83D\uDCAC Si necesita una tarifa mensual reducida,por favor contáctenos. Intentaremos trabajar con usted para elegir un plan de pago que pueda pagar."
                val spannableString = SpannableString(text)
                val clickableSpan1 = object : ClickableSpan() {
                    override fun onClick(view: View) {

                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.isUnderlineText = false

                    }

                }

                val clickableSpan2 = object : ClickableSpan() {
                    override fun onClick(view: View) {
                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.isUnderlineText = false
                    }

                }

                val clickableSpan3 = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://bordercrossinglaw.com/contact")
                        context?.startActivity(intent)
                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.isUnderlineText = false
                    }
                }
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 43, 72, 0)
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 78, 101, 0)
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 184, 206, 0)

                spannableString.setSpan(
                    clickableSpan1, 43, 72, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    clickableSpan2, 78, 101, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(
                    clickableSpan3, 184, 206, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.tvText?.text = spannableString
                binding.tvText?.movementMethod = LinkMovementMethod.getInstance()
                binding.tvText?.highlightColor = Color.TRANSPARENT

            }
            else -> {
                val text =
                    "We offer all our services through affordable flat fees, with monthly payment options to help you finance the process.\n\n\uD83D\uDCAC If you need a reduced monthly rate, please contact us. We will try to work with you to choose a payment plan that you can afford."
                val spannableString = SpannableString(text)
                val clickableSpan1 = object : ClickableSpan() {
                    override fun onClick(view: View) {
                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.isUnderlineText = false
                    }
                }

                val clickableSpan2 = object : ClickableSpan() {
                    override fun onClick(view: View) {
                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.isUnderlineText = false
                    }

                }

                val clickableSpan3 = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://bordercrossinglaw.com/contact")
                        context?.startActivity(intent)
                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.isUnderlineText = false
                    }

                }

                spannableString.setSpan(
                    clickableSpan1, 34, 54, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(StyleSpan(Typeface.BOLD), 34, 54, 0)
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 61, 84, 0)
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 157, 175, 0)


                spannableString.setSpan(
                    clickableSpan2, 61, 84, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(
                    clickableSpan3, 157, 175, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.tvText?.text = spannableString
                binding.tvText?.movementMethod = LinkMovementMethod.getInstance()

            }
        }


        getFilterApiCall()
        callPackagesApi("-1")
        swipeRefresh()
        try {
            toolbarControl()
        } catch (e: Exception) {
            showAlertDialog(e.toString())
        }

    }

//    private fun setClickListeners() {
//        dummyData()
//    }

    private fun setAdapter() {
        binding.rvPackages.let {
            it.layoutManager = LinearLayoutManager(mActivity)
            it.adapter = getPackagesAdapter
        }
    }


//    private fun dummyData() {
//        val list = ArrayList<FlatFeeServiceModel>()
//        list.add(FlatFeeServiceModel("Monthly Payment", "$500.00 every month", R.drawable.dummy1))
//        list.add(
//            FlatFeeServiceModel(
//                "Retainer Payment (One Time Payment)",
//                "from $10.00",
//                R.drawable.dummy2
//            )
//        )
//        list.add(
//            FlatFeeServiceModel(
//                "Naturalization (Citizenship)",
//                "$1,000.00 every month for 2 months",
//                R.drawable.dummy3
//            )
//        )
//        flatFeeAdapter.flatFeeList = list
//        setAdapter()
//    }

    private fun attachViewModel() {
        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }

        observe(viewModel.getPackagesList) {
            if (it.status) {
                flatFreeList = it.data?.toMutableList()!!

                binding.shimmerFrameLayout.stopShimmer()
                binding.shimmerFrameLayout.viewGone()
                if (!flatFreeList.isNullOrEmpty()) {
                    binding.rvPackages.viewVisible()
                    binding.noDataView.viewGone()
                    getPackagesAdapter.packagesList = flatFreeList
                    setAdapter()
                } else {
                    binding.rvPackages.viewGone()
                    binding.noDataView.viewVisible()
                }
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }

        observe(viewModel.getPackageFilterLiveData) {
            if (it.status) {
                flatFeeFiltersList = it.data.toMutableList()
                val data = PackageFilterModel(
                    "",
                    "",
                    -1,
                    "",
                    "All",
                    "",
                    ""
                )
                flatFeeFiltersList.add(0, data)
                setSpinner()
            }
        }
    }

    private fun callPackagesApi(packageId: String) {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getPackages(packageId)
        } else {
            CustomToasts.showToast(
                requireActivity(),
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    override fun onPackagesItemClick(
        data: GetPackagesModel,
        index: Int,
        option: ActivityOptionsCompat
    ) {
        val intent = Intent(requireContext(), PackageDetailActivity::class.java)
        intent.putExtra("myObject", data)
        intent.putExtra("fromFragment", 1)
        startActivity(intent, option.toBundle())
    }

    override fun onImageClick(index: Int, imageUrl: String) {
        lifecycleScope.launch {
            SharePreferenceHelper.getInstance(requireContext())
                .saveFileName(Constants.IMAGE_BASE_URL + imageUrl)
        }
        showFilePopup()
    }

    //Swipe to refresh
    @RequiresApi(Build.VERSION_CODES.M)
    private fun swipeRefresh() {
        val swipe = binding.swipeToRefresh
        swipe.setOnRefreshListener {
            swipe.setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(
                    requireContext(), R.color.white
                )
            )
            callPackagesApi("-1")
            swipe.isRefreshing = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        val homeActivity = (requireActivity() as HomeHost).binding.homeToolbarId
        homeActivity.toolbarNameTv.text = getString(R.string.flat_fee_services)
        homeActivity.backArrow.viewGone()

        homeActivity.llChangeLanguage.setOnClickListener {
            (activity as HomeHost).changeLanguageMenu()
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTabType("Services")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logScreenName("Services")
        toolbarControl()
    }

    private fun getFilterApiCall() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getPackagesFilter()
        } else {
            CustomToasts.showToast(
                requireActivity(),
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSpinner() {
        binding.spinner.apply {
            setSpinnerAdapter(SpinnerAdapter(this))
            setItems(flatFeeFiltersList)
            getSpinnerRecyclerView().layoutManager = LinearLayoutManager(requireContext())
            selectItemByIndex(0) // select a default item.
            lifecycleOwner = this@FlatFeeFragment
        }
        binding.spinner.selectItemByIndex(0)
        binding.spinner.setOnSpinnerItemSelectedListener<PackageFilterModel> { oldIndex, oldItem, newIndex, newItem ->
            flatFreeList.clear()
            getPackagesAdapter.notifyDataSetChanged()
            binding.rvPackages.viewGone()
            binding.shimmerFrameLayout.viewVisible()
            callPackagesApi(newItem.id.toString())
        }
    }

}