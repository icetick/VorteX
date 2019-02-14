package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.viewModel.RadioViewModel
import android.widget.Toast
import androidx.lifecycle.Observer

class RadioFragment: BaseFragment() {
    override fun init() {
        binder.bind<RadioFragment, RadioViewModel>(R.layout.fragment_radio) {viewModel ->
            viewModel.apply {
                radioResponse.observe(this@RadioFragment, Observer { radioResponse ->
                    val endpointRadioData = radioResponse.data.joinToString { data -> data.picture+data.id+data.title }
                    Toast.makeText(this@RadioFragment.context, endpointRadioData, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}
