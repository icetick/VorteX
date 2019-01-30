package alex.orobinsk.vortex.util.animation

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

class ViewAnimation(var initialview: View, val set: AnimationSet) {
    var view: View? = null
    var animations = AnimationSet(false)
    init {
        this.view = initialview
        this.animations = set
    }

    fun start() {
        view?.startAnimation(set)
    }
}

fun View.chainAnimation(animations: () -> ArrayDeque<Animation?>) {
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
                        chainAnimation { queue }
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationStart(animation: Animation?) {}
                })
                startAnimation(firstAnimation)
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

/*infix fun ArrayDeque<Animation?>.and(animation: () -> Animation?): ArrayDeque<Animation?> {
    add(animation.invoke())
    return this
}*/

class Animations {
    companion object {
        val kodein: Kodein = App.singletonKodein
        val app: App by kodein.instance()

        fun bounce(): () -> Animation? {
            return {
                AnimationUtils.loadAnimation(app, R.anim.bounce)?.apply {
                    interpolator = BounceInterpolator()
                }
            }
        }

        fun scaleTranslateUp(): () -> Animation? {
            return {
                AnimationUtils.loadAnimation(app, R.anim.scale_and_move)?.apply {
                    interpolator = BounceInterpolator()
                }
            }
        }
    }
}
