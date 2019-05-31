package alex.orobinsk.vortex.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment(), BaseView {
    val binder by Binder()
    override var inflater: LayoutInflater? = null
    override val container: ViewGroup?
        get() = null

    abstract fun init()
    abstract fun onReleaseResources()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.inflater = inflater
        binder bind this
        init()
        return binder.binding?.root
    }

    companion object {
        inline fun<reified T: BaseFragment> newInstance(items: Bundle?): T {
            val fragment = T::class.java.newInstance()
            fragment.arguments = items
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onReleaseResources()
    }

    override fun onStop() {
        super.onStop()
        onReleaseResources()
    }
}
