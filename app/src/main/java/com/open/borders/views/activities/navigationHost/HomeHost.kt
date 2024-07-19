package com.open.borders.views.activities.navigationHost

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.MenuCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.ActivityHomeHostBinding
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.setupProgressDialog
import com.open.borders.models.generalModel.QuestionStateModel
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.models.generalModel.questionsApiModel.GetQuestionsModel
import com.open.borders.utils.Constants
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.MainMVVMBaseActivity
import com.open.borders.views.activities.baseActivity.SummeryModel
import com.open.borders.views.activities.splash.SplashViewModel
import com.open.borders.views.fragments.scheduleConsulting.ActivityResultViewModel
import com.open.borders.views.fragments.scheduleConsulting.DateAndTimeViewModel
import kotlinx.serialization.InternalSerializationApi
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@InternalSerializationApi
class HomeHost : MainMVVMBaseActivity<SplashViewModel>(SplashViewModel::class) {
    lateinit var binding: ActivityHomeHostBinding
    private var navHostFragment: NavHostFragment? = null
    private var doubleBackToExitPressedOnce = false
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

    private val LOAD_PAYMENT_DATA_REQUEST_CODE_For_Guest_Mode = 992

    private var currentTabIndex = 0

    val activityResultViewModel: ActivityResultViewModel by viewModel()
    private val mViewVModel: DateAndTimeViewModel by viewModel()

    companion object {
        public var homeHost: HomeHost? = null
        public var navController: NavController? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(mViewVModel.showHideProgressDialog, dialogHelper)

        homeHost = this@HomeHost
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment_id) as NavHostFragment
        navController = navHostFragment!!.navController

