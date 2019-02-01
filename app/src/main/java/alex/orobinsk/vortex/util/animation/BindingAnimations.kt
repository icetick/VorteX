package alex.orobinsk.vortex.util.animation

import alex.orobinsk.vortex.util.animation.Animations.Companion.accelerateInterpolator
import android.view.View
import alex.orobinsk.vortex.util.animation.Animations.Companion.bounce
import alex.orobinsk.vortex.util.animation.Animations.Companion.bounceInterpolator
import alex.orobinsk.vortex.util.animation.Animations.Companion.linearInterpolator
import alex.orobinsk.vortex.util.animation.Animations.Companion.scaleTranslateUp
import alex.orobinsk.vortex.util.animation.Animations.Companion.translateUp
import alex.orobinsk.vortex.util.animation.Animations.Companion.vortexAnimation
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData

@BindingAdapter("bounceAnimate")
fun setBounceAnimation(view: ImageView, flag: MutableLiveData<Boolean>) {
    flag.value?.let {
        if (!it) {
            view.chainAnimation {
                bounce() and scaleTranslateUp() and scaleTranslateUp() interpolator bounceInterpolator() then { flag.postValue(true) }
            }
        }
    }
}

@BindingAdapter("overlayReveal")
fun setOverlayreveal(view: View, flag: MutableLiveData<Boolean>) {
    view.visibility = View.INVISIBLE
    flag.observeForever {
        if (it) {
            view.chainAnimation {
                translateUp() interpolator linearInterpolator()
            }
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("animatedVortex")
fun setVortexAnimation(view: View, flag: MutableLiveData<Boolean>) {
    view.visibility = View.INVISIBLE
    flag.observeForever {
        if (it) {
            view.chainAnimation {
                vortexAnimation() interpolator accelerateInterpolator()
            }
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("fieldReveal")
fun setFieldReveal(view: View, flag: MutableLiveData<Boolean>) {
    view.visibility = View.INVISIBLE
    flag.observeForever {
        if (it) {
            view.chainAnimation {
                translateUp() interpolator linearInterpolator()
            }
            view.visibility = View.VISIBLE
        }
    }
}