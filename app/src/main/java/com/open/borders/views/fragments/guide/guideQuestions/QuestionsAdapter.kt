package com.open.borders.views.fragments.guide.guideQuestions

import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Html.TagHandler
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.ItemQuestionnaireBinding
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.extensions.handleUrlClicks
import com.open.borders.extensions.setHtmlText
import com.open.borders.models.generalModel.guideQuestionsModels.ActionLinkModel
import com.open.borders.models.generalModel.guideQuestionsModels.Child
import com.open.borders.models.generalModel.guideQuestionsModels.GuideQuestionsModelItem
import com.open.borders.thirdPartyModules.htmltextview.HtmlResImageGetter
import com.open.borders.thirdPartyModules.htmltextview.HtmlTextView
import com.open.borders.utils.SharePreferenceHelper
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi
import org.xml.sax.XMLReader

@SuppressLint("ClickableViewAccessibility")
@InternalSerializationApi
class QuestionsAdapter(
    private var mActivity: BaseActivity, private var listener: QuestionsClickListener
) : RecyclerView.Adapter<QuestionsAdapter.GuideAnswerViewHolder>() {

    var questionList = mutableListOf<GuideQuestionsModelItem>()


    inner class GuideAnswerViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = ItemQuestionnaireBinding.bind(itemView)

        //        var myWebView: WebView? = binding.mWebView
//        var qImage2 = binding.openBorderLogo2
        var qLabel = binding.qLabel
        var qDescription: HtmlTextView = itemView.findViewById(R.id.mQDescription)

        //        var qDescription = binding.htmlTextView
        var answerRecycler = binding.answerRecyclerId
        var actionLinkRecycler = binding.actionLinkRecylcer
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(mActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideAnswerViewHolder {
        val view = mInflater.inflate(R.layout.item_questionnaire, parent, false)
        return GuideAnswerViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor", "SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: GuideAnswerViewHolder, position: Int) {
        val items = questionList[position]

        holder.qDescription.handleUrlClicks()
        when (mActivity.getCurrentLanguage()) {
            "es" -> {
                holder.qDescription.setHtml(
                    Html.fromHtml(items.data?.description_ES).toString(),
                    HtmlResImageGetter(mActivity), mActivity
                )
            }
            else -> {
                holder.qDescription.setHtml(
                    Html.fromHtml(items.data?.description).toString(),
                    HtmlResImageGetter(mActivity), mActivity
                )

            }
        }

        //Fixme
        try {
            holder.actionLinkRecycler.adapter =
                items.data?.actionLink?.let {
                    Log.e("ActionLinks", "Size ${it.size}")
                    ActionLinkAdapter(mActivity, items, object : ActionLinkAdapter.OnActionLinkClick {
                            override fun onActionLinkClick(
                                index: Int,
                                actionLink: ActionLinkModel
                            ) {
                                listener.onActionSelected(holder.absoluteAdapterPosition, index, items, actionLink)
                            }
                        })
                }
        } catch (e: Exception) {
            Log.e("CrashError", e.toString())
        }


        try {
            var mAdapter: GuideAnswerAdapter? = null
            mAdapter =
                items.data?.child?.let {
                    GuideAnswerAdapter(mActivity, items, object : GuideAnswerAdapter.OnClick {
                        override fun onAnswerClick(index: Int, answers: Child) {
                            Log.e(
                                "---QuestionPosition",
                                "${holder.adapterPosition}, ---AnswerPosition: $index, ---QuestionId: ${items.id}, ---AnswerId: ${answers.id}"
                            )
                            listener.onAnswerSelected(
                                holder.adapterPosition,
                                index,
                                items,
                                answers
                            )
                        }
                    })
                }
            holder.answerRecycler.adapter = mAdapter
            holder.answerRecycler.setHasFixedSize(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CrashError", e.toString())
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

}

interface QuestionsClickListener {
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
        selectedActionLick: ActionLinkModel
    )

//    fun onItemClick()
}