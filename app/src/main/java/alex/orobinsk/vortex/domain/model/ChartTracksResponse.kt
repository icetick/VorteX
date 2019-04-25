package alex.orobinsk.vortex.domain.model

import com.google.gson.annotations.SerializedName

data class ChartTracksResponse(
    val tracks: Tracks
) {
    data class Tracks(
        @SerializedName("@attr")
        val attr : Attr,
        val track: List<Track>
    )

    data class Track(
        val artist: alex.orobinsk.vortex.domain.model.Artist,
        val duration: String,
        val image: List<Image>,
        val listeners: String,
        val mbid: String,
        val name: String,
        val playcount: String,
        val streamable: Streamable,
        val url: String
    )

    data class Artist(
        val mbid: String,
        val name: String,
        val url: String
    )

    data class Image(
        @SerializedName("#text")
        val text: String,
        val size: String
    )

    data class Streamable(
        @SerializedName("#text")
        val text: String,
        val fulltrack: String
    )

    data class Attr(
        val page: String,
        val perPage: String,
        val total: String,
        val totalPages: String
    )
}

