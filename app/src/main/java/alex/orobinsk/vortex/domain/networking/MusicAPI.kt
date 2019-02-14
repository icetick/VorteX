package alex.orobinsk.vortex.domain.networking

import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicAPI {
    /*@GET("music/")
    fun getMusicFeed(): Deferred<MutableList<Track>>

    @POST("")
    fun addMusicTrack(track: Track): Deferred<ResponseBody>

    @GET("/tracks/{id}")
    fun getTrack(@Path("id") id: String): Deferred<Track>

    @FormUrlEncoded
    @HTTP(method = "UPDATE", path = "/tracks/{id}", hasBody = true)
    fun updateTrack(@Body track: Track): Deferred<ResponseBody>

    @DELETE("/tracks/{id}")
    fun deleteTrack(@Path("id")id: String): Deferred<ResponseBody>

    @DELETE("/tracks/all")
    fun deleteAllTracks(): Deferred<ResponseBody>

    @POST("/users/login")
    fun login(@Body body: JsonObject): Deferred<ResponseBody>*/
    @GET("radio")
    fun getRadioSets(): Deferred<RadioResponse>

    @GET("radio/{id}/tracks")
    fun getRadioTracks(@Path("id") id: String): Deferred<TracksResponse>
}