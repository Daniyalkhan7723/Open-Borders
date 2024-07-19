package com.open.borders.views.fragments.guide.guideQuestions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.R
import com.open.borders.databinding.EvaluationPopupRecyclerLayoutBinding
import com.open.borders.extensions.getCurrentLanguage
import com.open.borders.extensions.setHtmlText
import com.open.borders.extensions.viewVisible
import com.open.borders.views.activities.baseActivity.BaseActivity
import com.open.borders.views.activities.baseActivity.SummeryModel
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class EvaluationAdapter(private var mActivity: BaseActivity) :
    RecyclerView.Adapter<EvaluationAdapter.EvaluationViewHolder>() {
    var list = ArrayList<SummeryModel>()

    inner class EvaluationViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = EvaluationPopupRecyclerLayoutBinding.bind(itemView)
        fun onBind(position: Int) {
            mActivity.getCategoryName(list[position].category!!) {
                binding.tvTitle.text = it
            }

            when (mActivity.getCurrentLanguage()) {
                "es" -> {
                    list[position].summaryDescription_ES?.let { binding.tvDescription?.setHtmlText(it) }
                }
                else -> {
                    list[position].summaryDescription?.let {
                        binding.tvDescription?.setHtmlText(it)
                    }
                }
            }
        }
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(mActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationViewHolder {
        val view = mInflater.inflate(R.layout.evaluation_popup_recycler_layout, parent, false)
        return EvaluationViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvaluationViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}