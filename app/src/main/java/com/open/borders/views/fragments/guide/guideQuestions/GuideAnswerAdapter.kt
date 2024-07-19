package com.open.borders.views.fragments.guide.guideQuestions

import android.annotation.SuppressLint
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.AnswersLayoutBinding
import com.open.borders.databinding.PreviousNewsLayoutBinding
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.models.generalModel.guideQuestionsModels.Child
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.coroutines.*
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class GuideAnswerAdapter(
    private var mActivity: BaseActivity,
    var question: GuideQuestionsModelItem,
    private var listener: OnClick

) : RecyclerView.Adapter<GuideAnswerAdapter.GuideAnswerViewHolder>() {

    inner class GuideAnswerViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = AnswersLayoutBinding.bind(itemView)
        var answer = binding.answerTv
        var answerLayout = binding.answerLayout
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(mActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideAnswerViewHolder {
        val view = mInflater.inflate(R.layout.answers_layout, parent, false)
        return GuideAnswerViewHolder(view)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    override fun onBindViewHolder(
        holder: GuideAnswerViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val items = question.data?.child?.get(position)

        var isMultiSelect = false

        question.data?.child?.forEachIndexed { index, child ->
            if (child.targetNode == null) {
                isMultiSelect = true
            }
        }
//        if (question.id == "c9e1837c-effa-47c0-9209-614fa6fea6f4" || question.id == "d385e03c-6829-4f15-9bea-e3c6832176db") {
//            val layoutParams = ViewGroup.LayoutParams.MATCH_PARENT
//            holder.answerLayout.layoutParams.width = layoutParams
//            holder.answerLayout.background =
//                ContextCompat.getDrawable(mActivity, R.drawable.lets_start_bg)
//            holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
//            holder.answer.gravity = Gravity.CENTER
//        } else {
        Log.e("targetNode~", "${question.data?.child?.get(position)?.targetNode}")
        Log.e(" type~", "${question.type}")
        if (question.data?.child?.get(position)?.isSelected == true) {
            holder.answerLayout.background =
                ContextCompat.getDrawable(mActivity, R.drawable.select_answer_bg)
            holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
        } else if (question.data?.child?.get(position)?.targetNode != null && isMultiSelect) {
            val layoutParams = ViewGroup.LayoutParams.MATCH_PARENT
            holder.answerLayout.layoutParams.width = layoutParams
            holder.answerLayout.background =
                ContextCompat.getDrawable(mActivity, R.drawable.select_answer_bg)
            holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
            holder.answer.gravity = Gravity.CENTER
        } else {
            holder.answerLayout.setBackgroundColor(
                ContextCompat.getColor(
                    mActivity,
                    R.color.app_bg_color
                )
            )
            holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.black))
        }
//            holder.answerLayout.setBackgroundColor(
//                ContextCompat.getColor(
//                    mActivity,
//                    R.color.app_bg_color
//                )
//            )
//            holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.black))
//        }

        when (mActivity.getCurrentLanguage()) {
            "es" -> {
                holder.answer.text = items?.text_ES
            }
            else -> {
                holder.answer.text = items?.text
            }
        }

//        holder.answerLayout.setBackgroundColor(
//            ContextCompat.getColor(
//                mActivity,
//                R.color.app_bg_color
//            )
//        )

        holder.itemView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.answerLayout.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.select_answer_bg)
                    holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                }
                MotionEvent.ACTION_UP -> {
                    if (question.data?.child?.get(position)?.targetNode == null && isMultiSelect) {
                        Log.e("DoNothing", "")
                    } else {
                        holder.answerLayout.background =
                            ContextCompat.getDrawable(mActivity, R.drawable.answer_default_bg)

                        holder.answer.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.black
                            )
                        )
                    }

                    listener.onAnswerClick(position, items ?: Child())
                    if (question.data?.child?.get(position)?.targetNode == null) {
                        question.data?.child?.get(position)?.isSelected =
                            !(question.data?.child?.get(position)?.isSelected)!!
                        notifyItemChanged(position)

                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    holder.answerLayout.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.answer_default_bg)
                    holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.black))

                }
            }
            true
        }

//        holder.itemView.setOnSingleClickListener {
//            listener.onAnswerClick(position, items ?: Child())
//            if (question.data?.child?.get(position)?.targetNode == null) {
//                question.data?.child?.get(position)?.isSelected =
//                    !(question.data?.child?.get(position)?.isSelected)!!
//                notifyItemChanged(position)
//
//            }
//        }
    }

    override fun getItemCount(): Int {
        return question.data?.child?.size ?: 0
    }

    interface OnClick {
        fun onAnswerClick(index: Int, answers: Child)
    }
}