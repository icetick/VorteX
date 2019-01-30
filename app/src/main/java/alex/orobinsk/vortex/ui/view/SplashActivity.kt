package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.SplashViewModel


class SplashActivity : BaseActivity() {
    override fun init() {
        binder.bind<SplashActivity, SplashViewModel>(layoutId = R.layout.activity_splash) {
            it.apply {
                endSplash.observeForever { ended ->
                    if(ended) {
                       //startActivity<LoginActivity>(null, logo_iv, splashView)
                    }
                }
            }
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}
