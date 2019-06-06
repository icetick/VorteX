package alex.orobinsk.vortex.util.animation

import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.accelDecelerateInterpolator
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.accelerateInterpolator
import android.view.View
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.bounce
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.bounceInterpolator
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.linearInterpolator
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.scaleTranslateUp
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.translateUp
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.vortexAnimation
import alex.orobinsk.vortex.util.findViewsByType
import alex.orobinsk.vortex.util.hideKeyboard
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.logging.Handler

@BindingAdapter("bounceAnimate")
fun setBounceAnimation(view: ImageView, startState: MutableLiveData<Boolean>) {
    startState.observeForever {
        if (!it) {
            view.visibility = View.INVISIBLE

            view.postDelayed({
                view.chainAnimation {
                    bounce() and scaleTranslateUp() before { startState.value = true } interpolator bounceInterpolator()
                }
            }, 200)
        }
    }
}

@BindingAdapter("overlayReveal")
fun setOverlayreveal(view: View, startState: MutableLiveData<Boolean>) {
    view.setOnTouchListener { v, event -> v.hideKeyboard(); false }
    view.visibility = View.INVISIBLE
    startState.observeForever {
        if (it) {
            view.chainAnimation {
                translateUp() interpolator accelDecelerateInterpolator()
            }
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("animatedVortex")
fun setVortexAnimation(view: View, startState: MutableLiveData<Boolean>) {
    view.visibility = View.INVISIBLE
    startState.observeForever {
        if (it) {
            view.chainAnimation {
                vortexAnimation() interpolator accelerateInterpolator()
            }
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("animateChilds")
fun setAnimateInsiders(view: ViewGroup, startState: MutableLiveData<Boolean>) {
    val animatedFields = view.findViewsByType<TextInputLayout>()
    val animatedButtons = view.findViewsByType<Button>()

    animatedFields.forEach { it.visibility = View.INVISIBLE }
    animatedButtons.forEach { it.visibility = View.INVISIBLE }

    startState.observeForever {
        if (it) {
            animatedFields.forEach { item ->
                item.chainAnimation {
                    translateUp() interpolator accelerateInterpolator() then {
                        item.visibility = View.VISIBLE
                    }
                }
            }

            animatedButtons.forEach { item ->
                item.chainAnimation {
                    translateUp() interpolator accelerateInterpolator() then {
                        item.visibility = View.VISIBLE
                    }
                }
            }
            /*scaleTranslateUp().invoke()?.animateViewChain(*animatedViews, *animatedButtons) then {
            view.visibility = View.VISIBLE
        }*/
        }
    }
}

@BindingAdapter("fieldReveal")
fun setFieldReveal(view: View, startState: MutableLiveData<Boolean>) {
    view.visibility = View.INVISIBLE
    startState.observeForever {
        if (it) {
            view.chainAnimation {
                translateUp() interpolator linearInterpolator()
            }
            view.visibility = View.VISIBLE
        }
    }
}