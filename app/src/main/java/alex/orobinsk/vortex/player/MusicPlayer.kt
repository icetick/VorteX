package alex.orobinsk.vortex.player

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MusicPlayer(private val context: App): MediaPlayer {
    private lateinit var exoPlayer: ExoPlayer

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector()
        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(context)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context,
            renderersFactory, trackSelector, loadControl)
    }

    override fun play(url: String) {
        //1
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        //2
        val mediaSource = ProgressiveMediaSource
            .Factory(DefaultDataSourceFactory(context, userAgent), DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(url))
        //3
        exoPlayer.prepare(mediaSource)

        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        //4
        exoPlayer.playWhenReady = true
    }

//    override fun play(urls: TrackGroupArray) {
//        //1
//        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
//        //2
//        val mediaSource = ProgressiveMediaSource
//            .Factory(DefaultDataSourceFactory(context, userAgent), DefaultExtractorsFactory())
//            .createMediaSource(urls)
//        //3
//        exoPlayer.prepare(mediaSource)
//
//        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
//        //4
//        exoPlayer.playWhenReady = true
//    }

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
}