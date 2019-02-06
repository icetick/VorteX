package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.util.*
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.deezer.sdk.model.Permissions
import com.deezer.sdk.network.connect.DeezerConnect
import com.deezer.sdk.network.connect.event.DialogListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import android.webkit.WebViewClient
import okhttp3.Cookie


//@ObservableVM //Automatically creates observable class for all livedata members
class SplashViewModel : BaseViewModel() {
    val application: App by instance()
    val deezerConnectInstance: DeezerConnect by instance()

    private val SPLASH_END_TIME: Long = 3100
    var duration: Long = 20000

    val androidID: MutableLiveData<String> = MutableLiveData()
    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val splashEnded = MutableLiveData<Boolean>().apply { value = false }
    val afterLogoAnimationEnabled = MutableLiveData<Boolean>().apply { value = false }
    val progressBarAnimationEnabled = MutableLiveData<Boolean>().apply { value = false }

    val defaultEmail = BuildConfig.DEFAULT_EMAIL
    val defaultPassword = BuildConfig.DEFAULT_PASSWORD
    init {
        //TODO: Get androidID
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
        GlobalScope.launch(Dispatchers.IO) {
            delay(SPLASH_END_TIME) {
                progressBarAnimationEnabled.postValue(false)
            }
        }
        if (!validateFields()) {
            GlobalScope.launch(Dispatchers.Main) {
                val htmlCode = withContext(Dispatchers.IO) {
                    DeezerAuthenticationHelper.authenticate(defaultEmail, defaultPassword) {}
                }
                val alert = AlertDialog.Builder(it.context)
                alert.setTitle("Title here")

                val view = WebView(it.context)
                view.settings.javaScriptEnabled = true
                view.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        if(url!!.contains("code=")) {
                            val code = url.split("code=")[1]
                        }
                        view?.loadUrl("javascript:(function() {" +
                                "document.getElementById('login_mail').value='$defaultEmail';" +
                                "document.getElementById('login_password').value='$defaultPassword';" +
                                "document.getElementById('login_mail').dispatchEvent(new Event('input'));" +
                                "document.getElementById('login_password').dispatchEvent(new Event('input'));" +
                                "document.getElementById('login_form_submit').click();"+"})()")
                        super.onPageFinished(view, url)
                    }
                }
                view.loadUrl("https://connect.deezer.com/oauth/auth.php?app_id=327262&redirect_uri=http://www.alexorovortex.com&perms=basic_access,email")


                alert.setView(view)
                alert.setNegativeButton("Close"
                ) { dialog, _ -> dialog?.dismiss() }
                alert.show()

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

    fun onLongClick() = View.OnLongClickListener { v ->
        progressBarAnimationEnabled.value = true
        v.hideKeyboard()
        GlobalScope.launch(Dispatchers.IO) {
            delay(SPLASH_END_TIME) {
                progressBarAnimationEnabled.postValue(false)
            }
        }
        if (!validateFields()) {
            val permissions =
                arrayOf(Permissions.BASIC_ACCESS, Permissions.MANAGE_LIBRARY, Permissions.LISTENING_HISTORY)
            val listener = object : DialogListener {
                override fun onComplete(p0: Bundle?) {
                    Toast.makeText(v.context, p0?.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onCancel() {

                }

                override fun onException(p0: Exception?) {
                    Log.e("ERROR", p0?.localizedMessage)
                }

            }
            deezerConnectInstance.authorize(bindedActivity, permissions, listener)
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