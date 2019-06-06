package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.adapter.recycler.BindingRecyclerAdapter
import alex.orobinsk.vortex.ui.widgets.ActionListener
import alex.orobinsk.vortex.ui.widgets.ParallaxTransformer
import alex.orobinsk.vortex.ui.widgets.ResideLayout
import alex.orobinsk.vortex.ui.widgets.VortexProgress
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.bounce
import alex.orobinsk.vortex.util.animation.AnimationSets.Companion.bounceInterpolator
import alex.orobinsk.vortex.util.animation.chainAnimation
import alex.orobinsk.vortex.util.animation.interpolator
import alex.orobinsk.vortex.util.animation.setOnClickListenerWithScale
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Interpolator
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

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

@BindingAdapter("listItems", "callbackHandler", "listLayout")
fun <T> setListItems(
    recyclerView: RecyclerView,
    items: MutableLiveData<List<T>>,
    callbackHandler: ActionListener<T>,
    layoutItem: Int
) {
    items.observeForever {
        it?.let { list ->
            recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 2)
            recyclerView.adapter = BindingRecyclerAdapter<ViewDataBinding, T>(layoutItem, callbackHandler, list)
        }
    }
}

@BindingAdapter("scaleTapListener")
fun <T> scaleTapListener(view: CardView, block: () -> Unit) {
    view.setOnClickListenerWithScale {
        block.invoke()
    }
}

@BindingAdapter("resideAdapter"/*, "navigator"*/)
fun setResideMenu(
    view: ListView,
    resideMenuAdapter: ArrayAdapter<String>?/*, navigator: ActivityNavigator*/
) {
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
            if (s.toString() != textField.value) {
                view.error = "Password is not correct"
            }
        }
    })
}

@BindingAdapter("updateProgress")
fun updateProgress(view: ProgressBar, progressFlag: MutableLiveData<Int>) {
    progressFlag.observeForever { field ->
        if (field != 0 || field == 100) {
            view.visibility = View.VISIBLE
            view.progress = field
        } else {
            view.visibility = View.GONE
        }
    }
}


@BindingAdapter("progressField")
fun progressField(view: VortexProgress, progressFlag: MutableLiveData<Boolean>) {
    progressFlag.observeForever { field -> if (field) view.showProgressBar() else view.hideProgressBar() }
}

@BindingAdapter("disallowTouchEvent")
fun disallowTouchEvent(view: RecyclerView, isDisallowed: Boolean) {
    if (isDisallowed) {
        view.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.parent.requestDisallowInterceptTouchEvent(isDisallowed); return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }
}

@BindingAdapter("android:src")
fun setImageSrc(view: ImageView, drawable: Drawable?) {
    Glide.with(view).load(drawable)
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                scheduleStartPostponedTransition(view)
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(view)
}

@BindingAdapter("android:srcBlur")
fun setImageSrcBlurUrl(view: ImageView, url: String?) {
    val roundedBlurryTransform = MultiTransformation<Bitmap>(
        BlurTransformation(20, 1),
        RoundedCornersTransformation(70, 0, RoundedCornersTransformation.CornerType.ALL)
    )

    Glide.with(view).load(url)
        .apply(RequestOptions.bitmapTransform(roundedBlurryTransform).error(R.drawable.vortex_progress)).into(view)
}

@BindingAdapter("android:srcUrl")
fun setImageSrcUrl(view: ImageView, url: String?) {
    Glide.with(view).load(url).apply(RequestOptions().error(R.drawable.vortex_progress))
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                //scheduleStartPostponedTransition(view)
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
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