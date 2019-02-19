package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.MainViewModel
import alex.orobinsk.vortex.ui.widgets.ToolbarModelBuilder
import alex.orobinsk.vortex.util.toast
import android.view.View

class MainActivity: BaseActivity() {
    override fun init() {
        binder.bind<MainActivity, MainViewModel>(R.layout.activity_main, this) {
            it.apply {

            }
        }
    }

    private fun toggleMenu() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}
