package alex.orobinsk.vortex.util.animation

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.util.*

fun View.chainAnimation(currentSize: Int? = null, animations: () -> ArrayDeque<Animation?>) {
    val initialSize = currentSize ?: animations.invoke().size
    animations.invoke().let {queue ->
        if(queue.isEmpty()) {
            return
        } else {
            val firstAnimation = queue.poll()
            if(queue.isEmpty()) {
                startAnimation(firstAnimation)
            } else {
                firstAnimation?.setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        startAnimation(queue.poll())
                        chainAnimation(initialSize) { queue }
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationStart(animation: Animation?) {}
                })
                if(initialSize-queue.size==1) {
                    startAnimation(firstAnimation)
                }
            }
        }
    }
}

infix fun (() -> Animation?).and(animation: ()-> Animation?): ArrayDeque<Animation?> {
    val queue = ArrayDeque<Animation?>()
    if(queue.isEmpty() && queue.peek()!=this) {
        queue.add(this.invoke())
    }
    queue.add(animation.invoke())
    return queue
}

infix fun ArrayDeque<Animation?>.and(animation: () -> Animation?): ArrayDeque<Animation?> {
    apply { animation.invoke() }
    return this
}

infix fun ArrayDeque<Animation?>.then(block: () -> Unit): ArrayDeque<Animation?> {
    this.last?.setAnimationListener(object: Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) { block.invoke() }
    })
    return this
}

infix fun (() -> Animation?).interpolator(interpolator: Interpolator): ArrayDeque<Animation?> = ArrayDeque<Animation?>()
        .apply { add(this@interpolator.invoke().apply { this?.interpolator = interpolator }) }

infix fun ArrayDeque<Animation?>.interpolator(interpolator: Interpolator): ArrayDeque<Animation?> = apply { forEach { it?.interpolator = interpolator } }

fun Animation.animateViewChain(vararg views: View) {
    views.forEach { view ->
        view.startAnimation(this)
    }
}

class Animations {
    companion object {
        val kodein: Kodein = App.singletonKodein
        val app: App by kodein.instance()

        fun bounce(): () -> Animation? = { AnimationUtils.loadAnimation(app, R.anim.bounce) }
        fun scaleTranslateUp(): () -> Animation? = { AnimationUtils.loadAnimation(app, R.anim.scale_and_move) }
        fun translateUp(): () -> Animation? = { AnimationUtils.loadAnimation(app, R.anim.slide_up_fade).apply { interpolator = LinearInterpolator() } }
        fun bounceInterpolator() = BounceInterpolator()
    }
}
