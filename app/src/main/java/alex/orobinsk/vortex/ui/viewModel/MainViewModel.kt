package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.ui.adapter.viewpager.MainScreenAdapter
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.base.FragmentFactory
import alex.orobinsk.vortex.ui.view.RadioFragment

class MainViewModel: BaseViewModel() {
    var pagerAdapter: MainScreenAdapter? = null

    override fun onCreated() {
        pagerAdapter = MainScreenAdapter(bindedActivity!!.supportFragmentManager)
        pagerAdapter?.add(FragmentFactory.create<RadioFragment>())
        pagerAdapter?.notifyDataSetChanged()
    }
}