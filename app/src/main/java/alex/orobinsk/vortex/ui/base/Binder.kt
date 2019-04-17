package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.BR
import alex.orobinsk.vortex.util.ViewModelFactory
import android.app.Activity
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

class Binder {
    var binding: ViewDataBinding? = null
    var viewModel: BaseViewModel? = null
    var baseView: BaseView? = null

    /***
     * This method allows you to bind architecture components with
     * each other
     * @sample bind<Activity, ViewModel>(R.layout.activity) { it.apply { demo.observeForever{} } }
     * @param layoutId specifies layout binded with activity or fragment
     * @param callback specifies callback, that applies each observer to field of viewModel
     */
    inline fun <reified F, reified T: BaseViewModel> bind(layoutId: Int, callback: (viewModel: T) -> BaseViewModel): Binder where F: LifecycleOwner {
        when(F::class.isSubclassOf(Activity::class)) {
            true -> {
                binding = DataBindingUtil.setContentView(baseView as BaseActivity, layoutId)
            }
            false -> {
                baseView?.inflater?.let {
                    binding = DataBindingUtil.inflate(it, layoutId, baseView?.container, false)
                }
            }
        }
        viewModel = ViewModelFactory().create(T::class.java)
        viewModel = callback.invoke(viewModel as T)
        viewModel?.onCreated()
        binding?.setVariable(BR.viewModel, viewModel as T)
        return this
    }

    inline fun withVariables(applyClosure: (binding: ViewDataBinding) -> Unit) {
        binding?.let(applyClosure)
        binding?.executePendingBindings()
    }

    operator fun getValue(baseView: BaseView, property: KProperty<*>): Binder {
        return this
    }

    operator fun setValue(baseView: BaseView, property: KProperty<*>, binder: Binder) {
        this.baseView = baseView
    }

    infix fun with(view: BaseView) {
        this.baseView = view
    }

    fun destroy() {
        this.baseView = null
        this.binding = null
        this.viewModel = null
    }
}
