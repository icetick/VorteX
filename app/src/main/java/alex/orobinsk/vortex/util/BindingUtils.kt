package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.ui.widgets.VortexProgress
import alex.orobinsk.vortex.util.animation.BounceInterpolator
import android.app.Activity
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.RandomTransitionGenerator

@BindingAdapter("setTextField")
fun setTextField(view: EditText, textField: MutableLiveData<String>) {
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
@BindingAdapter("setPasswordValidateField")
fun setPasswordValidator(view: EditText, textField: MutableLiveData<String>) {
    view.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            if(s.toString() != textField.value) {
                view.error = "Password is not correct"
            }
        }
    })
}

@BindingAdapter("kenBurnsDuration")
fun setKenBurnsTransitor(view: ImageView, duration: Long) {
   // (view as KenBurnsView).setTransitionGenerator(RandomTransitionGenerator(duration, AccelerateDecelerateSlowInterpolator()))
}

class HesitateInterpolator : Interpolator {
    override fun getInterpolation(t: Float): Float {
        val x = 2.0f * t - 1.0f
        return 0.5f * (x/2)
    }
}
class AccelerateDecelerateSlowInterpolator : Interpolator {
    override fun getInterpolation(t: Float): Float {
        return (Math.cos((t + 1) * Math.PI) / 3.0f).toFloat()
    }
}

@BindingAdapter("progressField")
fun progressField(view: VortexProgress, progressFlag: MutableLiveData<Boolean>) {
    progressFlag.observeForever { field -> if(field) view.showProgressBar() else view.hideProgressBar()  }
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