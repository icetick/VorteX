package alex.orobinsk.vortex.player

interface MediaPlayer {
    fun setMode(mode: PlayMode)
    fun play(url: String)
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun pause()
    fun resume()
    fun setVolume(volume: Float)
    fun play(urls: List<String>)
    fun next()
    fun previous()
}