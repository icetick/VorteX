package alex.orobinsk.vortex.util

import android.util.Log

object Logger {
    fun log(exception: Exception, message: String? = null) {
        Log.e("Fatal", " :.. -:- $exception.localizedMessage - $message")
    }

    fun log(message: String) {
        Log.v("Verbose", ":.. -:- $message")
    }
}