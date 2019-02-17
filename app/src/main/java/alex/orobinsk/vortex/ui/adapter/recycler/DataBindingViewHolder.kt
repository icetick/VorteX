package alex.orobinsk.vortex.ui.adapter.recycler

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class DataBindingViewHolder<T: ViewDataBinding, R>(private val binding: T): RecyclerView.ViewHolder(binding.root) {
    fun bind(field: Int, item: R) {
        binding.setVariable(field, item)
        binding.executePendingBindings()
    }
}