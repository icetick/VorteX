package alex.orobinsk.vortex.domain.repository

import alex.orobinsk.vortex.domain.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Deferred

class FirebaseRepository<T: Any>: Repository<Task<QuerySnapshot>> {
    override suspend fun get(id: String): Deferred<Task<QuerySnapshot>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun remove(vararg ids: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeAll(): Deferred<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAll(): Deferred<MutableList<Task<QuerySnapshot>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun create(vararg items: Task<QuerySnapshot>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun update(vararg items: Task<QuerySnapshot>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val firebaseStore = FirebaseFirestore.getInstance()
}
