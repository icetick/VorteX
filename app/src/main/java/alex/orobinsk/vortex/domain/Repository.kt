package alex.orobinsk.vortex.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface Repository<T> : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job()

    suspend fun get(id: String): Deferred<T>
    fun create(vararg items: T)
    suspend fun update(vararg items: T)
    suspend fun remove(vararg ids: String)

    suspend fun removeAll(): Deferred<*>
    suspend fun getAll(): Deferred<MutableList<T>>
}