package alex.orobinsk.vortex.domain.repository

import alex.orobinsk.vortex.domain.model.Artist
import alex.orobinsk.vortex.domain.model.ChartTracksResponse
import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.networking.MusicAPI
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import kotlin.coroutines.CoroutineContext

/**
 * Created by Alex Orobinskiy 07.02.2019
 * This class does not implement Repository<T>, it`s 3rd-party and need to
 * be custom implemented
 */
open class MusicRepository(override val kodein: Kodein, override val coroutineContext: CoroutineContext): CoroutineScope, KodeinAware {
    @PublishedApi
    internal val publishedApi: MusicAPI by instance()
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext: CoroutineContext, throwable: Throwable -> throwable.printStackTrace() }

    /**
     * This method allows you to get any data depends on class, that you require
     * by reification
     * @param parameters declares all of parameters, that may be at request path ordered by usage
     * @param listener declares listener, that getting response after request done
     */
    inline fun<reified T> getData(vararg parameters: String, noinline listener: (T)-> Unit) = when(T::class) {
        Artist::class -> {
            loadData(publishedApi.getArtistInfo(parameters.first()), listener)
        }
        ChartTracksResponse::class -> {
            loadData(publishedApi.getChartTracks(), listener)
        }
        /*RadioResponse::class -> {
            loadData(publishedApi.getArtistInfo(parameters.first()), listener)
        }*/
        /*TracksResponse::class -> {
            loadData(publishedApi.getRadioTracks(parameters.first()), listener)
        }*/
        else -> {}
    }

    /**
     * This method allows you to call whole requests
     * in the same way in -> use one system of throwing exceptions
     * and make calls in one pool of coroutines
     * Casting as Deferred<T> is always working, cause executing method
     * must check this before execute
     */
    @SuppressWarnings("unchecked")
    fun<T> loadData(call: Deferred<*>, endBlock: (T) -> Unit) {
        call as Deferred<T>
        launch(Dispatchers.Main + coroutineContext + exceptionHandler) {
            val result = withContext(Dispatchers.IO) { call.await() }
            endBlock.invoke(result)
        }
    }

    /**
     * This method allows you to cancel your
     * current request
     */
    fun cancelJob() {
        coroutineContext.cancel()
    }


}