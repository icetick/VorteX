package alex.orobinsk.vortex.domain.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class NetworkResource<ResultType> constructor(override val coroutineContext: CoroutineContext) :
    CoroutineScope {
    private val result = MediatorLiveData<Resource<ResultType?>>()

    init {
        result.value = Resource.loading()
        val dbSource = MutableLiveData<ResultType>()
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

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
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
                    processResponse(Resource.success(response))?.let { requestType: ResultType ->
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
    private fun setValue(newValue: Resource<ResultType?>) {
        if (result.value != newValue) result.value = newValue
    }

    protected fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType?>> {
        return result
    }

    fun ResultType?.mutableLiveData(): MutableLiveData<ResultType> {
        val data = MutableLiveData<ResultType>()
        data.value = this
        return data
    }

    fun cancelJob() {
        coroutineContext.cancel()
    }

    private fun processResponse(response: Resource<ResultType>): ResultType? {
        return response.data
    }

    protected abstract fun saveNetworkCallResult(item: ResultType)
    protected abstract fun shouldFetch(data: ResultType?): Boolean
    protected abstract fun dbCall(): ResultType?
    protected abstract fun networkCall(): Deferred<ResultType>
}