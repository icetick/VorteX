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
import com.flaviofaria.kenburnsview.KenBurnsView


class MainViewModel: BaseViewModel() {
    var pagerAdapter: MainScreenAdapter? = null
    var resideAdapter: ArrayAdapter<String>? = null
    var resideListener = object: ResideLayout.PanelSlideListener {
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
        pagerAdapter = MainScreenAdapter(bindedActivity!!.supportFragmentManager)
        resideAdapter = ArrayAdapter(bindedActivity!!.applicationContext, R.layout.item_reside_menu, arrayOf("Main", "Settings", "Exit"))
        pagerAdapter?.add(FragmentFactory.create<RadioFragment>())
        pagerAdapter?.notifyDataSetChanged()
    }
}