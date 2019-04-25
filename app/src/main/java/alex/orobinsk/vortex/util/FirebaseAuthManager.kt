package alex.orobinsk.vortex.util

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthManager {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun signInUp(email: String, password: String, status: MutableLiveData<Resource<FirebaseUser>>) {
        status.postValue(Resource.loading(null))
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {signInResult ->
            if(signInResult.isSuccessful) {
                status.postValue(Resource.success(firebaseAuth.currentUser))
            } else {
                if(signInResult.exception is FirebaseAuthInvalidUserException) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {createUserResult ->
                        if(createUserResult.isSuccessful) {
                            status.postValue(Resource.success(firebaseAuth.currentUser))
                        } else {
                            status.postValue(Resource.error(createUserResult.exception?.localizedMessage))
                        }
                    }
                } else {
                    status.postValue(Resource.error(signInResult.exception?.localizedMessage))
                }
            }
        }
    }

    fun hasUser() = firebaseAuth.currentUser!=null

}