        if (isTablet()) {
            logDeviceType("Tablet")
        } else {
            logDeviceType("Android Phone")
        }
//        attachViewModel()
        setBottomLayout()
        Log.e("Evaluated Summery List", evaluatedSummeryList.toString())
        val tabType = SharePreferenceHelper.getInstance(this).getTabType()
        when (tabType) {
            "welcome" -> {
                guideTab()
//                val navBuilder = NavOptions.Builder().setEnterAnim(R.anim.empty_animation)
                navController?.navigate(R.id.guideWelcomeFragment)
            }
            "Guide" -> {
                try {
                    val questions = tinyDB.getString("questionJson")
                    val summeryJson = tinyDB.getString("summeryJson")
                    val traversedPath = tinyDB.getString("traversedPathJson")
                    val type = object : TypeToken<HashMap<String?, String?>?>() {}.type
                    if (traversedPath != null && traversedPath != "") {
                        traversedPathMap = Gson().fromJson(traversedPath, type)
                    }

                    lastQuestion = tinyDB.getString("lastQuestion")
                    val mLocalQuestions = Gson().fromJson(questions, GetQuestionsModel::class.java)
//                    val summeryDataJson: ArrayList<SummeryModel> = ArrayList()
//                    val summeryData = Gson().fromJson(summeryJson, summeryDataJson::class.java)
                    if (summeryJson.isNotEmpty()) {
                        val summeryArray = JSONArray(summeryJson)
                        Log.e("SummeryArray", summeryArray.toString())
                        for (i in 0 until summeryArray.length()) {
                            Log.e("summery", "HistoryModel: ${summeryArray[i]}")
                            val obj = JSONObject("${summeryArray[i]}")
                            val summeryModel = SummeryModel()
                            summeryModel.id = obj.getString("id")
                            summeryModel.optionId = obj.getString("option_id")
                            summeryModel.category = obj.getString("category")
                            summeryModel.summaryDescription = obj.getString("description")
                            summeryModel.summaryDescription_ES = obj.getString("description_ES")

                            if (summeryModel.category != null && summeryModel.category != "" && ((summeryModel.summaryDescription != null && summeryModel.summaryDescription != "") || (summeryModel.summaryDescription_ES != null && summeryModel.summaryDescription_ES != ""))) {
                                evaluatedSummeryList.add(summeryModel)
                                Log.e("SumDesc", "${summeryModel.summaryDescription}")
                            }
                        }
                    }

                    Log.e("Evaluated Summery List Again", evaluatedSummeryList.toString())

                    updatedQuestionsList = mLocalQuestions.questions?.toMutableList()!!
                    q1 = updatedQuestionsList.first()
                    q2 = updatedQuestionsMap[q1?.data?.child?.first()?.targetNode]

                    savePathData("${q1?.id}", "${q1?.data?.child?.first()?.id}")
                    savePathData("${q2?.id}", "${q2?.data?.child?.first()?.id}")

                    updatedQuestionsList.forEachIndexed { idx, data: GuideQuestionsModelItem ->
                        updatedQuestionsMap[data.id] = data
//            Log.i("data", "> Items $idx:\n$data")
                    }

                    lastQuestion = tinyDB.getString("lastQuestion")
                    questionsOrder = tinyDB.getListString("questionOrder")
                    guideTab()
//                val navBuilder = NavOptions.Builder().setEnterAnim(R.anim.empty_animation)
                    navController?.navigate(R.id.guideQuestionerFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            "Consultation" -> {
                consultationTab()
                navController?.navigate(R.id.scheduleConsultingFragment, null, null)
            }

            "Services" -> {
                servicesTab()
                navController?.navigate(R.id.flatFeeFragment, null, null)
            }

            "News" -> {
                newsTab()
                navController?.navigate(R.id.newsFragment, null, null)
            }

            "Account" -> {
                accountTab()
//                val navBuilder = NavOptions.Builder().setEnterAnim(R.anim.slide_up)
                navController?.navigate(R.id.accountFragment, null, null)
                mViewVModel.showHideProgressBar(false)
            }

        }

        val userId = sharedPreference?.getUser()?.id
        val userName =
            sharedPreference?.getUser()?.first_name + "" + sharedPreference?.getUser()?.last_name
        if (sharedPreference?.getUser()?.is_guest == 0) {
            if (userId.toString().isNotEmpty() || userName.isNotEmpty()) {
                logUserDetails(userId.toString(), userName)
            }
        } else {
            if (userId.toString().isNotEmpty()) {
                logUserDetails(userId.toString(), "Guest User")
            }
        }

        if (getCurrentLanguage() == "es") {
            sendLanguageApiCall(2)
            binding.homeToolbarId.tvLanguage.text = resources.getString(R.string.spanish)
        } else {
            sendLanguageApiCall(1)
            binding.homeToolbarId.tvLanguage.text = resources.getString(R.string.english)
        }

//        getQuestionStateApi()
    }

    private fun attachViewModel() {
        observe(viewModel.getQuestionStateLiveData) {
            if (it.status) {
                lastQuestion = null
//                evaluatedSummeryList.clear()
                questionsOrder.clear()
                traversedPathMap.clear()
                if (it.data != null) {
                    mapData(it.data) {
                    }
                }
//                Log.e("previousSavedData", "${it.data}")
            }
        }
    }

    private fun getQuestionStateApi() {
        //Get Question Api Call
        if (isNetworkAvailable(this)) {
            viewModel.getQuestionState(
                sharedPreference?.getUser()?.id.toString()
            )
        } else {
            CustomToasts.showToast(
                this,
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    private fun mapData(data: QuestionStateModel, callback: (Boolean) -> Unit) {
//        Log.e("prevSelections", "HistoryModel: ${data.prev_selections}")
//        Log.e("currentSummary", "HistoryModel: ${data.current_summary}")
        try {
            val obj = JSONObject(data.prev_selections)
            val iterator: Iterator<String> = obj.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                val value: Any = obj.get(key)
                traversedPathMap["$key"] = "$value"
            }
            questionsOrder.clear()
            val questionsOrderArray = JSONArray(data.questions_order)
            for (i in 0 until questionsOrderArray.length()) {
                questionsOrder.add("${questionsOrderArray[i]}")
            }

            val summeryArray = JSONArray(data.current_summary)
            for (i in 0 until summeryArray.length()) {
                Log.e("summery", "HistoryModel: ${summeryArray[i]}")

                val obj = JSONObject("${summeryArray[i]}")
                val summeryModel = SummeryModel()
                summeryModel.id = obj.getString("id")
                summeryModel.optionId = obj.getString("option_id")
                summeryModel.category = obj.getString("category")
                summeryModel.summaryDescription = obj.getString("description")
                summeryModel.summaryDescription_ES = obj.getString("description_ES")

                if (summeryModel.category != null && summeryModel.category != "" && ((summeryModel.summaryDescription != null && summeryModel.summaryDescription != "") || (summeryModel.summaryDescription_ES != null && summeryModel.summaryDescription_ES != ""))) {
                    evaluatedSummeryList.add(summeryModel)
                    Log.e("SumDesc", "${summeryModel.summaryDescription}")
                }
            }

            lastQuestion = data.last_question ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("restoredAnswers", "${traversedPathMap.size}")
        Log.e("restoredSummaries", "${evaluatedSummeryList.size}")
        Log.e("questionsOrder", "${questionsOrder.size}")
        Log.e("lastQuestion", lastQuestion ?: "")
        callback(true)
    }

    override fun recreate() {
        finish()
        overridePendingTransition(
            R.anim.empty_animation,
            R.anim.empty_animation
        )
        startActivity(intent)
        overridePendingTransition(
            R.anim.empty_animation,
            R.anim.empty_animation
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            // Value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }

                    AppCompatActivity.RESULT_CANCELED -> {
                        // The user cancelled the payment attempt
                    }

                    RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
            }
            LOAD_PAYMENT_DATA_REQUEST_CODE_For_Guest_Mode -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }

                    AppCompatActivity.RESULT_CANCELED -> {
                        // The user cancelled the payment attempt
                    }

                    RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
            }
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()
        try {
            activityResultViewModel.setResult(paymentInformation)

        } catch (e: JSONException) {
            activityResultViewModel.setResult(e.toString())
        }
    }


    private fun handleError(statusCode: Int) {
        activityResultViewModel.setResult(statusCode.toString())
    }

    fun changeLanguageMenu() {
//        binding.homeToolbarId.llChangeLanguage.setOnClickListener {
        val context: Context = ContextThemeWrapper(this, R.style.PopupMenu)
        val popup = PopupMenu(this@HomeHost, binding.homeToolbarId.llChangeLanguage)
        popup.menuInflater.inflate(R.menu.change_language_popup_menu, popup.menu)
        MenuCompat.setGroupDividerEnabled(popup.menu, true)
        popup.setOnMenuItemClickListener { item ->
//            mViewVModel.showHideProgressBar(true)

//            lifecycleScope.launch {
//                delay(1000)
            Log.e("Id", "${item.itemId}")
            when (item.itemId) {
                R.id.one -> {
                    //setEnglish()
                    tinyDB.putString(
                        Constants.language,
                        "en_US"
                    )
                    updateLocale(Locale("en_US"))

//                    ContextUtils.updateLocale(applicationContext, Locale("en_US"))
//                    binding.homeToolbarId.tvLanguage.text =
//                        resources.getString(R.string.english)
//                    startActivity(Intent.makeRestartActivityTask(this.intent?.component))
                }
                else -> {
                    //setSpanish()
                    tinyDB.putString(
                        Constants.language,
                        "es"
                    )

                    updateLocale(Locale("es"))
//                    ContextUtils.updateLocale(applicationContext, Locale("es"))
//                    binding.homeToolbarId.tvLanguage.text = resources.getString(R.string.spanish)
//                    startActivity(Intent.makeRestartActivityTask(this.intent?.component))
                }
//                }
            }
            true
        }
        popup.show()
//        }
    }

    //NavigateTOGuide
    fun navigateToGuide() {
//        val navBuilder = NavOptions.Builder().setEnterAnim(R.anim.slide_up)
        navController?.navigate(R.id.guideWelcomeFragment)
    }

    //NavigateToConsulting
    fun navigateToConsultation() {
//        val navBuilder = NavOptions.Builder().setEnterAnim(R.anim.right_to_left)
        navController?.navigate(R.id.scheduleConsultingFragment, null, null)
    }

    //NavigateTOServices
    public fun navigateToServices() {
//        val navBuilder = NavOptions.Builder().setEnterAnim(R.anim.right_to_left)
        navController?.navigate(R.id.flatFeeFragment, null, null)
    }

    //NavigateToNews
    private fun navigateToNews() {
//        val navBuilder = NavOptions.Builder().setEnterAnim(R.anim.right_to_left)
        navController?.navigate(R.id.newsFragment, null, null)
    }

    //NavigateToAccount
    private fun navigateToAccount() {
        navController?.navigate(R.id.accountFragment, null, null)
    }

    //Reset all icons to default
    private fun resetItems() {
        binding.guideImageView.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_guide_new
                )
            )
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_icon_not_selected_color
                )
            )
        }
        binding.guideTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.app_icon_not_selected_color
            )
        )

        binding.consultationImageview.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_consult
                )
            )
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_icon_not_selected_color
                )
            )
        }
        binding.consultationTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.app_icon_not_selected_color
            )
        )

        binding.servicesImageview.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_services_new
                )
            )
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_icon_not_selected_color
                )
            )
        }
        binding.servicesTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.app_icon_not_selected_color
            )
        )

        binding.newsImageview.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_news_new
                )
            )
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_icon_not_selected_color
                )
            )
        }
        binding.newsTitleTv.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.app_icon_not_selected_color
            )
        )

        binding.accountImage.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_account_new
                )
            )
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_icon_not_selected_color
                )
            )
        }
        binding.accountTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.app_icon_not_selected_color
            )
        )
    }


    //Custom Bottom navigation View
    private fun setBottomLayout() {
        resetItems()
        currentTabIndex = 0
        binding.guideImageView.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_guide_fill
                )
            )
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_icon_selected_color
                )
            )
        }
        binding.guideTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.app_icon_selected_color
            )
        )

        binding.guideLayout.setOnClickListener {
            if (currentTabIndex != 0) {
                guideTab()
                navigateToGuide()
            }
        }

        binding.consultationLayout.setOnClickListener {
            if (currentTabIndex != 1) {
                consultationTab()
                navigateToConsultation()
            }
        }

        binding.servicesLayout.setOnClickListener {
            if (currentTabIndex != 2) {
                servicesTab()
                navigateToServices()
            }
        }

        binding.newsLayout.setOnClickListener {
            if (currentTabIndex != 3) {
                newsTab()
                navigateToNews()
            }
        }

        binding.accountLayout.setOnClickListener {
            if (currentTabIndex != 4) {
                accountTab()
                navigateToAccount()
            }
        }
    }


    //Reset and focus on Flat Fee Tab
    fun servicesTab() {
        if (currentTabIndex != 2) {
            resetItems()
            currentTabIndex = 2
            binding.servicesImageview.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_services_fill
                    )
                )
                setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.app_icon_selected_color
                    )
                )
            }
            binding.servicesTitle.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_icon_selected_color
                )
            )
            navController?.popBackStack()
        }
    }

    //Reset and focus on Consultation Tab
    fun consultationTab() {
        if (currentTabIndex != 1) {
            resetItems()
            currentTabIndex = 1
            binding.consultationImageview.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_consult_fill
                    )
                )
                binding.consultationImageview.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.app_icon_selected_color
                    )
                )
            }
            binding.consultationTitle.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_icon_selected_color
                )
            )
            navController?.popBackStack()
        }
    }

    //Reset and focus on Guide Tab
    fun guideTab() {
        if (currentTabIndex != 0) {
            resetItems()
            currentTabIndex = 0
            binding.guideImageView.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_guide_fill
                    )
                )
                binding.guideImageView.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.app_icon_selected_color
                    )
                )
            }
            binding.guideTitle.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_icon_selected_color
                )
            )
            navController?.popBackStack()
        }
    }

    //Reset and focus of News Tab
    private fun newsTab() {
        if (currentTabIndex != 3) {
            resetItems()
            currentTabIndex = 3
            binding.newsImageview.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_news_fill
                    )
                )
                binding.newsImageview.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.app_icon_selected_color
                    )
                )
            }
            binding.newsTitleTv.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_icon_selected_color
                )
            )
            navController?.popBackStack()
        }
    }

    //Reset and focus of Account Tab
    private fun accountTab() {
        if (currentTabIndex != 4) {
            resetItems()
            currentTabIndex = 4
            binding.accountImage.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_account_fill
                    )
                )
                setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.app_icon_selected_color
                    )
                )
            }
            binding.accountTitle.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_icon_selected_color
                )
            )
            navController?.popBackStack()
        }
    }

    //On top level destination on back press navigate to Home
    @SuppressLint("ResourceType")
    override fun onKeyDown(keyCode: Int, event: KeyEvent?) = if (keyCode == KeyEvent.KEYCODE_BACK) {
        val currentDestination = navController?.currentDestination
        if (currentDestination != null && currentDestination.id == R.id.guideQuestionerFragment || currentDestination?.id == R.id.guideWelcomeFragment) {
            doubleBackToExitPressedOnce = if (doubleBackToExitPressedOnce) {
                moveTaskToBack(true)
                false
            } else {
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
                true
            }
            true
        } else {
            guideTab()
            navigateToGuide()
            true
        }

    } else false

    override fun onBackPressed() = Unit

    private fun sendLanguageApiCall(language: Int) {
        if (isNetworkAvailable(this)) {
            viewModel.sendLanguage(language)
        } else {
            CustomToasts.showToast(this, Constants.INTERNET_CONNECTION_ERROR_MESSAGE, false)
        }
    }

    private fun savePathData(qId: String, optionId: String) {
        if (traversedPathMap.containsKey(qId) && !(traversedPathMap.get(
                qId
            )?.contains(optionId ?: "") == true)
        ) {
            val multipleAnswers = traversedPathMap.get(qId) + "~" + optionId
            traversedPathMap.put(qId, multipleAnswers)
            Log.e("multipleMapData", "${qId}, $multipleAnswers")
        } else if (!traversedPathMap.containsKey(qId)) {
            traversedPathMap.put(
                qId,
                optionId
            )
            Log.e(
                "singleMapData",
                "${qId}, $optionId"
            )

            Log.e("path", traversedPathMap.toString())
        }
    }


}