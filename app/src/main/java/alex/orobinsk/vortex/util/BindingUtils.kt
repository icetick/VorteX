package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.util.animation.*
import alex.orobinsk.vortex.util.animation.Animations.Companion.bounce
import alex.orobinsk.vortex.util.animation.Animations.Companion.bounceInterpolator
import alex.orobinsk.vortex.util.animation.Animations.Companion.scaleTranslateUp
import alex.orobinsk.vortex.util.animation.Animations.Companion.translateUp
import android.app.Activity
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

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
                translateUp() interpolator bounceInterpolator()
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
                translateUp() interpolator bounceInterpolator()
            }
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("textWatcher")
fun setText(view: EditText, textField: MutableLiveData<String>) {
    view.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            textField.postValue(s.toString())
        }

        override fun afterTextChanged(s: Editable) {
        }
    })
}

@BindingAdapter("android:src")
fun setImageSrc(view: ImageView, drawable: Drawable?) {
    Glide.with(view).load(drawable)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    scheduleStartPostponedTransition(view)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .into(view)
}


private fun scheduleStartPostponedTransition(imageView: ImageView) {
    imageView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            imageView.viewTreeObserver.removeOnPreDrawListener(this)
            startPostponedEnterTransition(imageView.context as Activity)
            return true
        }
    })
}