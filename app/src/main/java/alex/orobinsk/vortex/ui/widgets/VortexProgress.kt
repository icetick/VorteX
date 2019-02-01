package alex.orobinsk.vortex.ui.widgets

import alex.orobinsk.vortex.util.animation.Animations.Companion.bounce
import alex.orobinsk.vortex.util.animation.Animations.Companion.bounceInterpolator
import alex.orobinsk.vortex.util.animation.Animations.Companion.linearInterpolator
import alex.orobinsk.vortex.util.animation.Animations.Companion.scaleDown
import alex.orobinsk.vortex.util.animation.Animations.Companion.vortexAnimation
import alex.orobinsk.vortex.util.animation.and
import alex.orobinsk.vortex.util.animation.chainAnimation
import alex.orobinsk.vortex.util.animation.interpolator
import alex.orobinsk.vortex.util.animation.then
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

class VortexProgress: ProgressBar {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    fun showProgressBar() {
        visibility = View.VISIBLE
        chainAnimation {
            vortexAnimation() interpolator linearInterpolator()
        }
    }

    fun hideProgressBar() {
        this.clearAnimation()
        chainAnimation {
            scaleDown() interpolator bounceInterpolator() then {
                this.clearFocus()
                visibility = View.GONE
            }
        }
    }
}