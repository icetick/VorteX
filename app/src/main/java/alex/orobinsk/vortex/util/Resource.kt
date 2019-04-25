package alex.orobinsk.vortex.util

import javax.annotation.Nonnull
import androidx.work.Operation.State.SUCCESS



class Resource<T> constructor(private val status: Status,
                              private val data: T? = null,
                              private val message: String? = null) {
    companion object {
        fun <T> success(data: T? = null): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String?, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    fun status() = status
    fun data() = data
    fun message() = message

    enum class Status {
        SUCCESS, ERROR, LOADING
    }
}