package com.open.borders.views.fragments.baseFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.analytics.FirebaseAnalytics
import com.open.borders.backend.MyCustomApp
import com.open.borders.customPopups.FileViewerPopUp
import com.open.borders.utils.AlertMessageDialog
import com.open.borders.utils.TinyDB
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

@InternalSerializationApi
abstract class BaseFragment : Fragment() {
    private var mPreviousView: View? = null
    private var fileViewerPopUp: FileViewerPopUp? = null
    private var mModule: Module? = null
    lateinit var mActivity: BaseActivity
    lateinit var mContext: Context
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    lateinit var tinyDB: TinyDB


    val myCustomApp by lazy {
        requireContext().applicationContext as MyCustomApp
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as BaseActivity
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun inOnCreateView(
        mRootView: ViewGroup, savedInstanceState: Bundle?
    )

    open fun themeInflater(baseInflater: LayoutInflater): LayoutInflater? = null

    open fun initializeView(view: View) {}

    open fun registerModule(): Module? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
//        return super.onCreateView(inflater, container, savedInstanceState)
        mPreviousView?.let { return it }
        val newInflater = themeInflater(inflater) ?: inflater
        val mView = newInflater.inflate(getLayoutResId(), container, false)
        initializeView(mView)

        if (!::tinyDB.isInitialized)
            tinyDB = TinyDB(requireContext())

        val module = registerModule()
        if (module != null)
            loadKoinModules(module).also { mModule = module }

        inOnCreateView(mView as ViewGroup, savedInstanceState)
        return mView.also {
            mPreviousView = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mModule?.let { unloadKoinModules(it) }
    }

    fun showAlertDialog(
        msg: String,
        positiveButtonCallback: AlertMessageDialog.PositiveButtonCallback? = null
    ) {
        AlertMessageDialog.newInstance(msg, positiveButtonCallback)
            .show(childFragmentManager, AlertMessageDialog.TAG)
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }

    protected fun <T, LD : LiveData<T>> observe(liveData: LD, onChanged: (T) -> Unit) {
        liveData.observe(this, Observer {
            it?.let(onChanged)
        })
    }

    fun showFilePopup() {
        val mDialog = fileViewerPopUp
            ?: FileViewerPopUp().apply {
                isCancelable = false
            }.also { dialog ->
                fileViewerPopUp = dialog
            }
        mDialog.show(childFragmentManager, "")
    }

    fun logScreenName(string: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, string)
        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logQuestionAndId(id: String, name: String) {
        val bundle = Bundle()
        bundle.putString("QuestionNodeId", id)
        bundle.putString("QuestionTitle", name)
        mFirebaseAnalytics?.logEvent("QuestionView", bundle)
    }

    fun purchaseEventPlan(bookingStatus: String) {
        val bundle = Bundle()
        bundle.putString("ConsultationBookingStatus", bookingStatus)
        mFirebaseAnalytics?.logEvent("NewConsultationBooking", bundle)
    }
}