package alex.orobinsk.vortex.player

interface MediaPlayer {
    fun play(url: String)
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun pause()
    fun resume()
    fun setVolume(volume: Float)
}