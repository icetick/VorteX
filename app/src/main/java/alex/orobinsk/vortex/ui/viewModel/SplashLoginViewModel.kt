package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.domain.api.MusicApiType
import alex.orobinsk.vortex.domain.api.deezer.DeezerAuthenticationHelper
import alex.orobinsk.vortex.model.shared.PreferencesStorage
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.util.*
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.deezer.sdk.model.Permissions
import com.deezer.sdk.network.connect.DeezerConnect
import com.deezer.sdk.network.connect.SessionStore
import com.deezer.sdk.network.connect.event.DialogListener
import com.github.felixgail.gplaymusic.api.GPlayMusic
import com.github.felixgail.gplaymusic.util.TokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import java.lang.Exception

class SplashLoginViewModel : BaseViewModel() {
    val application: App by instance()
    val preferences: PreferencesStorage by instance()
    val androidID: MutableLiveData<String> = MutableLiveData()
    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val splashEnded = MutableLiveData<Boolean>().apply { value = false }
    val afterLogoAnimationEnabled = MutableLiveData<Boolean>().apply { value = false }
    val progressBarAnimationEnabled = MutableLiveData<Boolean>().apply { value = false }
    val offlineLoginSucceded = MutableLiveData<Boolean>()
    val loginSucceeded = MutableLiveData<Boolean>()
    val updateProgress: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentApi: MusicApiType = MusicApiType.DEEZER

    val defaultEmail = BuildConfig.DEFAULT_EMAIL
    val defaultPassword = BuildConfig.DEFAULT_PASSWORD
    private val SPLASH_END_TIME: Long = 3100

    override fun onCreated() {
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
            when (currentApi) {
                MusicApiType.DEEZER -> {
                    val deezerConnector =
                        DeezerConnect(it.context, BuildConfig.DEEZER_APPLICATION_ID)
                    val permissions = arrayOf(
                        Permissions.BASIC_ACCESS,
                        Permissions.MANAGE_LIBRARY,
                        Permissions.LISTENING_HISTORY
                    )
                    val listener = object : DialogListener {
                        override fun onComplete(p0: Bundle?) {
                            val store = SessionStore()
                            store.save(deezerConnector, it.context)
                            //deezerConnector.requestAsync()
                        }

                        override fun onCancel() {
                            Log.e("TAG", "Cancelled")
                        }

                        override fun onException(p0: Exception?) {
                            p0?.printStackTrace()
                        }
                    }

                    deezerConnector.authorize(it.context as Activity, permissions, listener)
                    /* val deezerAuthenticator = DeezerAuthenticationHelper.with(it.context)
                     deezerAuthenticator.authenticate(defaultEmail, defaultPassword) { authenticationCode ->
                         GlobalScope.launch(Dispatchers.Main) {
                             deezerAuthenticator.removeWebView()
                             val tokenResponse = withContext(Dispatchers.IO) { deezerAuthenticator.getTokenResponse(authenticationCode) }
                             preferences.storeExpirationTime(tokenResponse.expirationTime)
                             onLoginSucceded(tokenResponse.token)
                         }
                     }*/
                }
                MusicApiType.GPLAY -> {
                    //TODO: Implement GOOGLE Play Music feature
                    GlobalScope.launch(Dispatchers.Main) {
                        val token = withContext(Dispatchers.IO) {
                            TokenProvider.provideToken(
                                userName.value,
                                password.value,
                                androidID.value
                            )
                        }
                        val api = GPlayMusic.Builder().setAuthToken(token).build()
                        Toast.makeText(
                            it.context,
                            api.listenNowSituation.situations.first().imageUrl.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                MusicApiType.SPOTIFY -> {

                }
            }
        }
    }

    fun onLoginViaDeezer() = View.OnClickListener {
        val deezerConnector = DeezerConnect(it.context, BuildConfig.DEEZER_APPLICATION_ID)
        val permissions = arrayOf(
            Permissions.BASIC_ACCESS,
            Permissions.MANAGE_LIBRARY,
            Permissions.LISTENING_HISTORY
        )
        val listener = object : DialogListener {
            override fun onComplete(p0: Bundle?) {
                val store = SessionStore()
                store.save(deezerConnector, it.context)
                //deezerConnector.requestAsync()
            }

            override fun onCancel() {
                Log.e("TAG", "Cancelled")
            }

            override fun onException(p0: Exception?) {
                p0?.printStackTrace()
            }
        }

        deezerConnector.authorize(it.context as Activity, permissions, listener)
    }

    fun onLoginSucceded(token: String) {
        preferences.storeToken(token)
        progressBarAnimationEnabled.postValue(false)
        application.enqueueTokenRefresh()
        loginSucceeded.postValue(true)
    }

    fun onOfflineLoginSucceded() {
        progressBarAnimationEnabled.postValue(false)
        offlineLoginSucceded.postValue(true)
    }

    fun onLongClick() = View.OnLongClickListener { v ->
        progressBarAnimationEnabled.value = true
        v.hideKeyboard()
        if (!validateFields()) {
            val deezerAuthenticator = DeezerAuthenticationHelper.with(v.context)
            deezerAuthenticator.showAlertAuthentication { code ->
                GlobalScope.launch(Dispatchers.Main) {
                    deezerAuthenticator.removeWebView()
                    val token = try {
                        withContext(Dispatchers.IO) {
                            deezerAuthenticator.getTokenResponse(code).token
                        }
                    } catch (ex: Exception) {
                        null
                    }
                    if (token != null) {
                        onLoginSucceded(token)
                    }
                }
            }
        }
        false
    }

    private fun validateFields(): Boolean {
        if (userName isValidAs ValidationType.EMAIL &&
            androidID isValidAs ValidationType.ANDROID_ID &&
            password isValidAs ValidationType.PASSWORD
        ) {
            return true
        }
        return false
    }

}