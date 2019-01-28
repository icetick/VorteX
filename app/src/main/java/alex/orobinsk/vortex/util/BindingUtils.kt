package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.util.animation.BounceInterpolator
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData

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