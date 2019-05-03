package alex.orobinsk.vortex.domain.networking

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.domain.model.Artist
import alex.orobinsk.vortex.domain.model.ChartTracksResponse
import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MusicAPI {
    @GET("music/")
    fun getMusicFeed(): Deferred<MutableList<ChartTracksResponse.Track>>

    @POST("")
    fun addMusicTrack(track: ChartTracksResponse.Track): Deferred<ResponseBody>

    @GET("/tracks/{id}")
    fun getTrack(@Path("id") id: String): Deferred<ChartTracksResponse.Track>

    @FormUrlEncoded
    @HTTP(method = "UPDATE", path = "/tracks/{id}", hasBody = true)
    fun updateTrack(@Body track: ChartTracksResponse.Track): Deferred<ResponseBody>

    @DELETE("/tracks/{id}")
    fun deleteTrack(@Path("id")id: String): Deferred<ResponseBody>

    @DELETE("/tracks/all")
    fun deleteAllTracks(): Deferred<ResponseBody>

    @POST("/users/login")
    fun login(@Body body: JsonObject): Deferred<ResponseBody>
    @GET("radio")
    fun getRadioSets(): Deferred<RadioResponse>

    @POST("?method=artist.getinfo&artist={artist}&api_key=${BuildConfig.LAST_FM_API_KEY}&format=json")
    fun getArtistInfo(@Path("artist") artist: String): Deferred<Artist>

    @POST("?method=chart.gettoptracks&api_key=${BuildConfig.LAST_FM_API_KEY}&format=json")
    fun getChartTracks(): Deferred<ChartTracksResponse>

    @GET
    fun getHtml(@Url query: String): Deferred<ResponseBody>

    @GET("radio/{id}/tracks")
    fun getRadioTracks(@Path("id") id: String): Deferred<TracksResponse>
}