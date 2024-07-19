package com.open.borders.views.fragments.guide.guideQuestions

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.customPopups.BugsReportPopUp
import com.open.borders.customPopups.EvaluationPopUp
import com.open.borders.customPopups.RestartPopUp
import com.open.borders.databinding.FragmentGuideQuestionerBinding
import com.open.borders.extensions.isNetworkAvailable
import com.open.borders.extensions.setupProgressDialog
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.koinDIModules.QuestionUpdateAddViewModelModule
import com.open.borders.models.generalModel.guideQuestionsModels.ActionLinkModel
import com.open.borders.models.generalModel.guideQuestionsModels.Child
import com.open.borders.models.generalModel.guideQuestionsModels.Condition
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.utils.*
import com.open.borders.views.activities.baseActivity.SummeryModel
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.activities.packegeDetail.PackageDetailActivity
import com.open.borders.views.fragments.MVVMFactory.MainMVVMNavigationFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

@InternalSerializationApi
class GuideQuestionerFragment :
    MainMVVMNavigationFragment<GuideQuestionsViewModel>(GuideQuestionsViewModel::class) {
    private lateinit var binding: FragmentGuideQuestionerBinding

//    private var questionsList = mutableListOf<GuideQuestionsModelItem>()

    override fun getLayoutResId() = R.layout.fragment_guide_questioner

    private var autTok = ""
    private var currentNodeHeading = ""
    private var isScrolling: Boolean? = false

    override fun registerModule(): Module {
        return QuestionUpdateAddViewModelModule
    }

    override fun inOnCreateView(mRootView: ViewGroup, savedInstanceState: Bundle?) {
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGuideQuestionerBinding.bind(view)
        attachViewModel()
        logScreenName("Guide Questioner")



        toolbarControl()
        listeners()
//        getQuestionAuthToken()
        showHideBackArrow()

//        val current = mActivity.questionsOrder.last()
//        Log.e("oldSummery", "${mActivity.evaluatedSummeryList.size}")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            mActivity.evaluatedSummeryList.removeIf { it.id == current }
//        }
//        Log.e("newSummery", "${mActivity.evaluatedSummeryList.size}")
//        binding.rvQuestions.addOnScrollHiddenView(binding.backButton)

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter() {
        binding.rvQuestions.let {
            it.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            it.adapter = questionsAdapter
        }

        if (binding.rvQuestions.onFlingListener == null) {
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(binding.rvQuestions)
        }


//        val current = mActivity.questionsOrder.last()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listeners() {
        binding.backButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.backButton.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.actionlink_bg)
                    binding.tvBackButton?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )
                    binding.icBackButton?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.white))
                        )
                    }

                }
                MotionEvent.ACTION_UP -> {
                    binding.backButton.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvBackButton?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icBackButton?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }
                    onBackAndSwipe()
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.backButton.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvBackButton?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icBackButton?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }

                }
            }
            true
        }

        binding.saveAndContinue.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.saveAndContinue.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.actionlink_bg)
                    binding.tvSaveCont?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )
                    binding.icSaveCont?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.white))
                        )
                    }

                }
                MotionEvent.ACTION_UP -> {
                    binding.saveAndContinue.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvSaveCont?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icSaveCont?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }

                    // generate json of traversed path map
                    generateJsonOfTraversedPathMap() { traversedPathJson ->
                        generateJsonOfSummerDescription() { summerDescriptionJson ->

                            //callAPI to send generated data on server
                            //prev_selections = traversedPathJson
                            //current_summary = summerDescriptionJson
                            //last_question = selectedAnswer.targetNode
                            //user_id = mActivity.user.id

                            mActivity.tinyDB.putString("traversedPathJson", traversedPathJson)


                            if (mActivity.previousSelectedAnswerPathLabel.isNotEmpty()) {
                                mActivity.tinyDB.putListString(
                                    "AnswerTitle",
                                    mActivity.previousSelectedAnswerPathLabel
                                )
                            }

                            val questionOrders = JSONArray()
                            mActivity.questionsOrder.forEachIndexed { index, s ->
                                Log.e("orderJson", s)
                                questionOrders.put(s)
                            }

                            if (isNetworkAvailable(requireContext())) {
                                viewModel.questionUpdateOrAdd(
                                    mActivity.sharedPreference?.getUser()?.id.toString(),
                                    traversedPathJson,
                                    mActivity.lastQuestion ?: "",
                                    "$questionOrders",
                                    summerDescriptionJson
                                )
                            } else {
                                CustomToasts.showToast(
                                    requireActivity(),
                                    Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                                    false
                                )
                            }
                        }
                    }

                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.saveAndContinue.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvSaveCont?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icSaveCont?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }

                }
            }
            true
        }

        binding.restartButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.restartButton.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.actionlink_bg)
                    binding.tvRestart?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )
                    binding.icRestart?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.white))
                        )
                    }

                }
                MotionEvent.ACTION_UP -> {
                    binding.restartButton.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvRestart?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icRestart?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }
                    val popup = RestartPopUp(object : RestartPopUp.RestartInterface {
                        override fun onPopUpYesClick() {
                            //Delete Question Api Call
                            val id =
                                SharePreferenceHelper.getInstance(requireContext())
                                    .getUser().id.toString()
                            if (isNetworkAvailable(requireContext())) {
                                viewModel.deleteQuestioner(id)
                            } else {
                                CustomToasts.showToast(
                                    requireActivity(),
                                    Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                                    false
                                )
                            }
                        }
                    })
                    popup.show(childFragmentManager, "RestartPopUp")
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.restartButton.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvRestart?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icRestart?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }

                }
            }
            true
        }

        binding.reportBug?.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.reportBug?.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.actionlink_bg)
                    binding.tvBugReport?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )
                    binding.icBugReport?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.white))
                        )
                    }

                }
                MotionEvent.ACTION_UP -> {
                    binding.reportBug!!.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvBugReport?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icBugReport?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }
                    Log.e("LastQuestionLabel", currentNodeHeading)

                    val popup = BugsReportPopUp(object : BugsReportPopUp.BugsReportInterface {
                        override fun onSendButtonClick(description: String) {
                            if (isNetworkAvailable(requireContext())) {
//                        mActivity.getTitlesOfQuestionOrder() {
//                            Log.e("SelectedAnswerTitles", it.toString())

                                val titleJson =
                                    Gson().toJson(mActivity.previousSelectedAnswerPathLabel)
                                Log.e(
                                    "AnswerPath",
                                    mActivity.previousSelectedAnswerPathLabel.toString()
                                )
                                viewModel.bugReport(titleJson, description, currentNodeHeading)
//                        }
                            }
                        }

                        override fun onHelpEmailClick(Email: String) {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$Email")
                            }
                            startActivity(Intent.createChooser(emailIntent, "Send feedback"))
                        }

                        override fun onPhoneCallClick(Phone: String) {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel: $Phone")
                            startActivity(intent)
                        }

                        override fun onConsultationClick() {
                            (requireActivity() as HomeHost).consultationTab()
                            (requireActivity() as HomeHost).navigateToConsultation()
                        }
                    })
                    popup.show(childFragmentManager, "")
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.reportBug!!.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.questioner_btm_button_bg)
                    binding.tvBugReport?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.brown_new
                        )
                    )
                    binding.icBugReport?.let {
                        ImageViewCompat.setImageTintList(
                            it,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.brown_new
                                )
                            )
                        )
                    }
                }
            }
            true
        }

        (requireActivity() as HomeHost).binding.homeToolbarId.backArrow.setOnClickListener {
            onBackAndSwipe()
        }

        binding.evaluationLayoutId.setOnClickListener {
//            mActivity.evaluatedSummeryList
            val popup = EvaluationPopUp(mActivity.evaluatedSummeryList, mActivity, object :
                EvaluationPopUp.Liteners {
                override fun destroyed(frag: DialogFragment) {
                    childFragmentManager.beginTransaction().remove(frag)
                        .commitAllowingStateLoss()
                }

                override fun onEmailSendAndDelete() {
                    //Delete Question Api Call
                    val id =
                        SharePreferenceHelper.getInstance(requireContext())
                            .getUser().id.toString()
                    if (isNetworkAvailable(requireContext())) {
                        viewModel.deleteQuestioner(id)
                    } else {
                        CustomToasts.showToast(
                            requireActivity(),
                            Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                            false
                        )
                    }
                }
            })
            popup.show(childFragmentManager, "EvaluationPopUp")
        }


        binding.mainSwipe.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeRight() {
                if (mActivity.questionsOrder.size > 1) {
                    super.onSwipeRight()
                    onBackAndSwipe()
                }
            }
        })

    }

    private fun generateJsonOfTraversedPathMap(callback: (String) -> Unit) {
        val obj = JSONObject()
        mActivity.traversedPathMap.forEach { (t, u) ->
            obj.put(t ?: "", u)
        }
        val json = "$obj"
        callback(json)
        Log.e("traversedPathJson", json)
    }

    private fun generateJsonOfSummerDescription(callback: (String) -> Unit) {
        val parentJasonArray = JSONArray()
        mActivity.evaluatedSummeryList.forEachIndexed { index, summeryModel ->
            val obj = JSONObject()
            obj.put("id", summeryModel.id)
            obj.put("option_id", summeryModel.optionId)
            obj.put("category", summeryModel.category)
            obj.put("description", summeryModel.summaryDescription)
            obj.put("description_ES", summeryModel.summaryDescription_ES)
            parentJasonArray.put(obj)
        }
        Log.e("evaluationJson", "$parentJasonArray")
        callback("$parentJasonArray")
    }

    private fun attachViewModel() {

        observe(viewModel.snackBarMessage) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                CustomToasts.showToast(requireActivity(), msg, false)
            }
        }

        binding.shimmerFrameLayout.viewGone()
        binding.rvQuestions.viewVisible()
        if (mActivity.evaluatedSummeryList.size > 0) {
            binding.tvEvaluationCount.viewVisible()
            binding.evaluationLayoutId.viewVisible()
            binding.tvEvaluationCount.text =
                mActivity.evaluatedSummeryList.size.toString()
        } else {
//            binding.evaluationLayoutId.viewGone()
            binding.tvEvaluationCount.viewGone()
        }
        getAllQuestions { setAdapter() }

        observe(viewModel.questionAddOrUpdateLiveData) {
            if (it.status) {
                CustomToasts.showToast(
                    requireActivity(),
                    "Your answers are saved.\nYou may continue the guide later.",
                    true
                )
            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
            }
            findNavController().navigate(R.id.guideWelcomeFragment)
        }

        observe(viewModel.questionnaireDelete) {
            if (it.status) {
//                binding.evaluationLayoutId.viewGone()
                mActivity.tinyDB.clear()
                mActivity.previousSelectedAnswerPathLabel.clear()
                mActivity.evaluatedSummeryList.clear()
                mActivity.traversedPathMap.clear()
                mActivity.questionsOrder.clear()
                mActivity.updatedQuestionsList.clear()
                mActivity.updatedQuestionsMap.clear()
                mActivity.lastQuestion = null
//                CustomToasts.showToast(requireActivity(), "Successfully Deleted", true)
                binding.rvQuestions.viewGone()
                binding.shimmerFrameLayout.viewVisible()
                findNavController().navigate(R.id.guideWelcomeFragment)
//                getQuestionFromAPi(autTok)

//                getAllQuestions {
//                    setAdapter()
//                }

            }
        }
        observe(viewModel.bugReportLiveData) {
            if (it.status) {
                CustomToasts.showToast(requireActivity(), it.message, true)
            }
        }
