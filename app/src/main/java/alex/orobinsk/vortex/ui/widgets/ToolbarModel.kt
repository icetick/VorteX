package alex.orobinsk.vortex.ui.widgets

import android.graphics.drawable.Drawable
import android.view.View

data class ToolbarModel(val leftIcon: Drawable?,
                        val rightIcon: Drawable?,
                        val toolbarTitle: String?,
                        val listenerLeft: View.OnClickListener?,
                        val listenerRight: View.OnClickListener?)