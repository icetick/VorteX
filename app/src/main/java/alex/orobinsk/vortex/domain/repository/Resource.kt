package alex.orobinsk.vortex.domain.repository

data class Resource<R>(var status: Status, var data: R? = null, var errorMessage: String? = null) {
    companion object {
        fun <R> success(data: R): Resource<R> = Resource(Status.SUCCESS, data)
        fun <R> loading(): Resource<R> = Resource(Status.LOADING)
        fun <R> error(message: String?): Resource<R> = Resource(Status.ERROR, errorMessage = message)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING;

    fun isSuccessful() = this == SUCCESS
    fun isLoading() = this == LOADING
}
