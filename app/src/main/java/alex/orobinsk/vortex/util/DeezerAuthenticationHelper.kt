package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.R
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import java.io.*
import java.net.*

/**
 * This class is used to prevent deezer login to user. In out application we need to use internal login/password field, but deezer requires
 * to use their own interface. In this case we are making workaround to pass login/password to deezer login webpage
 */
class DeezerAuthenticationHelper private constructor(val context: Context){
    val BASE_URL = "https://connect.deezer.com/oauth/auth.php"
    val REDIRECT_URL = "http://www.alexorovortex.com"
    val APPLICATION_PERMISSIONS = "basic_access,email"
    val contentType = "Content-Type"
    val authenticationContentType = "application/x-www-form-urlencoded"

    val COOKIES_HEADER = "Set-Cookie"
    var msCookieManager = CookieManager()

    companion object {
        fun with(context: Context): DeezerAuthenticationHelper {
            return DeezerAuthenticationHelper(context)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Throws(IOException::class)
    fun authenticate(email: String, password: String, listener: (String)-> Unit) {
            WebView(context).apply {
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

    @SuppressLint("SetJavaScriptEnabled")
    fun showAlertAuthentication() {
        val alert = AlertDialog.Builder(context)
        alert.setTitle(context.getString(R.string.authorization))
        alert.setView(
            WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = DeezerAuthenticationWebClient()
            loadUrl("$BASE_URL?app_id=${BuildConfig.DEEZER_APPLICATION_ID}&redirect_uri=$REDIRECT_URL&perms=$APPLICATION_PERMISSIONS") })
            .setNegativeButton(context.getString(R.string.close)) { dialog, _ -> dialog?.dismiss() }
        alert.show()
    }

    fun getDeezerCookie(connection: URLConnection) {
        if (msCookieManager.cookieStore.cookies.size > 0) {
            // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
            connection.setRequestProperty("Cookie", TextUtils.join(";",  msCookieManager.cookieStore.cookies))
        }
    }

    class DeezerAuthenticationWebClient(var loadListener: ((String) -> Unit)? = null, var email: String? = null, var password: String? = null): WebViewClient() {
        val idInputEmail = "login_email"
        val idInputPassword = "login_password"
        val idSubmitForm = "login_form_submit"

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
           /* url?.let { unwrappedUrl ->
                if(unwrappedUrl.contains("code=")) {
                    val code = unwrappedUrl.split("code=")[1]
                    loadListener?.invoke(code)
                }
            }*/
            val jsInjectedLogin = "javascript:(function() {" +
                    "document.getElementById('$idInputEmail').value='${email?: BuildConfig.DEFAULT_EMAIL}';" +
                    "document.getElementById('$idInputPassword').value='${password?: BuildConfig.DEFAULT_PASSWORD}';" +
                    "document.getElementById('$idInputEmail').dispatchEvent(new Event('input'));" +
                    "document.getElementById('$idInputPassword').dispatchEvent(new Event('input'));" +
                    "document.getElementById('$idSubmitForm').click();"+"})()"
            view?.loadUrl(jsInjectedLogin)
            super.onPageFinished(view, url)
        }
    }
}