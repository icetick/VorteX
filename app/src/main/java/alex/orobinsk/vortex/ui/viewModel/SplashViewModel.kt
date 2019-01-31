package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.util.ValidationType
import alex.orobinsk.vortex.util.delay
import alex.orobinsk.vortex.util.isValid
import android.os.Handler
import android.provider.Settings
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.github.felixgail.gplaymusic.api.GPlayMusic
import com.github.felixgail.gplaymusic.util.TokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

//@ObservableVM //Automatically creates observable class for all livedata members
class SplashViewModel : BaseViewModel() {
    val application: App by instance()
    private val SPLASH_END_TIME: Long = 6000

    val androidID: MutableLiveData<String> = MutableLiveData()
    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val endSplash = MutableLiveData<Boolean>()
    val flag = MutableLiveData<Boolean>()

    init {
        androidID.postValue(Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID))
        flag.value = false
        endSplash.postValue(false)
        GlobalScope.launch(Dispatchers.IO) {
            delay(SPLASH_END_TIME) {
                endSplash.postValue(true)
            }
        }
    }

    fun onSignInClicked() = View.OnClickListener {
        if (validateFields()) {
            val authToken = TokenProvider.provideToken(userName.value, password.value, androidID.value)
            val api = GPlayMusic.Builder().setAuthToken(authToken).build()
        }
    }

    private fun validateFields(): Boolean {
        if (userName isValid ValidationType.EMAIL && androidID isValid ValidationType.ANDROID_ID && password isValid ValidationType.PASSWORD) {
            return true
        }
        return false
    }
}