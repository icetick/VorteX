package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.viewModel.RadioViewModel
import androidx.lifecycle.Observer

class RadioFragment: BaseFragment() {
    override fun init() {
        binder.bind<RadioFragment, RadioViewModel>(R.layout.fragment_radio) { viewModel ->
            viewModel.apply {
                postActivityTracks.observe(this@RadioFragment, Observer {
                    (activity as MainActivity).playAudio(currentTracklist.value!!)
                })
            }
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}
