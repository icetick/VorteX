package alex.orobinsk.vortex.ui.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class MainScreenAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments = ArrayList<Fragment>()
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getCurrentFragment(viewPager: ViewPager2): Fragment {
        return fragments[viewPager.currentItem]
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