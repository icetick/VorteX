package alex.orobinsk.vortex.service

import alex.orobinsk.annotation.ModelBuilder
import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

@ModelBuilder
data class PlayerNotificationModel(
    val image: String? = null,
    val author: String,
    val title: String,
    val appName: String,
    var pauseResumeToggleIcon: Int
)