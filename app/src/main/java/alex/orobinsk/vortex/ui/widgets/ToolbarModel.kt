package alex.orobinsk.vortex.ui.widgets

import alex.orobinsk.annotation.ModelBuilder
import android.graphics.drawable.Drawable
import android.view.View

@ModelBuilder
data class ToolbarModel(
    val leftIcon: Drawable?,
    val rightIcon: Drawable?,
    val toolbarTitle: String?,
    val listenerLeft: View.OnClickListener?,
    val listenerRight: View.OnClickListener?
)