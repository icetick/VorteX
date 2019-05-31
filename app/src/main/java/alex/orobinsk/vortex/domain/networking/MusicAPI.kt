package alex.orobinsk.vortex.domain.networking

import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicAPI {
    @GET("radio")
    fun getRadioSets(): Deferred<RadioResponse>

    @GET("radio/{id}/tracks")
    fun getRadioTracks(@Path("id") id: String): Deferred<TracksResponse>
}