//        observe(viewModel.getQuestions) {
//            if (it.status == true) {
//                binding.shimmerFrameLayout.viewGone()
//                binding.rvQuestions.viewVisible()
//                if (mActivity.evaluatedSummeryList.size > 0) {
//                    binding.evaluationLayoutId.viewVisible()
//                    binding.tvEvaluationCount.text =
//                        mActivity.evaluatedSummeryList.size.toString()
//                } else {
//                    binding.evaluationLayoutId.viewGone()
//                    binding.tvEvaluationCount.text = "0"
//                }
////                CustomToasts.showToast(requireActivity(), "Questions fetched Successfully", true)
//                questionsList = it.data?.questions?.toMutableList()!!
//
//            }
//        }

        observe(viewModel.planDetails) {
            if (it.status) {
                Log.e("Observer", "called")
                startActivity(
                    Intent(requireContext(), PackageDetailActivity::class.java).putExtra("myObject", it.data)
                        .putExtra("fromFragment", 3)
                )

            } else {
                CustomToasts.showToast(requireActivity(), it.message, false)
                (requireActivity() as HomeHost).servicesTab()
                (requireActivity() as HomeHost).navigateToServices()

            }
        }
    }
//
//    private fun getQuestionAuthToken() {
//        if (isNetworkAvailable(requireContext())) {
//            viewModel.getQuestionAuthToken()
//        } else {
//            CustomToasts.showToast(
//                requireActivity(),
//                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
//                false
//            )
//        }
//    }

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

    private fun getAllQuestions(callback: (Boolean) -> Unit) {
        questionsAdapter.questionList = ArrayList()
        setAdapter()


        Log.e("restoredAnswers", "${mActivity.traversedPathMap.size}")
        Log.e("restoredSummaries", "${mActivity.evaluatedSummeryList.size}")
        Log.e("lastQuestion", mActivity.lastQuestion ?: "")

        if (mActivity.lastQuestion == null && mActivity.updatedQuestionsList.size > 0) {
            mActivity.lastQuestion = mActivity.updatedQuestionsList.first().id ?: ""
        }
        if (mActivity.questionsOrder.size == 0 && mActivity.updatedQuestionsList.size > 0) {
            mActivity.questionsOrder.add(mActivity.updatedQuestionsList.first().id ?: "")
        }

        showHideBackArrow()

        lateinit var nextQuestion: GuideQuestionsModelItem
        nextQuestion =
            mActivity.updatedQuestionsMap[mActivity.lastQuestion] ?: GuideQuestionsModelItem()
        val conditionList: MutableList<GuideQuestionsModelItem>
        conditionList = ArrayList()

        if ((mActivity.updatedQuestionsMap.get(mActivity.lastQuestion)
                ?: GuideQuestionsModelItem()).data?.conditions?.size ?: 0 > 0
        ) {
            Log.e("conditionSize", "${nextQuestion.data?.conditions?.size}")

            nextQuestion.data?.conditions?.forEachIndexed { index, condition ->
                if (evaluateSingleItem(condition)) {
                    conditionList.add(GuideQuestionsModelItem().apply {
                        this.id = nextQuestion.id
                        this.data?.label = condition.conditionLabel
                        this.data?.description = condition.conditionDescription
                        this.data?.description_ES = condition.conditionDescription_ES
                        this.data?.child = condition.conditionOptions ?: ArrayList()
                        this.data?.actionLink = condition.actionLink
                    })
                }
            }
        } else {
            nextQuestion = mActivity.updatedQuestionsMap.get(mActivity.lastQuestion)
                ?: GuideQuestionsModelItem()
        }

        if (conditionList.size == 0) {
            questionsAdapter.questionList.add(nextQuestion)
        } else {
            questionsAdapter.questionList.addAll(conditionList)
        }

        callback(true)
    }

    private fun showHideSummeryButton() {
        if (mActivity.evaluatedSummeryList.size > 0) {
            binding.tvEvaluationCount.viewVisible()
            binding.evaluationLayoutId.viewVisible()
            binding.tvEvaluationCount.text =
                mActivity.evaluatedSummeryList.size.toString()
        } else {
//            binding.evaluationLayoutId.viewGone()
            binding.tvEvaluationCount.viewGone()
        }
    }

    private fun showHideBackArrow() {
        if (mActivity.questionsOrder.size > 1) {
            (mActivity as HomeHost).binding.homeToolbarId.backArrow.viewVisible()
            binding.rlBottom.viewVisible()
        } else {
            (mActivity as HomeHost).binding.homeToolbarId.backArrow.viewGone()
            binding.rlBottom.viewGone()
        }
        showHideSummeryButton()
    }

    private fun onBackAndSwipe() {
        binding.rvQuestions.stopScroll()
        binding.rvQuestions.recycledViewPool.clear()
        questionsAdapter.notifyDataSetChanged()
        if (mActivity.previousSelectedAnswerPathLabel.size > 0) {
            mActivity.previousSelectedAnswerPathLabel.removeLast()
        }
        try {
            if (mActivity.traversedPathMap.size > 0) {
                mActivity.updatedQuestionsMap[mActivity.questionsOrder.last()]?.data?.child?.forEach { child ->
                    child.isSelected = false
                }

                Log.e("size", "${mActivity.questionsOrder.size}")
                val current = mActivity.questionsOrder.last()
                Log.e("removed", current)
                mActivity.questionsOrder.removeLast()
                Log.e("size", "${mActivity.questionsOrder.size}")
                mActivity.lastQuestion = mActivity.questionsOrder.last()
                Log.e("newCurrent", "${mActivity.lastQuestion}")
                Log.e("oldTraversedPathMap", "${mActivity.traversedPathMap.size}")
                mActivity.traversedPathMap.remove(current)
                mActivity.traversedPathMap.remove(mActivity.questionsOrder.last())
                Log.e("newTraversedPathMap", "${mActivity.traversedPathMap.size}")
                Log.e("oldSummery", "${mActivity.evaluatedSummeryList.size}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mActivity.evaluatedSummeryList.removeIf { it.id == current }
                }
                Log.e("newSummery", "${mActivity.evaluatedSummeryList.size}")

                mActivity.tinyDB.putListString("questionOrder", mActivity.questionsOrder)
                mActivity.tinyDB.putString("lastQuestion", mActivity.lastQuestion)


                questionsAdapter.questionList.clear()

                lateinit var nextQuestion: GuideQuestionsModelItem
                nextQuestion = mActivity.updatedQuestionsMap[mActivity.lastQuestion]
                    ?: GuideQuestionsModelItem()
                val conditionList: MutableList<GuideQuestionsModelItem>
                conditionList = ArrayList()

                currentNodeHeading = nextQuestion.data?.label.toString()
                Log.e("OnBackCurrentNodeLabel", currentNodeHeading)

                if ((mActivity.updatedQuestionsMap.get(mActivity.lastQuestion)
                        ?: GuideQuestionsModelItem()).data?.conditions?.size ?: 0 > 0
                ) {
                    Log.e("conditionSize", "${nextQuestion.data?.conditions?.size}")

                    nextQuestion.data?.conditions?.forEachIndexed { index, condition ->
                        if (evaluateSingleItem(condition)) {
                            conditionList.add(GuideQuestionsModelItem().apply {
                                this.id = nextQuestion.id
                                this.data?.label = condition.conditionLabel
                                this.data?.description = condition.conditionDescription
                                this.data?.description_ES = condition.conditionDescription_ES
                                this.data?.child = condition.conditionOptions ?: ArrayList()
                                this.data?.actionLink = condition.actionLink
                            })
                        }
                    }
                } else {
                    nextQuestion = mActivity.updatedQuestionsMap.get(mActivity.lastQuestion)
                        ?: GuideQuestionsModelItem()
                }
                questionsAdapter.questionList = ArrayList()
                if (conditionList.size == 0) {
                    questionsAdapter.questionList.add(
                        0,
                        nextQuestion
                    )
                } else {
                    questionsAdapter.questionList.addAll(
                        0,
                        conditionList
                    )
                }
                Log.e("size", "${questionsAdapter.questionList.size}")

                lifecycleScope.launch {
                    delay(100)
                    questionsAdapter.notifyDataSetChanged()
                }
                Log.e("size", "${questionsAdapter.questionList.size}")
                generateJsonOfTraversedPathMap() { traversedPathJson ->
                    generateJsonOfSummerDescription() { summerDescriptionJson ->

                        //callAPI to send generated data on server
                        //prev_selections = traversedPathJson
                        //current_summary = summerDescriptionJson
                        //last_question = selectedAnswer.targetNode
                        //user_id = mActivity.user.id

                        mActivity.tinyDB.putString("traversedPathJson", traversedPathJson)
                        mActivity.tinyDB.putString("summeryJson", summerDescriptionJson)

                        val questionOrders = JSONArray()
                        mActivity.questionsOrder.forEachIndexed { index, s ->
                            questionOrders.put(s)
                        }
                    }
                }

                showHideBackArrow()

                if (mActivity.evaluatedSummeryList.size > 0) {
                    binding.evaluationLayoutId.viewVisible()
                    binding.tvEvaluationCount.viewVisible()
                    binding.tvEvaluationCount.text =
                        mActivity.evaluatedSummeryList.size.toString()
                } else {
//                    binding.evaluationLayoutId.viewGone()
                    binding.tvEvaluationCount.viewGone()
                }
            }
        } catch (e: Exception) {
            Log.e("OnBackException", e.toString())
        }
    }

    private fun getConditionBasedNextQuestion(
        selectedAnswer: Child,
        callback: (MutableList<GuideQuestionsModelItem>) -> Unit
    ) {
        var nextQuestion: GuideQuestionsModelItem? = null
        nextQuestion =
            mActivity.updatedQuestionsMap[selectedAnswer.targetNode] ?: GuideQuestionsModelItem()
        val conditionList: MutableList<GuideQuestionsModelItem>
        conditionList = ArrayList()

        if (nextQuestion.data?.conditions?.size ?: 0 > 0) {
            Log.e("conditionSize", "${nextQuestion.data?.conditions?.size}")
            nextQuestion.data?.conditions?.forEachIndexed { index, condition ->
                if (evaluateSingleItem(condition)) {
                    conditionList.add(GuideQuestionsModelItem().apply {
                        this.id = nextQuestion.id
                        this.data?.label = condition.conditionLabel
                        this.data?.description = condition.conditionDescription
                        this.data?.description_ES = condition.conditionDescription_ES
                        this.data?.child = condition.conditionOptions ?: ArrayList()
                        this.data?.actionLink = condition.actionLink
                    })
                }
            }

            if (conditionList.isNotEmpty())
                callback(conditionList)
            else {
                conditionList.add(nextQuestion)
                callback(conditionList)
            }
        } else {
            if (selectedAnswer.targetNode != null) {
                conditionList.add(nextQuestion)
                callback(conditionList)
            }
        }
    }

    private fun evaluateSingleItem(condition: Condition): Boolean {
        var ids = ArrayList<String>()
        if (condition.condition != null) {

            if (condition.condition.contains(">")) {
                ids = (condition.condition ?: "").split(">") as ArrayList
            } else if (condition.condition.contains("&&") || condition.condition.contains("||") || condition.condition.isNotEmpty()) {
                ids.add((condition.condition ?: ""))
            } else {
                ids.add(evaluateSingleOrWithoutAnd(condition.condition))
            }
            for (i in 0 until ids.count()) {
                Log.e("conditions", ids[i])
                if (ids[i].contains("||")) {
                    val splittedWithAnd = ids[i].split("||") as ArrayList
                    for (j in 0 until splittedWithAnd.count()) {
                        if (splittedWithAnd[j].contains("&&")) {
                            splittedWithAnd[j] = evaluateSingleAnd(splittedWithAnd[j])
                        } else if (splittedWithAnd[j].contains("||")) {
                            splittedWithAnd[j] = evaluateSingleOrWithoutAnd(splittedWithAnd[j])
                        } else {
                            splittedWithAnd[j] = evaluateSingleOrWithoutAnd(splittedWithAnd[j])
                        }
                    }
                    if (splittedWithAnd.joinToString { it }.contains("true")) {
                        ids[i] = "true"
                    } else {
                        ids[i] = "false"
                    }

                    Log.e("afterSplittedWithAnd", splittedWithAnd.joinToString { it })
                } else if (ids[i].contains("&&")) {
                    ids[i] = evaluateSingleAnd(ids[i])
                } else if (!ids[i].contains("&&") && !ids[i].contains("||") && !ids[i].contains("AND") && !ids[i].contains(
                        "OR"
                    ) && !ids[i].contains("NOT")
                ) {
                    ids[i] = evaluateSingleOrWithoutAnd(ids[i])
                } else if (!ids[i].contains("&&") && !ids[i].contains("||")) {
                    ids[i] = ids[i]
                }
            }
        }
        return calculateConditionalOperators(ids) == "true"
    }

    private fun calculateConditionalOperators(ids: ArrayList<String>): String {
        var result = "false"

        while (ids.size > 2) {
            Log.e("beforeEleminating", ids.joinToString { "$it" })
            when {
                (ids.contains("NOT") || ids.contains("not")) -> {
                    val index = ids.indexOf("NOT")
                    ids[index + 1] = if (ids[index + 1] == "true") {
                        "false"
                    } else {
                        "true"
                    }
                    ids.removeAt(index)
                }

                (ids.contains("OR NOT") || ids.contains("or not")) -> {

                    val index = ids.indexOf("OR NOT")

                    ids[index + 1] = if (ids[index + 1] == "true") {
                        "false"
                    } else {
                        "true"
                    }

                    ids[index - 1] = if (ids[index - 1] == "true" || ids[index + 1] == "true") {
                        "true"
                    } else {
                        "false"
                    }
                    ids.removeAt(index + 1)
                    ids.removeAt(index)
                }

                (ids.contains("OR") || ids.contains("or")) -> {
                    val index = ids.indexOf("OR")
                    ids[index - 1] = if (ids[index - 1] == "true" || ids[index + 1] == "true") {
                        "true"
                    } else {
                        "false"
                    }
                    ids.removeAt(index + 1)
                    ids.removeAt(index)
                }

                (ids.contains("AND NOT") || ids.contains("and not")) -> {
                    val index = ids.indexOf("AND NOT")
                    ids[index + 1] = if (ids[index + 1] == "true") {
                        "false"
                    } else {
                        "true"
                    }
                    ids[index - 1] = if (ids[index - 1] == "true" && ids[index + 1] == "true") {
                        "true"
                    } else {
                        "false"
                    }

                    ids.removeAt(index + 1)
                    ids.removeAt(index)
                }

                (ids.contains("AND") || ids.contains("and")) -> {
                    val index = ids.indexOf("AND")
                    ids[index - 1] = if (ids[index - 1] == "true" && ids[index + 1] == "true") {
                        "true"
                    } else {
                        "false"
                    }
                    ids.removeAt(index + 1)
                    ids.removeAt(index)
                }
            }

            Log.e("afterEliminating", ids.joinToString { it })
        }
        if (ids.size == 1) {
            result = ids.first()
        }
        return result
    }

    private fun evaluateSingleOrWithoutAnd(splittedWithAnd: String): String {
        if (mActivity.traversedPathMap.containsKey(splittedWithAnd)) {
            return "true"
        } else {
            return "false"
        }
    }

    private fun evaluateSingleAnd(splittedWithAnd: String): String {
        Log.e("splittedWithAnd~", splittedWithAnd)
        val and = splittedWithAnd.split("&&")
        if (mActivity.traversedPathMap.containsKey(and.first()) && mActivity.traversedPathMap[and.first()]?.contains(
                and.last()
            ) == true
        ) {
            return "true"
        } else {
            return "false"
        }
    }

    fun checkSummeryAlreadyContains(optionId: String, callback: (Boolean) -> Unit) {
        val item = mActivity.evaluatedSummeryList.find { it.optionId == optionId }

        if (item != null) {
            callback(true)
            Log.e("Exists", "True")
        } else {
            callback(false)
            Log.e("Exists", "False")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarControl() {
        val homeActivity = (activity as HomeHost).binding.homeToolbarId
        homeActivity.toolbarNameTv.text = getString(R.string.guide)
        homeActivity.llChangeLanguage.setOnClickListener {
            lifecycleScope.launch {
                SharePreferenceHelper.getInstance(requireContext()).saveTabType("Guide")
            }
            (activity as HomeHost).changeLanguageMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarControl()
    }

    private val questionsAdapter: QuestionsMainAdapter by lazy {
        QuestionsMainAdapter(
            mActivity,
            binding,
            object : QuestionsMainAdapter.QuestionsMainClickListener {
                override fun onAnswerSelected(
                    qIndex: Int,
                    aIndex: Int,
                    currentItem: GuideQuestionsModelItem,
                    selectedAnswer: Child
                ) {
                    try {

                        selectedAnswer.text?.let { mActivity.previousSelectedAnswerPathLabel.add(it) }

                        Log.e("size-before", "${mActivity.questionsOrder.size}")
                        if (mActivity.traversedPathMap.containsKey(currentItem.id) && !(mActivity.traversedPathMap.get(
                                currentItem.id
                            )?.contains(selectedAnswer.id ?: "") == true)
                        ) {
                            val multipleAnswers =
                                mActivity.traversedPathMap.get(currentItem.id) + "~" + selectedAnswer.id
                            mActivity.traversedPathMap.put(currentItem.id, multipleAnswers)
                            Log.e("multipleMapData", "${currentItem.id}, $multipleAnswers")
                        } else if (!mActivity.traversedPathMap.containsKey(currentItem.id)) {
                            mActivity.traversedPathMap.put(
                                currentItem.id,
                                selectedAnswer.id
                            )
                            Log.e(
                                "singleMapData",
                                "${currentItem.id}, ${selectedAnswer.id}"
                            )
                        }
                        Log.e("selectedAnswer", "${selectedAnswer.isSelected}")
                        if (selectedAnswer.isSelected) {
                            mActivity.traversedPathMap.put(
                                currentItem.id,
                                mActivity.traversedPathMap.get(currentItem.id)
                                    ?.replace("${selectedAnswer.id}", "")
                            )
                        }

                        val summeryModel = SummeryModel()
                        summeryModel.id = currentItem.id
                        summeryModel.optionId = selectedAnswer.id
                        summeryModel.category = selectedAnswer.summaryCatagory
                        summeryModel.summaryDescription = selectedAnswer.summaryDescription
                        summeryModel.summaryDescription_ES = selectedAnswer.summaryDescription_ES

                        if (selectedAnswer.summaryCatagory != null && selectedAnswer.summaryCatagory != "" && (
                                    (selectedAnswer.summaryDescription != null && selectedAnswer.summaryDescription != "") || (selectedAnswer.summaryDescription_ES != null && selectedAnswer.summaryDescription_ES != ""))
                        ) {
                            checkSummeryAlreadyContains(selectedAnswer.id ?: "") {
                                if (!it) {
                                    mActivity.evaluatedSummeryList.add(summeryModel)

                                    Log.e("SumDesc", "${summeryModel.summaryDescription}")
                                }
                            }
                        }

                        currentNodeHeading = currentItem.data?.label.toString()


                        generateJsonOfTraversedPathMap() { it ->
                            mActivity.tinyDB.putString("traversedPathJson", it)

                            generateJsonOfSummerDescription {
                                mActivity.tinyDB.putString("summeryJson", it)
                            }
                        }


                        getConditionBasedNextQuestion(
                            selectedAnswer
                        ) { it ->
                            questionsAdapter.questionList = ArrayList()
                            questionsAdapter.questionList.addAll(it)

//                                currentNodeHeading = it[qIndex].data?.label.toString()

                            lifecycleScope.launch {
                                delay(100)
                                questionsAdapter.notifyDataSetChanged()
                            }

                            mActivity.lastQuestion = selectedAnswer.targetNode
                            mActivity.tinyDB.putString("lastQuestion", selectedAnswer.targetNode)

                            mActivity.questionsOrder.add(selectedAnswer.targetNode ?: "")
                            Log.e("size-after", "${mActivity.questionsOrder.size}")

                            mActivity.tinyDB.putListString(
                                "questionOrder",
                                mActivity.questionsOrder
                            )

                            showHideBackArrow()
                            currentItem.data?.child?.forEach { child ->
                                child.isSelected = false
                            }

                            val id = currentItem.id.toString()
                            logQuestionAndId(id, currentNodeHeading)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onActionSelected(
                    qIndex: Int,
                    aIndex: Int,
                    currentItem: GuideQuestionsModelItem,
                    actionLick: ActionLinkModel
                ) {
                    if (actionLick.link.contains("package")) {
                        Log.e("PackageId", actionLick.link)
                        Log.e("PackageId", actionLick.link.split("package").last())
                        val packageId = actionLick.link.split("package/").last()
                        getPlanDetails(packageId)

                    } else if (actionLick.link.contains("schedule")) {
                        Log.e("ConsultationId", actionLick.link)
                        Log.e("ConsultationId", actionLick.link.split("schedule").last())
                        (requireActivity() as HomeHost).consultationTab()
                        (requireActivity() as HomeHost).navigateToConsultation()
                    } else if (actionLick.link.contains("services")) {
                        if (actionLick.id != null) {
                            getPlanDetails(actionLick.id)
                        }else{
                            (requireActivity() as HomeHost).servicesTab()
                            (requireActivity() as HomeHost).navigateToServices()
                        }
                    } else {
                        try {
                            val uri = Uri.parse(actionLick.link)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        } catch (e: Exception) {
                            CustomToasts.showToast(requireActivity(), "Url not found!", false)
                        }
                    }
                }
            })
    }

    private fun getPlanDetails(planId: String) {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getPlanDetails(planId)
        } else {
            CustomToasts.showToast(
                requireActivity(),
                Constants.INTERNET_CONNECTION_ERROR_MESSAGE,
                false
            )
        }
    }
}

