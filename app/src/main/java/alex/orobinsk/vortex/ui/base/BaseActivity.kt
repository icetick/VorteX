package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.util.Binder
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), BaseView {
    override var inflater: LayoutInflater? = null
    override val container: ViewGroup?
        get() = null

    abstract fun init()
    abstract fun onReleaseResources()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        inflater = layoutInflater
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = TransitionInflater.from(this@BaseActivity).inflateTransition(android.R.transition.slide_right)
            exitTransition = TransitionInflater.from(this@BaseActivity).inflateTransition(android.R.transition.slide_left)
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