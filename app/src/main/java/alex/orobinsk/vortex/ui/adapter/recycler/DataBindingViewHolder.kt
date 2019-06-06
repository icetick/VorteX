package alex.orobinsk.vortex.ui.adapter.recycler

import alex.orobinsk.vortex.ui.widgets.ActionListener
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


class DataBindingViewHolder<T : ViewDataBinding, R>(private val binding: T) : RecyclerView.ViewHolder(binding.root) {
    fun bind(field: Int, item: R) {
        binding.setVariable(field, item)
        binding.executePendingBindings()
    }

    fun bindActionListener(field: Int, actionListener: ActionListener<R>) {
        binding.setVariable(field, actionListener)
        binding.executePendingBindings()
    }

    fun clearAnimation() {
        binding.root.clearAnimation()
    }
}