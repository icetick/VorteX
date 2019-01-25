package alex.orobinsk.vortex.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup

interface BaseView {
    var inflater: LayoutInflater?
    val container: ViewGroup?
}