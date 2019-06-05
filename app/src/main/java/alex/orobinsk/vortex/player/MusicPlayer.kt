package alex.orobinsk.vortex.player

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MusicPlayer(private val context: App) : MediaPlayer {
    private lateinit var exoPlayer: ExoPlayer
    private var currentMode: PlayMode = PlayMode.SINGLE
    private var firstSetup: Boolean = true

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector()
        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(context)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            context,
            renderersFactory, trackSelector, loadControl
        )
    }

    override fun play(url: String) {
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        val mediaSource = ProgressiveMediaSource
            .Factory(DefaultDataSourceFactory(context, userAgent), DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(url))
        exoPlayer.prepare(mediaSource)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.playWhenReady = true
    }

    override fun setMode(mode: PlayMode) {
        this.currentMode = mode
    }

    override fun play(urls: List<String>, playerListener: PlayerListener) {
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        val mediaSource = ConcatenatingMediaSource()
        urls.forEach {
            mediaSource.addMediaSource(
                ProgressiveMediaSource
                    .Factory(DefaultDataSourceFactory(context, userAgent), DefaultExtractorsFactory())
                    .createMediaSource(Uri.parse((it)))
            )
        }
        exoPlayer.prepare(mediaSource)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.playWhenReady = true

        exoPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                super.onTracksChanged(trackGroups, trackSelections)
            }
        })
    }

    override fun releasePlayer() {
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun setVolume(volume: Float) {
        (exoPlayer as SimpleExoPlayer).volume = volume
    }

    override fun isPlaying(): Boolean = exoPlayer.playWhenReady

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun resume() {
        exoPlayer.playWhenReady = true
    }

    override fun previous() {
        exoPlayer.previous()
    }

    override fun next() {
        exoPlayer.next()
    }
}