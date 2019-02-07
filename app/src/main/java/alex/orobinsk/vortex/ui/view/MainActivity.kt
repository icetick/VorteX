package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.MainViewModel
import android.widget.Toast
import androidx.lifecycle.Observer

class MainActivity: BaseActivity() {
    override fun init() {
        binder.bind<MainActivity, MainViewModel>(R.layout.activity_main, this) { viewModel ->
            viewModel.apply {
                radioResponse.observe(this@MainActivity, Observer { radioResponse ->
                    val endpointRadioData = radioResponse.data.joinToString { data -> data.picture+data.id+data.title }
                    Toast.makeText(this@MainActivity, endpointRadioData, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    override fun onReleaseResources() {

    }
}
