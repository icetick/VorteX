package alex.orobinsk.vortex.service

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.player.MediaPlayer
import alex.orobinsk.vortex.player.PlayerListener
import alex.orobinsk.vortex.ui.viewModel.MediaViewModel
import alex.orobinsk.vortex.ui.viewModel.PlayerState
import alex.orobinsk.vortex.util.MediaList
import alex.orobinsk.vortex.util.NotificationPlayer
import alex.orobinsk.vortex.util.MediaModelUtils
import alex.orobinsk.vortex.util.firstAvailable
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.*
import androidx.core.app.NotificationCompat
import java.io.IOException
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.NotificationTarget
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class MusicPlayerService : Service(), AudioManager.OnAudioFocusChangeListener, NotificationPlayer, KodeinAware {
    override val kodein: Kodein
        get() = App.singletonKodein

    private val binder: IBinder = LocalBinder()
    private var resumePosition: Int = 0
    private var waitingForStart: Boolean = false
    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null
    private lateinit var notificationManager: NotificationManager
    private val PLAYER_NOTIFICATION_ID: Int = 5532
    private var notification: Notification? = null
    private val PLAYER_NOTIFICATION_CHANNEL_ID: String = "music_channel_007"
    private val mediaPlayer: MediaPlayer by instance()
    var mediaViewModel: MediaViewModel? = null

    var mediaList: MediaList<TracksResponse.Data>? = null
        set(value) {
            field = value
            initMediaPlayer()
        }

    companion object {
        const val DEFAULT_ITEMSET = "defItems"
        const val NEXT_TAG = "alex.orobinsk.vortex.service.MusicPlayerService.NEXT_TAG"
        const val PREVIOUS_TAG = "alex.orobinsk.vortex.service.MusicPlayerService.PREVIOUS_TAG"
        const val RESUME_PAUSE_TOOGLE_TAG = "alex.orobinsk.vortex.service.MusicPlayerService.RESUME_PAUSE_TOOGLE_TAG"
        const val LIKE_TAG = "alex.orobinsk.vortex.service.MusicPlayerService.LIKE_TAG"
    }

    private fun initMediaPlayer() {
        try {
            mediaList?.firstAvailable()?.let {
                mediaPlayer.play(MediaModelUtils.getAllPreviews(mediaList!!), object : PlayerListener {
                    override fun onNextTrack() {
                        mediaList?.let { list ->
                            if (!list.isEmpty()) {
                                list.next()?.let { track ->
                                    showNotification(MediaModelUtils.playerModelOf(track))
                                }
                            }
                        }
                    }

                    override fun onTrackEnded() {}
                })
                showNotification(MediaModelUtils.playerModelOf(it))
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            stopSelf()
        }
        waitingForStart = true
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        PLAYER_NOTIFICATION_CHANNEL_ID,
                        "Music Player Notification Channel",
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
            } catch (ex: RemoteException) {

            }
        }
        if (!requestAudioFocus()) {
            stopSelf()
        }
        intent?.hasExtra(DEFAULT_ITEMSET)?.let {
            if (it) {
                intent.getStringExtra(DEFAULT_ITEMSET)?.let { tracks ->
                    val typeTokenList = object : TypeToken<List<TracksResponse.Data>>() {}.type
                    mediaList = MediaList.of(Gson().fromJson(tracks, typeTokenList))
                }
                mediaList?.isNotEmpty().let {
                    if (notification == null) {
                        mediaList?.current()?.let { track ->
                            showNotification(MediaModelUtils.playerModelOf(track))
                        }
                    }
                }
            } else {
                when (intent.action) {
                    RESUME_PAUSE_TOOGLE_TAG -> {
                        pauseResumeToggle()
                    }
                    NEXT_TAG -> {
                        next()
                    }
                    PREVIOUS_TAG -> {
                        previous()
                    }
                    LIKE_TAG -> {
                        like()
                    }
                    else -> {

                    }
                }
            }
        } ?: run {
            when (intent?.action) {
                RESUME_PAUSE_TOOGLE_TAG -> {
                    pauseResumeToggle()
                }
                NEXT_TAG -> {
                    next()
                }
                PREVIOUS_TAG -> {
                    previous()
                }
                LIKE_TAG -> {
                    like()
                }
                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaPlayer.releasePlayer()
        removeAudioFocus()
        stopForeground(true)
        stopSelf()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onAudioFocusChange(focusState: Int) {
        when (focusState) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer.let {
                    if (!it.isPlaying()) {
                        it.resume()
                    }
                }
                mediaPlayer.setVolume(1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer.let {
                    if (it.isPlaying()) {
                        it.pause()
                    }
                }
                mediaPlayer.releasePlayer()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                mediaPlayer.let {
                    if (it.isPlaying()) {
                        it.pause()
                    }
                }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                mediaPlayer.let {
                    if (it.isPlaying()) {
                        it.setVolume(0.1f)
                    }
                }
        }
    }

    @Suppress("DEPRECATION")
    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { }
                .build()
            audioManager?.requestAudioFocus(focusRequest)
        } else {
            audioManager?.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        } == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        //Could not gain focus
    }

    private fun removeAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager?.abandonAudioFocusRequest(focusRequest!!)
        } else {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager?.abandonAudioFocus(this)
        }
    }


    fun showNotification(model: PlayerNotificationModel) {
        val builder = NotificationCompat.Builder(this, PLAYER_NOTIFICATION_CHANNEL_ID)
        /* val openIntent = Intent(this, MainActivity::class.java)
         val contentIntent =
             PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE)
         builder.setContentIntent(contentIntent)*/
        builder.setSmallIcon(R.drawable.ic_stars)
        builder.setContentTitle("Vortex").setContentText("is Playing now").setOngoing(true)
        builder.priority = NotificationCompat.PRIORITY_MIN
        builder.setChannelId(PLAYER_NOTIFICATION_CHANNEL_ID)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_stars))
        return updateNotification(model, builder)
    }

    fun listener(remoteViews: RemoteViews, context: Context) {
        val pendingIntentPlayPause = getPendingSelfIntent(context, RESUME_PAUSE_TOOGLE_TAG)
        val pendingIntentNext = getPendingSelfIntent(context, NEXT_TAG)
        val pendingIntentPrevious = getPendingSelfIntent(context, PREVIOUS_TAG)
        val pendingIntentLike = getPendingSelfIntent(context, LIKE_TAG)

        remoteViews.setOnClickPendingIntent(R.id.pause_resume_btn, pendingIntentPlayPause)
        remoteViews.setOnClickPendingIntent(R.id.next_btn, pendingIntentNext)
        remoteViews.setOnClickPendingIntent(R.id.previous_btn, pendingIntentPrevious)
        remoteViews.setOnClickPendingIntent(R.id.like_btn, pendingIntentLike)
    }

    private fun updateNotification(model: PlayerNotificationModel?, builder: NotificationCompat.Builder? = null) {
        val remoteViews = RemoteViews(packageName, R.layout.player_notification)

        model?.let {
            remoteViews.apply {
                setTextViewText(R.id.author, model.author)
                setTextViewText(R.id.name, model.title)
                setImageViewResource(R.id.next_btn, R.drawable.ic_skip_next)
                setImageViewResource(R.id.previous_btn, R.drawable.ic_skip_previous)
                setImageViewResource(R.id.like_btn, R.drawable.ic_insert_emoticon)
                setImageViewResource(R.id.pause_resume_btn, model.pauseResumeToggleIcon)
            }
            builder?.setCustomBigContentView(remoteViews)
            listener(remoteViews, this)
        }
        builder?.let { notification = builder.build() }

        Glide.with(this).asBitmap().load(model?.image).listener(object : RequestListener<Bitmap?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap?>?,
                isFirstResource: Boolean
            ): Boolean {
                e?.printStackTrace()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                notificationManager.notify(PLAYER_NOTIFICATION_ID, notification)
                return false
            }
        }).into(NotificationTarget(this, R.id.image, remoteViews, notification, PLAYER_NOTIFICATION_ID))
        startForeground(PLAYER_NOTIFICATION_ID, notification)
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, this::class.java)
        intent.action = action
        return PendingIntent.getService(context, 0, intent, 0)
    }

    override fun like() {
        onDestroy()
    }

    override fun pauseResumeToggle() {
        val model = MediaModelUtils.playerModelOf(mediaList!!.current()!!)

        mediaPlayer.isPlaying().let {
            if (it) {
                mediaPlayer.pause()
                model.pauseResumeToggleIcon = R.drawable.ic_play_circle_outline
                mediaViewModel?.currentPlayState?.postValue(PlayerState.PLAYING)
            } else {
                mediaPlayer.resume()
                model.pauseResumeToggleIcon = R.drawable.ic_pause_circle_outline
                mediaViewModel?.currentPlayState?.postValue(PlayerState.PAUSE)
            }
        }
        showNotification(model)
    }


    override fun next() {
        mediaList?.let {
            if (!it.isEmpty()) {
                it.next()?.let { track ->
                    mediaPlayer.next()
                }
            }
        }
        showNotification(MediaModelUtils.playerModelOf(mediaList?.current()!!))
    }

    override fun previous() {
        mediaList?.let {
            if (!it.isEmpty()) {
                it.previous()?.let { track ->
                    mediaPlayer.previous()
                }
            }
        }
        showNotification(MediaModelUtils.playerModelOf(mediaList?.current()!!))
    }

    private fun stopMedia() {
        mediaPlayer?.let {
            if (it.isPlaying()) {
                it.pause()
            }
        }
    }

    inner class LocalBinder : Binder() {
        val service: MusicPlayerService
            get() = this@MusicPlayerService
    }
}