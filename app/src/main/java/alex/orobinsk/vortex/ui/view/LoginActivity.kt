package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.LoginViewModel
import com.gjiazhe.panoramaimageview.GyroscopeObserver
import com.gjiazhe.panoramaimageview.PanoramaImageView

class LoginActivity: BaseActivity() {
    private val gyroscopeObserver = GyroscopeObserver().apply { setMaxRotateRadian(Math.PI/2) }
    override fun init() {
        binder.bind<LoginActivity, LoginViewModel>(R.layout.activity_login) {
            it
        }
        binder.binding?.root?.findViewById<PanoramaImageView>(R.id.imageView)?.setGyroscopeObserver(gyroscopeObserver)
    }

    override fun onReleaseResources() {
        binder.destroy()
    }

    override fun onResume() {
        super.onResume()
        init()
        gyroscopeObserver.register(this)
    }

    override fun onPause() {
        super.onPause()
        gyroscopeObserver.unregister()
    }
}