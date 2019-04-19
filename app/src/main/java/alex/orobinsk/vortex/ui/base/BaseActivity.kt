package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import android.os.Build



abstract class BaseActivity : AppCompatActivity(), BaseView {
    val binder by Binder()
    override var inflater: LayoutInflater? = null
    override val container: ViewGroup?
        get() = null

    abstract fun init()
    abstract fun onReleaseResources()
    open fun requestPermissions() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
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
        this.overridePendingTransition(0, R.anim.vortex_animation)
    }

    override fun onStop() {
        super.onStop()
        onReleaseResources()
    }
}