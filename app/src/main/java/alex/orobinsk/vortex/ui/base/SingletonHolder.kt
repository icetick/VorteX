package alex.orobinsk.vortex.ui.base

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(creatorArg: A): T {
        val inst = instance
        if (inst != null) {
            return inst
        }
        return synchronized(this) {
            val secondInstance = instance
            if (secondInstance != null) {
                secondInstance
            } else {
                val created = creator!!(creatorArg)
                instance = created
                creator = null
                created
            }
        }
    }
}