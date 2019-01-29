package alex.orobinsk.vortex.util.animation

import android.view.animation.Interpolator

class BounceInterpolator(amplitude: Double = 0.2, frequency: Double = 20.toDouble()) : Interpolator {
    private var mAmplitude = amplitude
    private var mFrequency = frequency

    override fun getInterpolation(time: Float): Float {
        return (-1.0 * Math.pow(Math.E, -time / mAmplitude) *
                Math.cos(mFrequency * time) + 1).toFloat()
    }
}