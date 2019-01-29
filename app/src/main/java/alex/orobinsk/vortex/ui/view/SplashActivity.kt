package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.SplashViewModel
import alex.orobinsk.vortex.util.Binder
import alex.orobinsk.vortex.util.startActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {
    val binder by Binder()

    override fun init() {
        binder with this

        binder.bind<SplashActivity, SplashViewModel>(layoutId = R.layout.activity_splash) {
            it.apply {
                endSplash.observeForever { ended ->
                    if(ended) {
                       startActivity<LoginActivity>(null, logo_iv)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}
