package alex.orobinsk.vortex.service

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.ui.view.MainActivity
import alex.orobinsk.vortex.util.MediaList
import alex.orobinsk.vortex.util.NotificationPlayer
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException
import android.widget.RemoteViews
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
    private var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val PLAYER_NOTIFICATION_ID: Int = 5532
    private var notification: Notification? = null
    private val NEXT_TAG = "nextTAG"
    private val PREVIOUS_TAG = "previousTAG"
    private val RESUME_PAUSE_TOOGLE_TAG = "pauseResumeTag"
    private val LIKE_TAG = "likeTAG"
    private val PLAYER_NOTIFICATION_CHANNEL_ID: String = "MusicPlayerVortex"

    var mediaList: MediaList<TracksResponse.Data>? = null
    set(value) {
        field = value
        initMediaPlayer()
    }

    companion object {
        const val DEFAULT_ITEMSET = "defItems"
    }

    private fun initMediaPlayer() {
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
            mediaPlayer?.setDataSource(mediaList?.first()!!.preview)
        } catch (ex: IOException) {
            ex.printStackTrace()
            stopSelf()
        }
        mediaPlayer?.prepareAsync()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                notificationManager.getNotificationChannel(PLAYER_NOTIFICATION_CHANNEL_ID)
            } catch (ex: RemoteException) {
                notificationManager.createNotificationChannel(NotificationChannel(PLAYER_NOTIFICATION_CHANNEL_ID, "Music Player Notification Channel", NotificationManager.IMPORTANCE_LOW))
            }
        }
        if(!requestAudioFocus()) {
           stopSelf()
        }
        intent?.hasExtra(DEFAULT_ITEMSET)?.let {
            if(it) {
                intent.getParcelableArrayListExtra<TracksResponse>(DEFAULT_ITEMSET)?.let {tracks ->
                    mediaList = MediaList(*tracks)
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
        }

        mediaList?.isEmpty().let {
            if(notification==null) {
                mediaList?.current()?.let {track ->
                    startForeground(PLAYER_NOTIFICATION_ID, showNotification(PlayerNotificationModelBuilder()
                        .appName("Vortex")
                        .author(track.)))
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
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mediaList?.let {
            if(it.isEmpty()) {
                stopMedia()
                stopSelf()
            } else {
                it.next()?.let {track ->
                    resetupSource(track)
                }
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if(waitingForStart) {
            playMedia()
        }
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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


    fun showNotification(model: PlayerNotificationModel): Notification? {
        val builder = NotificationCompat.Builder(applicationContext, PLAYER_NOTIFICATION_CHANNEL_ID)
        val openIntent = Intent(this, MainActivity::class.java)
        val contentIntent =
            PendingIntent.getActivity(applicationContext, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)

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

    private fun updateNotification(model: PlayerNotificationModel?, builder: NotificationCompat.Builder? = null): Notification? {
        model?.let {
            val remoteViews = RemoteViews(packageName, R.layout.player_notification)
            remoteViews.setImageViewBitmap(R.id.image, model.image)
            remoteViews.setTextViewText(R.id.author, model.author)
            remoteViews.setTextViewText(R.id.name, model.title)
            remoteViews.setImageViewResource(R.id.pause_resume_btn, model.pauseResumeToggleIcon)
            listener(remoteViews, applicationContext)
            builder?.notification?.contentView = remoteViews
            builder?.notification?.flags = builder?.notification?.flags?.or(Notification.FLAG_AUTO_CANCEL)
        }
        builder?.let { notification = builder.build() }
        notificationManager.notify(PLAYER_NOTIFICATION_ID, notification)
        return notification
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    override fun like() {

    }

    override fun pauseResumeToggle() {
        mediaPlayer?.isPlaying?.let {
            if(it) {
                pauseMedia()
            } else {
                resumeMedia()
            }
        }
        updateNotification()
    }



    override fun next() {
        mediaList?.let {
            if(!it.isEmpty()) {
                it.next()?.let {track ->
                    resetupSource(track)
                }
            }
        }
        updateNotification(mediaList?.current())
    }

    override fun previous() {
        mediaList?.let {
            if(!it.isEmpty()) {
                it.previous()?.let {track ->
                    resetupSource(track)
                }
            }
        }
        updateNotification(model)
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

    inner class LocalBinder : Binder() {
        val service: MusicPlayerService
            get() = this@MusicPlayerService
    }
}