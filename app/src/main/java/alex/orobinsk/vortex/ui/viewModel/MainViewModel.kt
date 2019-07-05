package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.adapter.viewpager.MainScreenAdapter
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.base.FragmentFactory
import alex.orobinsk.vortex.ui.view.RadioFragment
import alex.orobinsk.vortex.ui.widgets.ResideLayout
import alex.orobinsk.vortex.ui.widgets.ToolbarModel
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.flaviofaria.kenburnsview.KenBurnsView
import org.kodein.di.generic.instance

class MainViewModel : BaseViewModel() {
    var pagerAdapter: MainScreenAdapter? = null
    var resideAdapter: ArrayAdapter<String>? = null
    val mediaViewModel: MediaViewModel = MediaViewModel()
    val playClicked: MutableLiveData<Boolean> = MutableLiveData()

    val onPlayClick = View.OnClickListener { view ->
        view?.let {
            playClicked.postValue(true)
            mediaViewModel.toggleMediaState()
        }
    }

    var resideListener = object : ResideLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {}
        override fun onPanelOpened(panel: View?) {
            // pagerAdapter?.pauseCurrentFragment()
            panel?.findViewById<KenBurnsView>(R.id.splashView)?.resume()
        }

        override fun onPanelClosed(panel: View?) {
            //pagerAdapter?.resumeCurrentFragment()
            panel?.findViewById<KenBurnsView>(R.id.splashView)?.pause()
        }
    }

    override fun onCreated() {
        mediaViewModel.onCreated()
    }
}