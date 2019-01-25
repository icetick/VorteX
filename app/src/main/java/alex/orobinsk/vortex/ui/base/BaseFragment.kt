package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.util.Binder
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
        binder with this
        init()
        return binder.binding?.root
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
