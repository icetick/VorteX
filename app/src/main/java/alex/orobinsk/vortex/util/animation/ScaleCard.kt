package alex.orobinsk.vortex.util.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.MotionEvent.ACTION_UP
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.View

fun View.setOnClickListenerWithScale(
    minScale: Float = 0.94f,
    clickDuration: Long = 80,
    touchDuration: Long = 300,
    onAnimEnd: (() -> Unit)? = null
) {
    initTouchAnimation(this, touchDuration)
    setOnClickListener {
        if (scaleX == minScale
            && scaleY == minScale
        ) {
            onAnimEnd?.invoke()
            return@setOnClickListener
        }

        val scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", minScale)
        val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", minScale)
        scaleDownX.duration = clickDuration
        scaleDownY.duration = clickDuration

        val scaleDown = AnimatorSet()
        scaleDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationStart(animator: Animator?) {}
            override fun onAnimationEnd(animator: Animator?) {
                onAnimEnd?.invoke()
                post { restoreScale() }
            }
        })

        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.start()
    }
}

private fun initTouchAnimation(v: View, duration: Long) {
    var touchAnimator = ValueAnimator.ofFloat(1f, 0.94f)
    touchAnimator.duration = duration
    touchAnimator.addUpdateListener { animation ->
        run {
            v.scale(animation.animatedValue as Float)
        }
    }
    var isReverseRunning = false

    v.setOnTouchListener { _, event ->
        when (event.action) {
            ACTION_DOWN -> {
                isReverseRunning = false
                touchAnimator.start()
            }
            ACTION_UP -> {
                if (!isReverseRunning) {
                    touchAnimator.reverse()
                    isReverseRunning = true
                }
            }
            ACTION_CANCEL -> {
                if (!isReverseRunning) {
                    touchAnimator.reverse()
                    isReverseRunning = true
                }
            }
        }
        false
    }
}

fun View.restoreScale() {
    scaleX = 1f
    scaleY = 1f
}

fun View.scale(value: Float) {
    scaleX = value
    scaleY = value
}