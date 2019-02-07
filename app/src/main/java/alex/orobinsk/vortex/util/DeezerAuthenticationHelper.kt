package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.*
import java.net.*
import io.grpc.internal.ReadableBuffers.openStream
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception


/**
 * This class is used to prevent deezer login to user. In out application we need to use internal login/password field, but deezer requires
 * to use their own interface. In this case we are making workaround to pass login/password to deezer login webpage
 */
class DeezerAuthenticationHelper private constructor(private val context: Context){
    val BASE_URL = "https://connect.deezer.com/oauth/auth.php"
    val BASE_URL_GET_TOKEN = "https://connect.deezer.com/oauth/access_token.php"
    val REDIRECT_URL = "http://www.alexorovortex.com"
    val APPLICATION_PERMISSIONS = "basic_access,email"
    val contentType = "Content-Type"
    val authenticationContentType = "application/x-www-form-urlencoded"
    val COOKIES_HEADER = "Set-Cookie"
    var webView: WebView? = null
    var msCookieManager = CookieManager()

    companion object {
        fun with(context: Context): DeezerAuthenticationHelper {
            return DeezerAuthenticationHelper(context)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Throws(IOException::class)
    fun authenticate(email: String, password: String, listener: (String)-> Unit) {
           webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = DeezerAuthenticationWebClient(listener, email, password)
                loadUrl("$BASE_URL?app_id=${BuildConfig.DEEZER_APPLICATION_ID}&redirect_uri=$REDIRECT_URL&perms=$APPLICATION_PERMISSIONS") }
    }

    fun setDeezerCookie(connection: URLConnection) {
        val headerFields = connection.headerFields
        val cookiesHeader = headerFields[COOKIES_HEADER]
        if (cookiesHeader != null) {
            for (cookie in cookiesHeader) {
                msCookieManager.cookieStore.add(null, HttpCookie.parse(cookie)[0])
            }
        }
    }

    fun removeWebView() {
        webView?.destroy()
        webView = null
    }

    fun getToken(code: String): String {
        val urlConnection = URL("$BASE_URL_GET_TOKEN?app_id=${BuildConfig.DEEZER_APPLICATION_ID}&secret=${BuildConfig.SECRET_KEY}&code=$code").openConnection()
        urlConnection.connect()
        val inputStream = DataInputStream(urlConnection.getInputStream())
        var htmlCode = ""
        try {
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            while (true) {
                val line = reader.readLine() ?: break
                htmlCode+=line
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return htmlCode.split("access_token=", "&expires=")[1]
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun showAlertAuthentication(listener: (String) -> Unit) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle(context.getString(R.string.authorization))
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = DeezerAuthenticationWebClient(listener)
            loadUrl("$BASE_URL?app_id=${BuildConfig.DEEZER_APPLICATION_ID}&redirect_uri=$REDIRECT_URL&perms=$APPLICATION_PERMISSIONS") }
        alert.setView(webView).setNegativeButton(context.getString(R.string.close)) {
                dialog, _ -> dialog?.dismiss(); webView?.destroy()  }
        alert.show()
    }

    fun getDeezerCookie(connection: URLConnection) {
        if (msCookieManager.cookieStore.cookies.size > 0) {
            // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
            connection.setRequestProperty("Cookie", TextUtils.join(";",  msCookieManager.cookieStore.cookies))
        }
    }

    class DeezerAuthenticationWebClient(var loadListener: ((String) -> Unit), var email: String? = null, var password: String? = null): WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            url.let { unwrappedUrl ->
                if(unwrappedUrl.contains("code=")) {
                    val code = unwrappedUrl.split("code=")[1]
                    loadListener.invoke(code)
                    return false
                }
            }
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            val jsInjectedLogin = "javascript:(function() {" +
                    "document.getElementsByName('continue')[0].click();" +
                    "document.getElementById('login_mail').value='${BuildConfig.DEFAULT_EMAIL}';" +
                    "document.getElementById('login_password').value='${BuildConfig.DEFAULT_PASSWORD}';" +
                    "document.getElementById('login_mail').dispatchEvent(new Event('input'));" +
                    "document.getElementById('login_password').dispatchEvent(new Event('input'));" +
                    "document.getElementById('login_form_submit').click();"+"})()"
            view?.loadUrl(jsInjectedLogin)
            super.onPageFinished(view, url)
        }
    }
}