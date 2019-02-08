package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.MainViewModel

class MainActivity: BaseActivity() {
    override fun init() {
        binder.bind<MainActivity, MainViewModel>(R.layout.activity_main, this) {
            it
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}
