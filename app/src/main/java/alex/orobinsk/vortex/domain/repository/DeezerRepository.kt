package alex.orobinsk.vortex.domain.repository

import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.networking.MusicAPI
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import kotlin.coroutines.CoroutineContext

/**
 * Created by Alex Orobinskiy 07.02.2019
 */
open class DeezerRepository(override val kodein: Kodein, val coroutineContext: CoroutineContext) : KodeinAware {
    val musicAPI: MusicAPI by instance()

    fun getRadioResponse(): LiveData<Resource<RadioResponse?>> {
        return object : NetworkResource<RadioResponse>(coroutineContext) {
            override fun saveNetworkCallResult(item: RadioResponse) {
            }

            override fun dbCall(): RadioResponse? = null
            override fun networkCall(): Deferred<RadioResponse> = musicAPI.getRadioSets()
            override fun shouldFetch(data: RadioResponse?): Boolean = true
        }.asLiveData()
    }

    fun getRadioTracks(vararg parameters: String): LiveData<Resource<TracksResponse?>> {
        return object : NetworkResource<TracksResponse>(coroutineContext) {
            override fun saveNetworkCallResult(item: TracksResponse) {

            }

            override fun shouldFetch(data: TracksResponse?): Boolean = true
            override fun dbCall(): TracksResponse? = null
            override fun networkCall(): Deferred<TracksResponse> = musicAPI.getRadioTracks(parameters.first())
        }.asLiveData()
    }
}