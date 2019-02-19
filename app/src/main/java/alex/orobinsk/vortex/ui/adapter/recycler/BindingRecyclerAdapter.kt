package alex.orobinsk.vortex.ui.adapter.recycler

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.BR
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.widgets.ActionListener
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.kodein.di.generic.instance

class BindingRecyclerAdapter<T: ViewDataBinding, R>(val layoutId: Int,
                                                    val actionListener: ActionListener<R>? = null,
                                                    items: List<R> = arrayListOf())
    : RecyclerView.Adapter<DataBindingViewHolder<T, R>>() {

    val application: App by App.singletonKodein.instance()

    var items = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T, R> {
        val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<T>(inflater, layoutId, parent, false)
        return DataBindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T, R>, position: Int) {
        holder.bind(BR.viewModel, items[position])
        actionListener?.let { holder.bindActionListener(BR.actionListener, it) }
    }

    override fun getItemCount(): Int = items.size
}