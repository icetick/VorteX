package alex.orobinsk.vortex.ui.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainScreenAdapter(fragmentManager: FragmentManager): FragmentStateAdapter(fragmentManager) {
    private val fragments = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
       return fragments.size
    }

    fun add(fragment: Fragment) {
        fragments.add(fragment)
        notifyDataSetChanged()
    }

    fun remove(fragment: Fragment) {
        fragments.remove(fragment)
        notifyDataSetChanged()
    }
}