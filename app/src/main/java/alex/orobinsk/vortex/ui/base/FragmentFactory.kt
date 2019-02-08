package alex.orobinsk.vortex.ui.base

import android.os.Bundle
import android.text.PrecomputedText
import androidx.core.os.bundleOf

class FragmentFactory {
    companion object {
        inline fun<reified T: BaseFragment> create(vararg params: Pair<String, Any>): BaseFragment {
            return T::class.java.newInstance().apply { arguments= bundleOf(*params) }
        }
    }
}
