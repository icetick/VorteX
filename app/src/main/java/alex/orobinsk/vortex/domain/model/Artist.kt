package alex.orobinsk.vortex.domain.model

import com.google.gson.annotations.SerializedName

data class Artist(
    val bio: Bio,
    val image: List<Image>,
    val mbid: String,
    val name: String,
    val ontour: String,
    val similar: Similar,
    val stats: Stats,
    val streamable: String,
    val tags: Tags,
    val url: String
) {
    data class Similar(
        val artist: List<ArtistX>
    )

    data class ArtistX(
        val image: List<Image>,
        val name: String,
        val url: String
    )

    data class Image(
        @SerializedName("#text")
        val text: String,
        val size: String
    )

    data class Tags(
        val tag: List<Tag>
    )

    data class Tag(
        val name: String,
        val url: String
    )

    data class Stats(
        val listeners: String,
        val playcount: String
    )

    data class Bio(
        val content: String,
        val links: Links,
        val published: String,
        val summary: String
    )

    data class Links(
        val link: Link
    )

    data class Link(
        @SerializedName("#text")
        val text: String,
        val href: String,
        val rel: String
    )
}

