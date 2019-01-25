package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.util.animation.BounceInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:src")
fun setImageRes(view: ImageView, resource: Int) {
    view.setImageResource(resource)
}

@BindingAdapter("bounceAnimate")
fun setBounceAnimation(view: ImageView, flag: Boolean) {
    if(flag) {
        AnimationUtils.loadAnimation(view.context, R.anim.bounce)?.let { animation ->
            animation.interpolator = BounceInterpolator()
            animation.repeatMode = Animation.INFINITE
            view.startAnimation(animation)
        }
    }
}