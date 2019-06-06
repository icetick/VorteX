package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.BR
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.direct
import org.kodein.di.generic.instance

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment(), KodeinAware {
    abstract fun init()
    abstract fun onReleaseResources()
    abstract fun getLayoutId(): Int

    abstract val viewModel: V
    var binding: T? = null
    var baseActivity: BaseActivity<*, *>? = null

    override val kodein: Kodein by closestKodein()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performDataBinding()
        init()
    }


    fun performDataBinding() {
        binding?.setVariable(BR.viewModel, viewModel)
        viewModel.onCreated()
        binding?.lifecycleOwner = this
        binding?.executePendingBindings()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            this.baseActivity = context
            context.onFragmentAttached()
        }
    }

    override fun onDetach() {
        baseActivity?.onFragmentDetached(tag)
        baseActivity = null
        super.onDetach()
    }

    inline fun <reified VM : ViewModel, T> T.viewModel(): Lazy<VM> where T : KodeinAware, T : Fragment {
        return lazy { ViewModelProviders.of(this, direct.instance()).get(VM::class.java) }
    }

    override fun onDestroy() {
        super.onDestroy()
        onReleaseResources()
    }

    override fun onStop() {
        super.onStop()
        onReleaseResources()
    }

    interface Callback {
        fun onFragmentAttached()

        fun onFragmentDetached(tag: String?)
    }
}
