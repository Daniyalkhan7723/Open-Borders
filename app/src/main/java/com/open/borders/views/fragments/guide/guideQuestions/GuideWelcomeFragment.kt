package com.open.borders.views.fragments.guide.guideQuestions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.customPopups.TermsAndConditionPopUp
import com.open.borders.databinding.FragmentGuideWelcomeBinding
import com.open.borders.extensions.*
import com.open.borders.koinDIModules.QuestionUpdateAddViewModelModule
import com.open.borders.models.generalModel.QuestionStateModel
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.models.generalModel.questionsApiModel.GetQuestionsModel
import com.open.borders.thirdPartyModules.htmltextview.HtmlResImageGetter
import com.open.borders.thirdPartyModules.htmltextview.HtmlTextView
import com.open.borders.utils.*
import com.open.borders.utils.Constants.Companion.BLOCK_MODE
import com.open.borders.utils.Constants.Companion.PADDING
import com.open.borders.views.activities.baseActivity.SummeryModel
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.activities.packegeDetail.PackageDetailActivity
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.module.Module
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.Iterator
import kotlin.collections.find
import kotlin.collections.first
import kotlin.collections.forEachIndexed
import kotlin.collections.isNullOrEmpty
import kotlin.collections.set


@InternalSerializationApi
class GuideWelcomeFragment :
    MainMVVMNavigationFragment<GuideQuestionsViewModel>(GuideQuestionsViewModel::class) {

    private var autTok = ""
    private var buttonText = ""
    private var termsAndCondition: String? = null
    private var termsAndConditionEs: String? = null
    var qDescription: HtmlTextView? = null
    private lateinit var sharePreferenceHelper: SharePreferenceHelper

    private lateinit var binding: FragmentGuideWelcomeBinding

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    override fun getLayoutResId() = R.layout.fragment_guide_welcome

    override fun registerModule(): Module {
        return QuestionUpdateAddViewModelModule
    }

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGuideWelcomeBinding.bind(view)
        qDescription = view.findViewById(R.id.qDescription)
        logScreenName("Guide Welcome")
        sharePreferenceHelper = SharePreferenceHelper.getInstance(requireContext())

//        val iTracker =  myCustomApp.getDefaultTracker()
//        iTracker?.setScreenName("Guide Welcome")
//        iTracker?.send(HitBuilders.ScreenViewBuilder().build())


//        binding.splashLogo?.viewTreeObserver
//            ?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    binding.splashLogo?.viewTreeObserver!!.removeOnPreDrawListener(this)
//        requireActivity().startPostponedEnterTransition()
//                    return true
//                }
//            })
//        val handler: Handler? = null
//        handler?.postDelayed(Runnable {
//            binding.splashLogo?.setImageResource(0)
//            binding.splashLogo?.viewGone()
//            binding.openBorderLogo.viewVisible()
//        }, 500)


        toolbarControl()
        getQuestionStateApi()
//        qDescription?.movementMethod = LinkMovementMethod.getInstance()

//        binding.letsStartBtn.isEnabled = false

        binding.letsStartBtn.setOnClickListener {
            buttonText = binding.letsStartBtn.text.toString()
            val firstQuestionText = mActivity.q1!!.data?.child?.first()?.text.toString()
            val firstQuestionTextEs = mActivity.q1!!.data?.child?.first()?.text_ES.toString()
            val secondQuestionText = mActivity.q2!!.data?.child?.first()?.text.toString()
            val secondQuestionTextEs = mActivity.q2!!.data?.child?.first()?.text_ES.toString()

            Log.e("buttonText", buttonText)
            Log.e("firstQText", firstQuestionText)
            Log.e("secondQText", secondQuestionText)
            Log.e("lastQuestion", mActivity.lastQuestion.toString())

//            findNavController().navigate(R.id.guideQuestionerFragment)
            if (buttonText == "Continue the guide." || buttonText == "Continúa la guía." || mActivity.lastQuestion != null) {
                val homeActivity = (activity as HomeHost).binding.homeToolbarId
                homeActivity.backArrow.viewGone()
                mActivity.previousSelectedAnswerPathLabel =
                    mActivity.tinyDB.getListString("AnswerTitle")
                findNavController().navigate(R.id.guideQuestionerFragment)
            }
            else if (buttonText == firstQuestionText || buttonText == firstQuestionTextEs) {
                if (!mActivity.previousSelectedAnswerPathLabel.contains(firstQuestionText)) {
                    mActivity.previousSelectedAnswerPathLabel.add(firstQuestionText)
                }

                savePathData("${mActivity.q1?.id}", "${mActivity.q1?.data?.child?.first()?.id}")

                val homeActivity = (activity as HomeHost).binding.homeToolbarId
                homeActivity.backArrow.viewVisible()
                when (mActivity.getCurrentLanguage()) {
                    "es" -> {
                        qDescription?.setHtml(
                            Html.fromHtml(mActivity.q2!!.data?.description_ES).toString(),
                            HtmlResImageGetter(requireContext()), mActivity
                        )

                        mActivity.q2!!.data?.child?.first().let {
                            binding.letsStartBtn.text = it?.text_ES.toString()
                        }
                    }
                    else -> {
                        qDescription?.setHtml(
                            Html.fromHtml(mActivity.q2!!.data?.description).toString(),
                            HtmlResImageGetter(requireContext()), mActivity
                        )
                        mActivity.q2!!.data?.child?.first().let {
                            binding.letsStartBtn.text = it?.text.toString()
                        }
                    }
                }
            } else if (buttonText == secondQuestionText || buttonText == secondQuestionTextEs) {
                val popup = TermsAndConditionPopUp(mActivity, object :
                    TermsAndConditionPopUp.TermAndCheckInterface {
                    override fun onCheckClickYes() {
                        savePathData(
                            "${mActivity.q2?.id}",
                            "${mActivity.q2?.data?.child?.first()?.id}"
                        )
                        val targetNode = mActivity.q2?.data?.child?.first()?.targetNode
                        mActivity.lastQuestion = targetNode
                        mActivity.tinyDB.putString("lastQuestion", targetNode)
                        mActivity.questionsOrder.add(targetNode ?: "")
                        findNavController().navigate(R.id.guideQuestionerFragment)
                    }
                })
                popup.show(childFragmentManager, "")
            }
        }

        val homeActivity = (activity as HomeHost).binding.homeToolbarId
        homeActivity.backArrow.setOnClickListener {
            homeActivity.backArrow.viewGone()
            when (mActivity.getCurrentLanguage()) {
                "es" -> {
                    qDescription?.setHtml(
                        Html.fromHtml(mActivity.q1!!.data?.description_ES).toString(),HtmlResImageGetter(requireContext()), mActivity
                    )
                    mActivity.q1!!.data?.child?.first().let {
                        binding.letsStartBtn.text = it?.text_ES.toString()
                    }
                }
                else -> {
                    qDescription?.setHtml(
                        Html.fromHtml(mActivity.q1!!.data?.description).toString(),
                        HtmlResImageGetter(requireContext()), mActivity
                    )
                    mActivity.q1!!.data?.child?.first().let {
                        binding.letsStartBtn.text = it?.text.toString()
                    }
                }
            }
        }


//      binding.letsStartBtn.isEnabled = true


    }

    private fun setQuestions() {
        binding.shimmerFrameLayout.viewGone()
        binding.questionLayout.viewVisible()

        val questionJson: String = Gson().toJson(sharePreferenceHelper.getQuestion())
        mActivity.tinyDB.putString("questionJson", questionJson)

        mActivity.updatedQuestionsList =
            sharePreferenceHelper.getQuestion()?.questions?.toMutableList()!!
        mActivity.updatedQuestionsList.forEachIndexed { idx, data: GuideQuestionsModelItem ->
            mActivity.updatedQuestionsMap[data.id] = data
        }

        mActivity.q1 = mActivity.updatedQuestionsList.first()
        mActivity.q2 = mActivity.updatedQuestionsMap[mActivity.q1?.data?.child?.first()?.targetNode]

        if (!mActivity.updatedQuestionsList.isNullOrEmpty()) {
            val terms: GuideQuestionsModelItem? = mActivity.updatedQuestionsList.find {
                it.id == "08d6214d-638d-4e35-aee4-2c015ebda981"
            }
            termsAndCondition = terms?.data?.description.toString()
            termsAndConditionEs = terms?.data?.description_ES.toString()
    //                    Log.e("TermsAndCondition", termsAndCondition!!)
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTermsAndCondition(
                    termsAndCondition!!
                )
                SharePreferenceHelper.getInstance(requireContext())
                    .saveTermsAndConditionEs(termsAndConditionEs!!)
            }
        }

        if (mActivity.lastQuestion == null) {
            when (mActivity.getCurrentLanguage()) {
                "es" -> {
                    qDescription?.setHtml(
                        Html.fromHtml(mActivity.q1!!.data?.description_ES).toString(),
                        HtmlResImageGetter(requireContext()),
                        mActivity
                    )
                    mActivity.q1!!.data?.child?.first().let {
                        binding.letsStartBtn.text = it?.text_ES.toString()
                    }
                }
                else -> {
                    qDescription?.setHtml(
                        Html.fromHtml(mActivity.q1!!.data?.description).toString(),
                        HtmlResImageGetter(requireContext()), mActivity
                    )
                    mActivity.q1!!.data?.child?.first().let {
                        binding.letsStartBtn.text = it?.text.toString()
                    }
                }
            }
        } else {
            when (mActivity.getCurrentLanguage()) {
                "es" -> {
                    qDescription?.setHtml(
                        Html.fromHtml(mActivity.q1!!.data?.description_ES).toString(),
                        HtmlResImageGetter(requireContext()), mActivity
                    )
                    mActivity.q1!!.data?.child?.first().let {
                        binding.letsStartBtn.text = it?.text_ES.toString()
                    }
                }
                else -> {
                    qDescription?.setHtml(
                        Html.fromHtml(mActivity.q1!!.data?.description).toString(),
                        HtmlResImageGetter(requireContext()), mActivity
                    )
                    mActivity.q1!!.data?.child?.first().let {
                        binding.letsStartBtn.text = "Continue the guide."
    //                        binding.letsStartBtn.text = it?.text.toString()
                    }
                }
            }
        }
    }

    private fun getQuestionAuthToken() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getQuestionAuthToken()
        } else {
            CustomToasts.showToast(
                requireActivity(),
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }

    private fun getQuestionFromAPi(token: String) {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getQuestions(token)
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

//        observe(viewModel.questionAuthToken) {
//            if (it.status == true) {
//                autTok = it.token.toString()
//                getQuestionFromAPi(autTok)
//            }
//        }

        observe(viewModel.getQuestionStateLiveData) {
            if (it.status) {
                mActivity.lastQuestion = null
                mActivity.evaluatedSummeryList.clear()
                mActivity.questionsOrder.clear()
                mActivity.traversedPathMap.clear()
//                binding.openBorderLogo.viewVisible()
//                binding.splashLogo?.viewGone()
//                binding.splashLogo?.setImageResource(0)
                if (it.data != null) {
                    mapData(it.data) {
                    }
                }
                setQuestions()
//                autTok = SharePreferenceHelper.getInstance(requireContext()).getQuestionAuthToken()
//                    .toString()
//                getQuestionFromAPi(autTok)
            }
        }


//        observe(viewModel.getQuestions) {
//            if (it.status == true) {
//                binding.letsStartBtn.isEnabled = true
//                binding.shimmerFrameLayout.viewGone()
//                binding.questionLayout.viewVisible()
//
//                val encrypted = it.data
//                var decrypted = ""
//                var decryptedQuestions: GetQuestionsModel? = null
//                try {
//                    decrypted = CryptoAES.decrypt("open_border_8824714592", encrypted!!)
//                    decryptedQuestions = Gson().fromJson(decrypted, GetQuestionsModel::class.java)
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//
//                val questionJson: String = Gson().toJson(decryptedQuestions)
//                mActivity.tinyDB.putString("questionJson", questionJson)
//
//                mActivity.updatedQuestionsList = decryptedQuestions?.questions?.toMutableList()!!
//                mActivity.updatedQuestionsList.forEachIndexed { idx, data: GuideQuestionsModelItem ->
//                    mActivity.updatedQuestionsMap[data.id] = data
//                }
//
//                mActivity.q1 = mActivity.updatedQuestionsList.first()
//                mActivity.q2 =
//                    mActivity.updatedQuestionsMap[mActivity.q1?.data?.child?.first()?.targetNode]
//
//                if (!mActivity.updatedQuestionsList.isNullOrEmpty()) {
//                    val terms: GuideQuestionsModelItem? = mActivity.updatedQuestionsList.find {
//                        it.id == "08d6214d-638d-4e35-aee4-2c015ebda981"
//                    }
//                    termsAndCondition = terms?.data?.description.toString()
//                    termsAndConditionEs = terms?.data?.description_ES.toString()
////                    Log.e("TermsAndCondition", termsAndCondition!!)
//                    lifecycleScope.launch {
//                        SharePreferenceHelper.getInstance(requireContext()).saveTermsAndCondition(
//                            termsAndCondition!!
//                        )
//                        SharePreferenceHelper.getInstance(requireContext())
//                            .saveTermsAndConditionEs(termsAndConditionEs!!)
//                    }
//                }
//
//
//                if (mActivity.lastQuestion == null) {
//                    when (mActivity.getCurrentLanguage()) {
//                        "es" -> {
//                            qDescription?.setHtml(
//                                Html.fromHtml(mActivity.q1!!.data?.description_ES).toString(),
//                                HtmlResImageGetter(requireContext()), mActivity
//                            )
//                            mActivity.q1!!.data?.child?.first().let {
//                                binding.letsStartBtn.text = it?.text_ES.toString()
//                            }
//                        }
//                        else -> {
//                            qDescription?.setHtml(
//                                Html.fromHtml(mActivity.q1!!.data?.description).toString(),
//                                HtmlResImageGetter(requireContext()), mActivity
//                            )
//                            mActivity.q1!!.data?.child?.first().let {
//                                binding.letsStartBtn.text = it?.text.toString()
//                            }
//                        }
//                    }
//                } else {
//                    when (mActivity.getCurrentLanguage()) {
//                        "es" -> {
//                            qDescription?.setHtml(
//                                Html.fromHtml(mActivity.q1!!.data?.description_ES).toString(),
//                                HtmlResImageGetter(requireContext()), mActivity
//                            )
//                            mActivity.q1!!.data?.child?.first().let {
//                                binding.letsStartBtn.text = "Continúa la guía."
//                            }
//                        }
//                        else -> {
//                            qDescription?.setHtml(
//                                Html.fromHtml(mActivity.q1!!.data?.description).toString(),
//                                HtmlResImageGetter(requireContext()), mActivity
//                            )
//                            mActivity.q1!!.data?.child?.first().let {
//                                binding.letsStartBtn.text = "Continue the guide."
//                            }
//                        }
//                    }
//                }
//            }
//        }

        observe(viewModel.planDetails) {
            if (it.status) {
                Log.e("Observer", "called")
                startActivity(
                    Intent(
                        requireContext(),
                        PackageDetailActivity::
                        class.java
                    ).putExtra("myObject", it.data)
                        .putExtra("fromFragment", 3)
                )

            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        val homeActivity = (activity as HomeHost).binding.homeToolbarId
        homeActivity.toolbarNameTv.text = getString(R.string.guide)
        homeActivity.backArrow.viewGone()
        homeActivity.llChangeLanguage.setOnClickListener {
            (activity as HomeHost).changeLanguageMenu()
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTabType("welcome")
            }
        }
    }

    private fun getQuestionStateApi() {
        //Get Question Api Call
        if (isNetworkAvailable(requireContext())) {
            viewModel.getQuestionState(
                SharePreferenceHelper.getInstance(requireContext()).getUser().id.toString()
            )
        } else {
            CustomToasts.showToast(
                requireActivity(),
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
                mActivity.traversedPathMap["$key"] = "$value"
            }
            mActivity.questionsOrder.clear()
            val questionsOrderArray = JSONArray(data.questions_order)
            for (i in 0 until questionsOrderArray.length()) {
                mActivity.questionsOrder.add("${questionsOrderArray[i]}")
            }

            val summeryArray = JSONArray(data.current_summary)
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
                    mActivity.evaluatedSummeryList.add(summeryModel)
                    Log.e("SumDesc", "${summeryModel.summaryDescription}")
                }
            }
            mActivity.lastQuestion = data.last_question ?: ""
        } catch (e: Exception) {
//            mActivity.lastQuestion = data.last_question ?: ""
            e.printStackTrace()
        }
        Log.e("restoredAnswers", "${mActivity.traversedPathMap.size}")
        Log.e("restoredSummaries", "${mActivity.evaluatedSummeryList.size}")
        Log.e("questionsOrder", "${mActivity.questionsOrder.size}")
        Log.e("lastQuestion", mActivity.lastQuestion ?: "")
        callback(true)
    }

    fun savePathData(qId: String, optionId: String) {
        if (mActivity.traversedPathMap.containsKey(qId) && !(mActivity.traversedPathMap.get(
                qId
            )?.contains(optionId ?: "") == true)
        ) {
            val multipleAnswers =
                mActivity.traversedPathMap.get(qId) + "~" + optionId
            mActivity.traversedPathMap.put(qId, multipleAnswers)
            Log.e("multipleMapData", "${qId}, $multipleAnswers")
        } else if (!mActivity.traversedPathMap.containsKey(qId)) {
            mActivity.traversedPathMap.put(
                qId,
                optionId
            )
            Log.e(
                "singleMapData",
                "${qId}, $optionId"
            )

            Log.e("path", mActivity.traversedPathMap.toString())
        }
    }


    override fun onResume() {
        super.onResume()
        logScreenName("Guide Welcome")
    }
}