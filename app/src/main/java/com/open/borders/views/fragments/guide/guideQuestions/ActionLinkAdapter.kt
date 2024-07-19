package com.open.borders.views.fragments.guide.guideQuestions

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.ActionLinkLayoutBinding
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.models.generalModel.guideQuestionsModels.ActionLinkModel
import com.open.borders.models.generalModel.guideQuestionsModels.Child
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.utils.setOnSingleClickListener
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi


@InternalSerializationApi
class ActionLinkAdapter(
    private var mActivity: BaseActivity,
    var question: GuideQuestionsModelItem,
    private var listener: OnActionLinkClick

) : RecyclerView.Adapter<ActionLinkAdapter.ActionLinkViewHolder>() {

//    var actionLinkList = mutableListOf<ActionLinkModel>()


    inner class ActionLinkViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = ActionLinkLayoutBinding.bind(itemView)
        var answer = binding.answerTv
        var answerLayout = binding.answerLayout
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(mActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionLinkViewHolder {
        val view = mInflater.inflate(R.layout.action_link_layout, parent, false)
        return ActionLinkViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ActionLinkViewHolder, position: Int) {
        val items = question.data?.actionLink?.get(position)!!

        when (mActivity.getCurrentLanguage()) {
            "es" -> {
                holder.answer.text = items.title_ES
            }
            else -> {
                holder.answer.text = items.title
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
                        ContextCompat.getDrawable(mActivity, R.drawable.action_link_selected_bg)
                    holder.answer.setTextColor(ContextCompat.getColor(mActivity, R.color.brown_new))
                }
                MotionEvent.ACTION_UP -> {
                    holder.answerLayout.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.actionlink_bg)

                    holder.answer.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )
                    listener.onActionLinkClick(position, items)

                }
                MotionEvent.ACTION_CANCEL -> {
                    holder.answerLayout.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.actionlink_bg)

                    holder.answer.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )
                }
            }
            true
        }

    }

    override fun getItemCount(): Int {
        return question.data?.actionLink?.size ?: 0
    }

    interface OnActionLinkClick {
        fun onActionLinkClick(index: Int, actionLink: ActionLinkModel)
    }

}