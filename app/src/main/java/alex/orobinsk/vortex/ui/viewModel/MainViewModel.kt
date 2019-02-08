package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.ui.adapter.viewpager.MainScreenAdapter
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.base.FragmentFactory
import alex.orobinsk.vortex.ui.view.RadioFragment
import androidx.fragment.app.FragmentStatePagerAdapter

class MainViewModel: BaseViewModel() {
    val pagerAdapter = MainScreenAdapter(bindedActivity?.supportFragmentManager)

    init {
        pagerAdapter.add(FragmentFactory.create<RadioFragment>())
    }
}