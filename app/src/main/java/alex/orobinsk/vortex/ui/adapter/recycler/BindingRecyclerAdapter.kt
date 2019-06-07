package alex.orobinsk.vortex.ui.adapter.recycler

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.BR
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.widgets.ActionListener
import alex.orobinsk.vortex.util.animation.setOnClickListenerWithScale
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.kodein.di.generic.instance
import java.util.*


class BindingRecyclerAdapter<T : ViewDataBinding, S>(
    val layoutId: Int,
    val actionListener: ActionListener<S>? = null,
    items: List<S> = arrayListOf()
) : RecyclerView.Adapter<DataBindingViewHolder<T, S>>() {

    val application: App by App.singletonKodein.instance()
    val animatedViews = LinkedList<Int>()

    var items = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T, S> {
        val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<T>(inflater, layoutId, parent, false)
        return DataBindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T, S>, position: Int) {
        holder.bind(BR.viewModel, items[position])
        actionListener?.let { holder.bindActionListener(BR.actionListener, it) }
    }

    override fun onViewDetachedFromWindow(holder: DataBindingViewHolder<T, S>) {
        /*      holder.clearAnimation()
              animatedViews.remove(holder.itemView.id)*/
    }

    override fun onViewAttachedToWindow(holder: DataBindingViewHolder<T, S>) {
        super.onViewAttachedToWindow(holder)
        setAnimation(holder.itemView)
    }

    private fun setAnimation(viewToAnimate: View) {
        // If the bound view wasn't previously displayed on screen, it's animated
        // if(!animatedViews.contains(viewToAnimate.id)) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.abc_fade_in)
        viewToAnimate.startAnimation(animation)
        //    animatedViews.add(viewToAnimate.id)
        //  }
    }

    override fun getItemCount(): Int = items.size
}