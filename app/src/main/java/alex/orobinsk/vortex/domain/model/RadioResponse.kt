package alex.orobinsk.vortex.domain.model

class RadioResponse(override var data: ArrayList<Data>) : DataContainer<RadioResponse.Data> {
    data class Data(
        val id: String,
        val picture: String,
        val picture_big: String,
        val picture_medium: String,
        val picture_small: String,
        val picture_xl: String,
        val title: String,
        val tracklist: String,
        val type: String
    )
}

