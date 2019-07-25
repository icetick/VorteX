package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.databinding.FragmentRadioBinding
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.viewModel.PlayerState
import alex.orobinsk.vortex.ui.viewModel.RadioViewModel
import alex.orobinsk.vortex.util.toast
import android.graphics.drawable.Animatable
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_radio.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

class RadioFragment : BaseFragment<FragmentRadioBinding, RadioViewModel>(),PlayableFragment, KodeinAware {
    override val kodein: Kodein = App.singletonKodein
    override val viewModel: RadioViewModel by viewModel()

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

    override fun playFirstTrack() {
        viewModel.playFirstTrack()
    }

    override fun onReleaseResources() {

    }
}
