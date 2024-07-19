package com.open.borders.customPopups

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.EvaluationPopupLayoutBinding
import com.open.borders.extensions.*
import com.open.borders.models.responseModel.SendEmailResponse
import com.open.borders.utils.Constants
import com.open.borders.utils.Event
import com.open.borders.utils.MaterialDialogHelper
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.BaseActivity
import com.open.borders.views.activities.baseActivity.SummeryModel
import com.open.borders.views.activities.navigationHost.AuthenticationHost
import com.open.borders.views.activities.navigationHost.HomeHost
import com.open.borders.views.fragments.guide.guideQuestions.EvaluationAdapter
import com.open.borders.views.fragments.scheduleConsulting.DateAndTimeViewModel
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@InternalSerializationApi
class EvaluationPopUp(
    var list: ArrayList<SummeryModel>,
    var mActivity: BaseActivity,
    var liteners: Liteners
) :
    DialogFragment() {
    var ih = StringBuilder(5000);
    var fo = StringBuilder(5000);
    var id = StringBuilder(5000);

    val viewModel: DateAndTimeViewModel by viewModel()

    var immigrationHistory: SummeryModel? = null
    var factorsOptions: SummeryModel? = null
    var inadmissibility: SummeryModel? = null

    var isEnglish = 1
    private val mList = ArrayList<SummeryModel>()
    private lateinit var binding: EvaluationPopupLayoutBinding

    private val evaluationAdapter: EvaluationAdapter by lazy {
        EvaluationAdapter(mActivity)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mActivity.logScreenName("Evaluation Summery PopUp")


//        setAdapter()

        binding.cross?.setOnClickListener {
            dismiss()
        }

        var temp = ArrayList<SummeryModel>()
        temp.addAll(list)
        temp.forEachIndexed { index, summeryModel ->
            when (summeryModel.category) {
                "immigrationHistory" -> {
                    if (immigrationHistory == null) {
                        immigrationHistory = SummeryModel()
                        immigrationHistory?.id = summeryModel.id
                        immigrationHistory?.optionId = summeryModel.optionId
                        immigrationHistory?.category = summeryModel.category
                        immigrationHistory?.summaryDescription = summeryModel.summaryDescription
                        immigrationHistory?.summaryDescription_ES =
                            summeryModel.summaryDescription_ES
                    }

                    when (mActivity.getCurrentLanguage()) {
                        "es" -> {
                            ih.append(summeryModel.summaryDescription_ES + "\n")
                        }
                        else -> {
                            ih.append(summeryModel.summaryDescription + "\n")
                        }

                    }


                }
                "factorsOptions" -> {
                    if (factorsOptions == null) {
                        factorsOptions = SummeryModel()
                        factorsOptions?.id = summeryModel.id
                        factorsOptions?.optionId = summeryModel.optionId
                        factorsOptions?.category = summeryModel.category
                        factorsOptions?.summaryDescription = summeryModel.summaryDescription
                        factorsOptions?.summaryDescription_ES = summeryModel.summaryDescription_ES
                    }

                    when (mActivity.getCurrentLanguage()) {
                        "es" -> {
                            fo.append(summeryModel.summaryDescription_ES + "\n")
                        }
                        else -> {
                            fo.append(summeryModel.summaryDescription + "\n")
                        }

                    }
                }
                else -> {
                    if (inadmissibility == null) {
                        inadmissibility = SummeryModel()
                        inadmissibility?.id = summeryModel.id
                        inadmissibility?.optionId = summeryModel.optionId
                        inadmissibility?.category = summeryModel.category
                        inadmissibility?.summaryDescription = summeryModel.summaryDescription
                        inadmissibility?.summaryDescription_ES = summeryModel.summaryDescription_ES
                    }

                    when (mActivity.getCurrentLanguage()) {
                        "es" -> {
                            id.append(summeryModel.summaryDescription_ES + "\n")
                        }
                        else -> {
                            id.append(summeryModel.summaryDescription + "\n")
                        }

                    }
                }
            }
        }
        Log.e("ihDes", "${ih}")
        Log.e("foDes", "${fo}")
        Log.e("idDes", "${id}")

        when (mActivity.getCurrentLanguage()) {
            "es" -> {
                isEnglish = 0
                immigrationHistory?.summaryDescription_ES = ih.toString()
                factorsOptions?.summaryDescription_ES = fo.toString()
                inadmissibility?.summaryDescription_ES = id.toString()
            }
            else -> {
                isEnglish = 1
                immigrationHistory?.summaryDescription = ih.toString()
                factorsOptions?.summaryDescription = fo.toString()
                inadmissibility?.summaryDescription = id.toString()
            }
        }


        if (immigrationHistory != null){
            when (mActivity.getCurrentLanguage()) {
                "es" -> {
                    immigrationHistory?.summaryDescription_ES?.let { binding.iTvDescription?.setHtmlText(it) }
                }
                else -> {
                    immigrationHistory?.summaryDescription?.let {
                        binding.iTvDescription?.setHtmlText(it)
                    }
                }
            }
        }
//        mList.add(immigrationHistory!!)
        if (factorsOptions != null){
            when (mActivity.getCurrentLanguage()) {
                "es" -> {
                    factorsOptions?.summaryDescription_ES?.let { binding.fTvDescription?.setHtmlText(it) }
                }
                else -> {
                    factorsOptions?.summaryDescription?.let {
                        binding.fTvDescription?.setHtmlText(it)
                    }
                }
            }
        }
//        mList.add(factorsOptions!!)
        if (inadmissibility != null){
            when (mActivity.getCurrentLanguage()) {
                "es" -> {
                    inadmissibility?.summaryDescription_ES?.let { binding.tvDescription?.setHtmlText(it) }
                }
                else -> {
                    inadmissibility?.summaryDescription?.let {
                        binding.tvDescription?.setHtmlText(it)
                    }
                }
            }
        }
//        mList.add(inadmissibility!!)

//        evaluationAdapter.list = mList

//        evaluationAdapter.notifyDataSetChanged()

        binding.tvEmailResult.setOnClickListener {
            val user = SharePreferenceHelper.getInstance(requireContext()).getUser()
            if (user.is_guest == 0) {
                val data: SendEmailResponse =
                    SendEmailResponse(ArrayList(), ArrayList(), ArrayList())

                immigrationHistory?.let { it1 -> data.immigrationHistory.add(it1) }
                factorsOptions?.let { it1 -> data.factorsOptions.add(it1) }
                inadmissibility?.let { it1 -> data.inadmissibilty.add(it1) }

                val email = Gson().toJson(data)
                Log.e("jsonData", email)
                viewModel.sendSummeryEvaluationData(email, isEnglish)
            } else {
                dismiss()
                CustomToasts.showToast(activity as HomeHost, getString(R.string.we_do_not_have_email), false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
//        mActivity.evaluatedSummeryMap.forEach { it ->
//            list.add(it.key ?: "")
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.evaluation_popup_layout, container, false)
        binding = EvaluationPopupLayoutBinding.bind(view)
        binding.evaluationId.setOnClickListener { dismiss() }
        val dialogHelper by inject<MaterialDialogHelper>()
        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)
        attachViewModel()
        return view
    }

    override fun onStop() {
        immigrationHistory = null
        factorsOptions = null
        inadmissibility = null
        mList.clear()
        liteners.destroyed(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setAdapter() {
        binding.evaluationRecyclerId.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = evaluationAdapter
        }
    }

    private fun attachViewModel() {
        viewModel.snackBarMessage.observe(viewLifecycleOwner) {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
//                dialog?.dismiss()
                CustomToasts.failureToastCenterTop(requireContext(), msg)
            }
        }

        viewModel.sendEvaluationSummeryToEmailLiveData.observe(viewLifecycleOwner) {
            if (it.status) {
                dismiss()
                CustomToasts.showToast(requireActivity(), it.message, true)
                liteners.onEmailSendAndDelete()
            }
        }
    }


    fun setupProgressDialog(
        showHideProgress: LiveData<Event<Boolean>>,
        dialogHelper: MaterialDialogHelper
    ) {
        var mDialog: MaterialDialog? = null
        showHideProgress.observe(this) {
            if (!it.consumed) it.consume()?.let { flag ->
                if (flag)
                    mDialog?.show() ?: dialogHelper.showSimpleProgress(requireContext())
                        .also { dialog ->
                            mDialog = dialog
                        }
                else mDialog?.dismiss()
            }
        }
    }

    interface Liteners {
        fun destroyed(frag: DialogFragment)
        fun onEmailSendAndDelete()
    }
}