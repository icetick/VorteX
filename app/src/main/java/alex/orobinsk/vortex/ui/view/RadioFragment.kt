package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.viewModel.RadioViewModel
import alex.orobinsk.vortex.ui.widgets.ToolbarModelBuilder
import alex.orobinsk.vortex.util.toast
import android.view.View
import androidx.lifecycle.Observer
import java.util.*

class RadioFragment: BaseFragment() {
    var currentRadioData = listOf<TracksResponse.Data>()
    override fun init() {
        binder.bind<RadioFragment, RadioViewModel>(R.layout.fragment_radio) {viewModel ->
            viewModel.apply {
               /* toolbarModel = ToolbarModelBuilder()
                        .leftIcon(resources.getDrawable(R.drawable.ic_menu, activity?.theme))
                        .listenerLeft(View.OnClickListener {
                        })
                        .rightIcon(resources.getDrawable(R.drawable.ic_search, activity?.theme))
                        .toolbarTitle("Radio")
                        .listenerRight(View.OnClickListener {
                            toast("Search clicked")
                        })
                        .build()*/
                radioResponse.observe(this@RadioFragment, Observer { radioResponse ->
                    /*val endpointRadioData = radioResponse.data.joinToString { data -> data.picture+data.id+data.title }
                    toast(endpointRadioData)*/
                })
                postActivityTracks.observe(this@RadioFragment, Observer {
                    (activity as MainActivity).let {
                        it.playAudio(currentTracklist.value!!)
                    }
                })
            }
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
    }
}
