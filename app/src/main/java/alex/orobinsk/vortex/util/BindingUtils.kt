package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.widgets.ParallaxTransformer
import alex.orobinsk.vortex.ui.widgets.ResideLayout
import alex.orobinsk.vortex.ui.widgets.VortexProgress
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.bounce
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.bounceInterpolator
import alex.orobinsk.vortex.util.animation.chainAnimation
import alex.orobinsk.vortex.util.animation.interpolator
import android.app.Activity
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewTreeObserver
import android.view.animation.Interpolator
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

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

@BindingAdapter("resideAdapter"/*, "navigator"*/)
fun setResideMenu(view: ListView, resideMenuAdapter: ArrayAdapter<String>?/*, navigator: ActivityNavigator*/) {
    view.adapter = resideMenuAdapter
    view.divider = null
    view.dividerHeight = 0
    view.setOnItemClickListener { parent, view, position, id ->
        /*navigator.navigate(ActivityNavigatorDestinationBuilder)*/
    }
}

@BindingAdapter("resideListener")
fun onRevealUnrevealMenu(view: ResideLayout, resideListener: ResideLayout.PanelSlideListener) {
    view.setPanelSlideListener(resideListener)
}

@BindingAdapter("parallaxViewPagerAdapter")
fun setAdapter(viewPager: ViewPager2, pageradapter: FragmentStateAdapter) {
    viewPager.setPageTransformer(ParallaxTransformer(R.id.background))
    viewPager.adapter = pageradapter
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

class HesitateInterpolator : Interpolator {
    override fun getInterpolation(t: Float): Float {
        val x = 2.0f * t - 1.0f
        return 0.5f * (x/2)
    }
}
class AccelerateDecelerateSlowInterpolator : Interpolator {
    override fun getInterpolation(t: Float): Float {
        return (Math.cos((t/2 * Math.PI) / 3.0f).toFloat())
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
@BindingAdapter("android:srcUrl")
fun setImageSrcUrl(view: ImageView,url: String?) {
    Glide.with(view).load(url).apply(RequestOptions().error(R.drawable.vortex_progress))
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    scheduleStartPostponedTransition(view)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    view.chainAnimation {
                        bounce() interpolator bounceInterpolator()
                    }
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