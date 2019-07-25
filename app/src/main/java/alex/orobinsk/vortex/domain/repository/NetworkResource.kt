package alex.orobinsk.vortex.domain.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class NetworkResource<R> constructor(override val coroutineContext: CoroutineContext) :
    CoroutineScope {
    private val result = MediatorLiveData<Resource<R?>>()

    init {
        result.value = Resource.loading()
        val dbSource = MutableLiveData<R>()
        dbSource.value = this.dbCall()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData -> setValue(Resource.success(newData)) }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<R>) {
        val apiResponse = networkCall()
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext: CoroutineContext, throwable: Throwable ->
            onFetchFailed()
            result.addSource(dbSource) { result.value = Resource.error(throwable.localizedMessage) }
        }
        launch(Dispatchers.Main + coroutineContext + exceptionHandler) {
            result.addSource(dbSource) { result.setValue(Resource.loading()) }
            val fetchedData = withContext(Dispatchers.IO) { apiResponse.await() }.mutableLiveData()
            result.addSource(fetchedData) { response ->
                result.removeSource(fetchedData)
                result.removeSource(dbSource)
                launch(Dispatchers.IO) {
                    processResponse(Resource.success(response))?.let { requestType: R ->
                        saveNetworkCallResult(
                            requestType
                        )
                    }
                }
                if (dbCall() != null) {
                    result.addSource(dbCall().mutableLiveData()) { newData ->
                        setValue(Resource.success(newData))
                    }
                } else {
                    setValue(Resource.success(response))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<R?>) {
        if (result.value != newValue) result.value = newValue
    }

    protected fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<R?>> {
        return result
    }

    fun R?.mutableLiveData(): MutableLiveData<R> {
        val data = MutableLiveData<R>()
        data.value = this
        return data
    }

    fun cancelJob() {
        coroutineContext.cancel()
    }

    private fun processResponse(response: Resource<R>): R? {
        return response.data
    }

    protected abstract fun saveNetworkCallResult(item: R)
    protected abstract fun shouldFetch(data: R?): Boolean
    protected abstract fun dbCall(): R?
    protected abstract fun networkCall(): Deferred<R>
}