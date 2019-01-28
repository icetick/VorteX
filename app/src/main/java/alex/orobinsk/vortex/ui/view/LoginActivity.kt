package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.LoginViewModel

class LoginActivity: BaseActivity() {
    override fun init() {
        binder.bind<LoginActivity, LoginViewModel>(R.layout.activity_login) {
            it
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}