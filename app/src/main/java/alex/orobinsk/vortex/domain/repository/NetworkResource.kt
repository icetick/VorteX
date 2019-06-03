package alex.orobinsk.vortex.domain.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class NetworkResource<ResultType> constructor(override val coroutineContext: CoroutineContext) : CoroutineScope {
    private val result = MediatorLiveData<Resource<ResultType?>>()

    init {
        result.value = Resource.loading()
        val dbSource = MutableLiveData<ResultType>()
        dbSource.value = this.loadFromDb()
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
        val apiResponse = createCall()
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
                        saveCallResult(
                            requestType
                        )
                    }
                }
                if (loadFromDb() != null) {
                    result.addSource(loadFromDb().mutableLiveData()) { newData ->
                        setValue(
                            Resource.success(newData)
                        )
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

    @WorkerThread
    private fun processResponse(response: Resource<ResultType>): ResultType? {
        return response.data
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: ResultType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): ResultType?

    @MainThread
    protected abstract fun createCall(): Deferred<ResultType>
}