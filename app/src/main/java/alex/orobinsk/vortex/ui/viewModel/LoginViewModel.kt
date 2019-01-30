package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.util.ValidationType.*
import alex.orobinsk.vortex.util.isValid
import android.provider.Settings.Secure
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.github.felixgail.gplaymusic.api.GPlayMusic
import com.github.felixgail.gplaymusic.util.TokenProvider
import org.kodein.di.generic.instance


class LoginViewModel : BaseViewModel() {
    val application: App by instance()
    val androidID: MutableLiveData<String> = MutableLiveData()
    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()

    init {
        androidID.postValue(Secure.getString(application.contentResolver, Secure.ANDROID_ID))
    }

    fun onSignInClicked() = View.OnClickListener {
        if (validateFields()) {
            val authToken = TokenProvider.provideToken(userName.value, password.value, androidID.value)
            val api = GPlayMusic.Builder().setAuthToken(authToken).build()
        }
    }

    private fun validateFields(): Boolean {
        if (userName isValid EMAIL && androidID isValid ANDROID_ID && password isValid PASSWORD) {
            return true
        }
        return false
    }
}