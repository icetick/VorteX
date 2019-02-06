package alex.orobinsk.vortex.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment

fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Fragment.toast(message: CharSequence) = Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()

fun Activity.hideKeyboard()  {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    window.currentFocus?.let {
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

@SuppressLint("HardwareIds")
fun Context.getImei(): String? {
    val telephonyMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return if(checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyMgr.imei
        } else {
            telephonyMgr.deviceId
        }
    } else {
        null
    }
}

suspend fun delay(time: Long, action: ()->Unit){
    kotlinx.coroutines.delay(time)
    action()
}

inline fun<reified T: View> ViewGroup.findViewsByType(): Array<T> {
     var childsByType = ArrayList<T>()
     for(index in 0..childCount) {
         val currentItem = getChildAt(index)
         if(currentItem is T) {
             childsByType.add(currentItem)
         }
     }
     return childsByType.toTypedArray()
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