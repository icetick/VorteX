package alex.orobinsk.vortex.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), BaseView {
    val binder by Binder()
    override var inflater: LayoutInflater? = null
    override val container: ViewGroup?
        get() = null

    abstract fun init()
    abstract fun onReleaseResources()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /* with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = TransitionInflater.from(this@BaseActivity).inflateTransition(android.R.transition.fade)
            exitTransition = TransitionInflater.from(this@BaseActivity).inflateTransition(android.R.transition.fade)
        }*/
        postponeEnterTransition()
        binder with this
        inflater = layoutInflater
        init()
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