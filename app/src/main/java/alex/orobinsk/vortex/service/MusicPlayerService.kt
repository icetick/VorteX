package alex.orobinsk.vortex.service

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.ui.view.MainActivity
import alex.orobinsk.vortex.util.MediaList
import alex.orobinsk.vortex.util.NotificationPlayer
import alex.orobinsk.vortex.util.Utils
import alex.orobinsk.vortex.util.firstAvailable
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
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
import org.kodein.di.javaType
import java.lang.Exception

class MusicPlayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener, NotificationPlayer {

    private val binder: IBinder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var resumePosition: Int = 0
    private var waitingForStart: Boolean = false
    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null
    private lateinit var notificationManager: NotificationManager
    private val PLAYER_NOTIFICATION_ID: Int = 5532
    private var notification: Notification? = null
    private val PLAYER_NOTIFICATION_CHANNEL_ID: String = "music_channel_007"
    private val receiver = MusicControlReceiver()

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
        if(mediaPlayer==null) {
            mediaPlayer = MediaPlayer()

            mediaPlayer?.setOnCompletionListener(this)
            mediaPlayer?.setOnErrorListener(this)
            mediaPlayer?.setOnPreparedListener(this)
            mediaPlayer?.setOnBufferingUpdateListener(this)
            mediaPlayer?.setOnSeekCompleteListener(this)
            mediaPlayer?.setOnInfoListener(this)

            mediaPlayer?.reset()

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            mediaPlayer?.setAudioAttributes(audioAttributes)
            try {
                mediaList?.firstAvailable()?.let {
                    mediaPlayer?.setDataSource(it.preview)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                stopSelf()
            }
            mediaPlayer?.prepareAsync()
        } else {
            mediaPlayer?.reset()
            try {
                mediaList?.firstAvailable()?.let {
                    mediaPlayer?.setDataSource(it.preview)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                stopSelf()
            }
            mediaPlayer?.prepareAsync()
        }
        waitingForStart = true
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                notificationManager.createNotificationChannel(NotificationChannel(PLAYER_NOTIFICATION_CHANNEL_ID, "Music Player Notification Channel", NotificationManager.IMPORTANCE_LOW))
            } catch (ex: RemoteException) {

            }
        }
        if(!requestAudioFocus()) {
           stopSelf()
        }
        intent?.hasExtra(DEFAULT_ITEMSET)?.let {
            if(it) {
                intent.getStringExtra(DEFAULT_ITEMSET)?.let {tracks ->
                    val typeTokenList = object: TypeToken<List<TracksResponse.Data>>(){}.type
                    mediaList = MediaList.of(Gson().fromJson(tracks, typeTokenList))
                }
                mediaList?.isNotEmpty().let {
                    if(notification==null) {
                        mediaList?.current()?.let {track ->
                            showNotification(Utils.PlayerModelOf(track))
                        }
                    }
                }
            } else {
                when(intent.action) {
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
            when(intent?.action) {
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
        super.onDestroy()
        if(mediaPlayer!=null) {
            stopMedia()
            stopForeground(true)
            mediaPlayer?.release()
        }
        removeAudioFocus()
        unregisterReceiver(receiver)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mediaList?.let {
            if(it.isEmpty()) {
                stopMedia()
                stopSelf()
            } else {
                it.next()?.let {track ->
                    resetupSource(track.preview)
                }
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if(waitingForStart) {
            playMedia()
            notification?.let {
                showNotification(Utils.PlayerModelOf(mediaList!!.current()!!))
            }
        }
    }

    override fun onSeekComplete(mp: MediaPlayer?) {

    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra"
            )
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED $extra")
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN $extra")
        }
        return false
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onAudioFocusChange(focusState: Int) {
        //Invoked when the audio focus of the system is updated.
        when (focusState) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // resume playback
                mediaPlayer?.let {
                    if(!it.isPlaying) {
                        it.start()
                    }
                } ?: run {
                    initMediaPlayer()
                }
                mediaPlayer?.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus for an unbounded amount of time: stop playback and release media player
                mediaPlayer?.let {
                    if(it.isPlaying) {
                        it?.stop()
                    }
                }
                mediaPlayer?.release()
                mediaPlayer = null
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                mediaPlayer?.let {
                    if(it.isPlaying){
                        it.pause()
                    }
                }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                mediaPlayer?.let {
                    if(it.isPlaying) {
                        it.setVolume(0.1f, 0.1f)
                    }
                }
        }
    }

    @Suppress("DEPRECATION")
    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        val openIntent = Intent(this, MainActivity::class.java)
        val contentIntent =
            PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)
        builder.setSmallIcon(R.drawable.ic_stars)
        builder.setContentTitle("Vortex").setContentText("is Playing now").setOngoing(true)
        builder.priority = NotificationCompat.PRIORITY_HIGH
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
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                e?.printStackTrace()
                return false
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                notificationManager.notify(PLAYER_NOTIFICATION_ID, notification)
                return false
            }
        }).into(NotificationTarget(this, R.id.image, remoteViews, notification, PLAYER_NOTIFICATION_ID))
        startForeground(PLAYER_NOTIFICATION_ID,notification)
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, this::class.java)
        intent.action = action
        return PendingIntent.getService(context, 0, intent, 0)
    }

    override fun like() {

    }

    override fun pauseResumeToggle() {
        val model = Utils.PlayerModelOf(mediaList!!.current()!!)

        mediaPlayer?.isPlaying?.let {
            if(it) {
                pauseMedia()
                model.pauseResumeToggleIcon = R.drawable.ic_play_circle_outline
            } else {
                resumeMedia()
                model.pauseResumeToggleIcon = R.drawable.ic_pause_circle_outline
            }
        }
        showNotification(model)
    }



    override fun next() {
        mediaList?.let {
            if(!it.isEmpty()) {
                it.next()?.let {track ->
                    resetupSource(track.preview)
                }
            }
        }
        showNotification(Utils.PlayerModelOf(mediaList?.current()!!))
    }

    override fun previous() {
        mediaList?.let {
            if(!it.isEmpty()) {
                it.previous()?.let {track ->
                    resetupSource(track.preview)
                }
            }
        }
        updateNotification(Utils.PlayerModelOf(mediaList?.current()!!))
    }

    private fun resetupSource(source: String) {
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(source)
        mediaPlayer?.prepareAsync()
        waitingForStart = true
    }

    private fun playMedia() {
        mediaPlayer?.let {
            if(!it.isPlaying) {
                it.start()
            }
        }
    }

    private fun stopMedia() {
        mediaPlayer?.let {
            if(it.isPlaying) {
                it.stop()
            }
        }
    }

    private fun pauseMedia() {
        mediaPlayer?.let {
            if(it.isPlaying) {
                it.pause()
                resumePosition = it.currentPosition
            }
        }
    }

    private fun resumeMedia() {
        mediaPlayer?.let {
            if(!it.isPlaying) {
                it.seekTo(resumePosition)
                it.start()
            }
        }
    }

    inner class MusicControlReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("PGFSAFASAFSFASF", "ASFASFSAFSAFMASLFMKAFMASKLMFASKLFMKLASMFKLASMFKLSAKLAS")
            when(intent?.action) {
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

    }

    inner class LocalBinder : Binder() {
        val service: MusicPlayerService
            get() = this@MusicPlayerService
    }
}