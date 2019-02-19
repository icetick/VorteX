package alex.orobinsk.vortex.domain.model

class TracksResponse(override var data: List<Data>): DataContainer<TracksResponse.Data> {
    data class Data(
        val album: Album,
        val artist: Artist,
        val duration: Int,
        val explicit_content_cover: Int,
        val explicit_content_lyrics: Int,
        val explicit_lyrics: Boolean,
        val id: Int,
        val link: String,
        val preview: String,
        val rank: Int,
        val readable: Boolean,
        val title: String,
        val title_short: String,
        val title_version: String,
        val type: String
    )
    data class Artist(
        val id: Int,
        val link: String,
        val name: String,
        val picture: String,
        val picture_big: String,
        val picture_medium: String,
        val picture_small: String,
        val picture_xl: String,
        val tracklist: String,
        val type: String
    )

    data class Album(
        val cover: String,
        val cover_big: String,
        val cover_medium: String,
        val cover_small: String,
        val cover_xl: String,
        val id: Int,
        val title: String,
        val tracklist: String,
        val type: String
    )
}



