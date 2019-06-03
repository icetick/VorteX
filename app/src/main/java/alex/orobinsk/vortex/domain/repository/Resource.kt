package alex.orobinsk.vortex.domain.repository

data class Resource<ResultType>(var status: Status, var data: ResultType? = null, var errorMessage: String? = null) {
    companion object {
        fun <ResultType> success(data: ResultType): Resource<ResultType> = Resource(Status.SUCCESS, data)
        fun <ResultType> loading(): Resource<ResultType> = Resource(Status.LOADING)
        fun <ResultType> error(message: String?): Resource<ResultType> = Resource(Status.ERROR, errorMessage = message)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING;
    fun isSuccessful() = this == SUCCESS
    fun isLoading() = this == LOADING
}
