package com.open.borders.customPopups

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.open.borders.R
import com.open.borders.databinding.EvaluationPopupLayoutBinding
import com.open.borders.databinding.PopupRestartConfirmationBinding
import com.open.borders.databinding.SelectMoneyPopupBinding
import com.open.borders.models.responseModel.PlanPrice
import com.open.borders.views.fragments.flatFee.SelectPriceAdapter
import com.open.borders.views.fragments.guide.guideQuestions.EvaluationAdapter
import com.open.borders.views.fragments.guide.guideQuestions.GuideAnswerAdapter
import com.open.borders.views.fragments.scheduleConsulting.TimeSlotAdaptor
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
class PricePopUp(
    private var priceList: MutableList<PlanPrice>,
    private var listener: PriceInterface
) : DialogFragment(), SelectPriceAdapter.SelectPriceInterface {

    private var price = ""

    private lateinit var binding: SelectMoneyPopupBinding

    private val mAdapter : SelectPriceAdapter by lazy {
        SelectPriceAdapter(requireContext(), this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.popup_background)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.select_money_popup, container, false)
        binding = SelectMoneyPopupBinding.bind(view)
        binding.popUpLayout.setOnClickListener { dismiss() }
        mAdapter.priceList = priceList
        setAdapter()
        return view
    }


    interface PriceInterface {
        fun onPriceClick(index: Int, data: PlanPrice)
    }

    private fun setAdapter() {
        binding.selectMoneyRecycler.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it?.adapter = mAdapter
        }
    }

    override fun onPriceClick(index: Int, data: PlanPrice) {
        listener.onPriceClick(index, data)
        dismiss()
    }

}