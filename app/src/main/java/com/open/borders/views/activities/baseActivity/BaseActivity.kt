package com.open.borders.views.activities.baseActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.open.borders.utils.SharePreferenceHelper
import com.jakewharton.threetenabp.AndroidThreeTen
import com.open.borders.R
import com.open.borders.customPopups.FileViewerPopUp
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.utils.TinyDB
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import kotlinx.serialization.InternalSerializationApi
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@InternalSerializationApi
open class BaseActivity : AppCompatActivity() {

    private val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()
    var q1: GuideQuestionsModelItem? = null
    var q2: GuideQuestionsModelItem? = null
    var updatedQuestionsList = mutableListOf<GuideQuestionsModelItem>()
    var updatedQuestionsMap: HashMap<String?, GuideQuestionsModelItem?> = HashMap()

    private var fileViewerPopUp: FileViewerPopUp? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null

    var fontBold: Typeface? = null
    var fontRegular: Typeface? = null
    var fontLight: Typeface? = null

    var traversedPathMap: HashMap<String?, String?> = HashMap()
    var evaluatedSummeryList: ArrayList<SummeryModel> = ArrayList()
    var previousSelectedAnswerPathLabel: ArrayList<String> = ArrayList()

    //    var evaluatedSummeryMap: HashMap<String?, SummeryModel> = HashMap()
    var lastQuestion: String? = null
    var questionsOrder: ArrayList<String> = ArrayList()

    lateinit var tinyDB: TinyDB
    private var mModule: Module? = null

    open fun registerModule(): Module? = null

    var sharedPreference: SharePreferenceHelper? = null

    @SuppressLint("HardwareIds")
    fun getDeviceID(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }


    fun getCategoryName(id: String, callback: (String) -> Unit) {
        when (id) {
            "immigrationHistory" -> callback(getString(R.string.immigration_history))
            "factorsOptions" -> callback(getString(R.string.factors_options))
            "inadmissibility" -> callback(getString(R.string.inadmissibility))
        }
    }

    fun getTitlesOfQuestionOrder(callback: (ArrayList<String>) -> Unit) {
        val aTitles = ArrayList<String>()
        questionsOrder.forEachIndexed { index, s ->
//            qTitles.add(updatedQuestionsMap[s]?.data?.label ?: "")
            updatedQuestionsMap[s]?.data?.child?.forEachIndexed { index, child ->
                aTitles.add(child.text.toString())
            }
        }
        Log.e("ListSize", "$aTitles")
        callback(aTitles)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeDelegate.onCreate(this)
        supportActionBar?.hide()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedPreference = SharePreferenceHelper.getInstance(this@BaseActivity)

        // The interesting piece here.;
        AndroidThreeTen.init(this@BaseActivity)

        val module = registerModule()
        if (module != null)
            loadKoinModules(module).also { mModule = module }

        if (!::tinyDB.isInitialized)
            tinyDB = TinyDB(this@BaseActivity)
        fontBold = Typeface.createFromAsset(assets, "font/grotesk_pro_bold.ttf")
        fontRegular =
            Typeface.createFromAsset(assets, "font/grotesk_pro_normal.ttf")
        fontLight =
            Typeface.createFromAsset(assets, "font/grotesk_pro_light.ttf")
    }


    protected fun <T, LD : LiveData<T>> observe(liveData: LD, onChanged: (T) -> Unit) {
        liveData.observe(this, Observer {
            it?.let(onChanged)
        })
    }

    fun generateHoursList(): ArrayList<String> {
        val temp = ArrayList<String>()
        for (i in 1..12 step 1) {
            if (i < 10) {
                temp.add("0$i")
            } else {
                temp.add("$i")
            }
        }
        return temp
    }

    fun generateMinutesList(): ArrayList<String> {
        val temp = ArrayList<String>()
        for (i in 0..59 step 1) {
            if (i < 10) {
                temp.add("0$i")
            } else {
                temp.add("$i")
            }
        }
//        temp.add("00")
//        temp.add("30")
        return temp
    }

    fun generateAmPmList(): ArrayList<String> {
        val temp = ArrayList<String>()
        temp.add("AM")
        temp.add("PM")
        return temp
    }

    override fun getDelegate() = localeDelegate.getAppCompatDelegate(super.getDelegate())

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase))

        // get chosen language from shared preference
//        if (!::tinyDB.isInitialized)
//            tinyDB = TinyDB(newBase)
//        val localeToSwitchTo = tinyDB.getString(Constants.language)
//        val locale = Locale(localeToSwitchTo)
//        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, locale)
//        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onResume() {
        super.onResume()
        localeDelegate.onResumed(this)
    }

    override fun onPause() {
        super.onPause()
        localeDelegate.onPaused()
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        val context = super.createConfigurationContext(overrideConfiguration)
        return LocaleHelper.onAttach(context)
    }

    override fun getApplicationContext(): Context =
        localeDelegate.getApplicationContext(super.getApplicationContext())

    open fun updateLocale(locale: Locale) {
        localeDelegate.setLocale(this, locale)
    }

    fun showFilePopup() {
        val mDialog = fileViewerPopUp
            ?: FileViewerPopUp().apply {
                isCancelable = false
            }.also { dialog ->
                fileViewerPopUp = dialog
            }
        mDialog.show(supportFragmentManager, "")
    }

    fun isTablet() =
        with(getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager) {
            this.phoneType == TelephonyManager.PHONE_TYPE_NONE
        }

    fun logScreenName(string: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, string)
        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logDeviceType(deviceType: String) {
        val bundle = Bundle()
        bundle.putString("DeviceType", deviceType)
        mFirebaseAnalytics?.setDefaultEventParameters(bundle)
    }

    fun purchaseEventPlanSubscription(secrets: String, planName: String) {
        val bundle = Bundle()
        bundle.putString("PurchaseSecret", secrets)
        bundle.putString("PlanName", planName)
        mFirebaseAnalytics?.logEvent("PlanPurchase", bundle)
    }

    fun logUserDetails(id: String, userName: String) {
        val bundle = Bundle()
        bundle.putString("UserId", id)
        bundle.putString("UserName", userName)
        mFirebaseAnalytics?.setDefaultEventParameters(bundle)
    }
}

class SummeryModel {
    var id: String? = null
    var optionId: String? = null
    var category: String? = null
    var summaryDescription: String? = null
    var summaryDescription_ES: String? = null
}
