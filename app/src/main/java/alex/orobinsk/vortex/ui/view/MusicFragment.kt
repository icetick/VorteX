package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.viewModel.MusicViewModel

class MusicFragment: BaseFragment() {
    override fun init() {
        binder.bind<MusicFragment, MusicViewModel>(R.layout.fragment_music) {
            it.apply {  }
        }
    }

    override fun onReleaseResources() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}