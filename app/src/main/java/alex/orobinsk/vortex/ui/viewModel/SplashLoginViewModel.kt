package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.model.shared.PreferencesStorage
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.util.*
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.deezer.sdk.network.connect.DeezerConnect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance


//@ObservableVM //Automatically creates observable class for all livedata members
class SplashLoginViewModel : BaseViewModel() {
    val application: App by instance()
    val deezerConnectInstance: DeezerConnect by instance()
    val preferences: PreferencesStorage by instance()
    val androidID: MutableLiveData<String> = MutableLiveData()
    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val splashEnded = MutableLiveData<Boolean>().apply { value = false }
    val afterLogoAnimationEnabled = MutableLiveData<Boolean>().apply { value = false }
    val progressBarAnimationEnabled = MutableLiveData<Boolean>().apply { value = false }
    val loginSucceeded = MutableLiveData<Boolean>()

    val defaultEmail = BuildConfig.DEFAULT_EMAIL
    val defaultPassword = BuildConfig.DEFAULT_PASSWORD
    private val SPLASH_END_TIME: Long = 3100
    var duration: Long = 20000

    init {
        //TODO: Get androidID for Google Play Music(?)
        //androidID.postValue(Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID))
        GlobalScope.launch(Dispatchers.IO) {
            delay(SPLASH_END_TIME) {
                splashEnded.postValue(true)
            }
        }
    }

    fun onSignInClicked() = View.OnClickListener {
        progressBarAnimationEnabled.value = true
        it.hideKeyboard()

        if (!validateFields()) {
            val deezerAuthenticator = DeezerAuthenticationHelper.with(it.context)
            deezerAuthenticator.authenticate(defaultEmail, defaultPassword) { authenticationCode ->
                GlobalScope.launch(Dispatchers.Main) {
                    deezerAuthenticator.removeWebView()
                    val token = withContext(Dispatchers.IO) { deezerAuthenticator.getToken(authenticationCode) }
                    onLoginSucceded(token)
                }
            }

            //TODO: Implement GOOGLE Play Music feature
            /*GlobalScope.launch(Dispatchers.Main) {
                val token = withContext(Dispatchers.IO) {
                    TokenProvider.provideToken(userName.value, password.value, androidID.value)
                }
                val api = GPlayMusic.Builder().setAuthToken(token).build()
                Toast.makeText(it.context, api.listenNowSituation.situations.first().imageUrl.toString(), Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    fun onLoginSucceded(token: String) {
        preferences.storeToken(token)
        progressBarAnimationEnabled.postValue(false)
        loginSucceeded.postValue(true)
    }

    fun onLongClick() = View.OnLongClickListener { v ->
        progressBarAnimationEnabled.value = true
        v.hideKeyboard()
        if (!validateFields()) {
            val deezerAuthenticator = DeezerAuthenticationHelper.with(v.context)
            deezerAuthenticator.showAlertAuthentication { code ->
                GlobalScope.launch(Dispatchers.Main) {
                    deezerAuthenticator.removeWebView()
                    val token = withContext(Dispatchers.IO) { deezerAuthenticator.getToken(code) }
                    onLoginSucceded(token)
                }
            }
        }
        false
    }

    private fun validateFields(): Boolean {
        if (userName isValid ValidationType.EMAIL && androidID isValid ValidationType.ANDROID_ID && password isValid ValidationType.PASSWORD) {
            return true
        }
        return false
    }

}