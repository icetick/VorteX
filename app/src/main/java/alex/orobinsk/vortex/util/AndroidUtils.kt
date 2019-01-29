package alex.orobinsk.vortex.util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Fragment.toast(message: CharSequence) = Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()

fun Activity.hideKeyboard()  {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    window.currentFocus?.let {
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

inline fun <reified T: Activity> Activity.startActivity(extra: Bundle? = null, vararg views: View) {
    val intent = Intent(this, T::class.java)
    extra?.let {
        intent.putExtras(extra)
    }
    val pairs = views.map { item -> Pair(item, ViewCompat.getTransitionName(item).toString())  }.toTypedArray()
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs)
    startActivity(intent, options.toBundle())
}