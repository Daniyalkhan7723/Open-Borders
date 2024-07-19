package com.open.borders.views.fragments.guide.guideQuestions

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.matrimony.sme.utils.CustomToasts
import com.open.borders.R
import com.open.borders.databinding.FragmentGuideQuestionerBinding
import com.open.borders.databinding.QuestionsRecyclerLayoutBinding
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.models.generalModel.guideQuestionsModels.ActionLinkModel
import com.open.borders.models.generalModel.guideQuestionsModels.Child
import com.open.borders.models.generalModel.guideQuestionsModels.Condition
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.utils.OnSwipeTouchListener
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class QuestionsMainAdapter(
    private var mActivity: BaseActivity,
    private var binding: FragmentGuideQuestionerBinding,
    private var listener: QuestionsMainClickListener
) : RecyclerView.Adapter<QuestionsMainAdapter.QuestionMainViewHolder>() {

    var questionList = mutableListOf<GuideQuestionsModelItem>()

    inner class QuestionMainViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = QuestionsRecyclerLayoutBinding.bind(itemView)
        var questionMainRecycler = binding.mainRecycler
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(mActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionMainViewHolder {
        val view = mInflater.inflate(R.layout.questions_recycler_layout, parent, false)
        return QuestionMainViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: QuestionMainViewHolder, position: Int) {

        val adapter = QuestionsAdapter(mActivity, object : QuestionsClickListener {
            override fun onAnswerSelected(
                qIndex: Int,
                aIndex: Int,
                currentItem: GuideQuestionsModelItem,
                selectedAnswer: Child
            ) {
                listener.onAnswerSelected(qIndex, aIndex, currentItem, selectedAnswer)
            }

            override fun onActionSelected(
                qIndex: Int,
                aIndex: Int,
                currentItem: GuideQuestionsModelItem,
                selectedActionLick: ActionLinkModel
            ) {
                listener.onActionSelected(qIndex, aIndex, currentItem, selectedActionLick)
            }

//            override fun onItemClick() {
//                listener.onItemClick()
//            }
        })
        adapter.questionList = ArrayList()
        adapter.questionList = questionList
        holder.questionMainRecycler.adapter = adapter

        questionList.forEachIndexed { index, guideQuestionsModelItem ->
            Log.e("oldSummery", "${mActivity.evaluatedSummeryList.size}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mActivity.evaluatedSummeryList.removeIf { it.id == guideQuestionsModelItem.id }
            }
            Log.e("newSummery", "${mActivity.evaluatedSummeryList.size}")
        }

        if (mActivity.evaluatedSummeryList.size > 0) {
            binding.evaluationLayoutId.viewVisible()
            binding.tvEvaluationCount.text =
                mActivity.evaluatedSummeryList.size.toString()
        } else {
//            binding.evaluationLayoutId.viewGone()
            binding.tvEvaluationCount.text = "0"
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    interface QuestionsMainClickListener {
        fun onAnswerSelected(
            qIndex: Int,
            aIndex: Int,
            currentItem: GuideQuestionsModelItem,
            selectedAnswer: Child
        )

        fun onActionSelected(
            qIndex: Int,
            aIndex: Int,
            currentItem: GuideQuestionsModelItem,
            actionLick: ActionLinkModel
        )
    }
}