package alex.orobinsk.vortex.service

import alex.orobinsk.annotation.ModelBuilder
import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

@ModelBuilder
data class PlayerNotificationModel(val image: Bitmap? = null, val author: String, val title: String, val appName: String, val previousIcon: Drawable, val nextIcon: Drawable, val previousAction: PendingIntent, val nextAction: PendingIntent, val pauseResumeAction: PendingIntent, val pauseResumeToggleIcon: Int, val resumeIcon: Drawable, val likeDrawable: Drawable? = null, val likeListener: PendingIntent?= null)