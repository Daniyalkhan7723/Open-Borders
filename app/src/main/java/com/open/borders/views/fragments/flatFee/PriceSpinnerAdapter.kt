package com.open.borders.views.fragments.flatFee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.open.borders.databinding.SpinnerItemLayoutBinding
import com.open.borders.models.responseModel.PlanPrice
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerInterface
import com.skydoves.powerspinner.PowerSpinnerView


class PriceSpinnerAdapter (
    powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<PriceSpinnerAdapter.IconSpinnerViewHolder>(),
    PowerSpinnerInterface<PlanPrice> {

    override var index: Int = powerSpinnerView.selectedIndex
    override val spinnerView: PowerSpinnerView = powerSpinnerView
    override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<PlanPrice>? = null

    private val spinnerItems: MutableList<PlanPrice> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconSpinnerViewHolder {
        val binding =
            SpinnerItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        return IconSpinnerViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener
                notifyItemSelected(position)
            }
        }
    }

    override fun onBindViewHolder(holder: IconSpinnerViewHolder, position: Int) {
        holder.bind(spinnerItems[position], spinnerView)
    }

    override fun setItems(itemList: List<PlanPrice>) {
        this.spinnerItems.clear()
        this.spinnerItems.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun notifyItemSelected(index: Int) {
        //if (index == NO_SELECTED_INDEX) return
        val oldIndex = this.index
        this.index = index
        this.spinnerView.notifyItemSelected(index, this.spinnerItems[index].price.toString())
        this.onSpinnerItemSelectedListener?.onItemSelected(
            oldIndex = oldIndex,
            oldItem = oldIndex.takeIf { it >=0 && it< spinnerItems.size }?.let { spinnerItems[oldIndex] },
            newIndex = index,
            newItem = spinnerItems[index]
        )
    }

    override fun getItemCount() = this.spinnerItems.size

    class IconSpinnerViewHolder(private val binding: SpinnerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlanPrice, spinnerView: PowerSpinnerView) {
            // do something using a custom item.
            binding.title.text = "$${item.price}"
        }
    }
}