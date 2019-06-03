package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.databinding.FragmentRadioBinding
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.viewModel.RadioViewModel
import alex.orobinsk.vortex.util.toast
import androidx.lifecycle.Observer
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class RadioFragment: BaseFragment<FragmentRadioBinding, RadioViewModel>(), KodeinAware {
    override val kodein: Kodein = App.singletonKodein
    override val viewModel: RadioViewModel by instance()
    override fun getLayoutId(): Int = R.layout.fragment_radio

    override fun init() {
        viewModel.apply {
            postActivityTracks.observe(this@RadioFragment, Observer {
                (activity as MainActivity).playAudio(currentTracklist.value!!)
            })
            message.observe(this@RadioFragment, Observer {
                toast(it)
            })
        }
    }

    override fun onReleaseResources() {

    }
}
