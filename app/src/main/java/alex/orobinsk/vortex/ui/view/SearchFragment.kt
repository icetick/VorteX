package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.databinding.SearchFragmentBinding
import alex.orobinsk.vortex.ui.base.BaseFragment
import alex.orobinsk.vortex.ui.viewModel.SearchViewModel

class SearchFragment: BaseFragment<SearchFragmentBinding, SearchViewModel>() {
    override val viewModel: SearchViewModel by viewModel()
    override fun getLayoutId(): Int = R.layout.search_fragment

    override fun init() {

    }

    override fun onReleaseResources() {
    }
}