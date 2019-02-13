package alex.orobinsk.vortex.domain.api.deezer

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.api.AuthenticationHelper
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import okhttp3.OkHttpClient
import java.io.*
import java.net.*
import java.lang.Exception


/**
 * This class is used to prevent deezer login to user. In out application we need to use internal login/password field, but deezer requires
 * to use their own interface. In this case we are making workaround to pass login/password to deezer login webpage
 */
class DeezerAuthenticationHelper private constructor(private val context: Context) :
    AuthenticationHelper<DeezerTokenResponse> {
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
    override fun authenticate(email: String, password: String, listener: (String) -> Unit) {
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient =
                DeezerAuthenticationWebClient(
                    listener,
                    email,
                    password
                )
            loadUrl("$BASE_URL?app_id=${BuildConfig.DEEZER_APPLICATION_ID}&redirect_uri=$REDIRECT_URL&perms=$APPLICATION_PERMISSIONS")
        }
    }

    override fun setCookie(connection: URLConnection) {
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

    override fun getTokenResponse(vararg additionalParams: String?): DeezerTokenResponse {
        val urlConnection = URL(
            "$BASE_URL_GET_TOKEN?app_id=" +
                    "${BuildConfig.DEEZER_APPLICATION_ID}&secret=" +
                    "${BuildConfig.SECRET_KEY}&code=${additionalParams.first()}"
        ).openConnection()
        urlConnection.connect()
        val inputStream = DataInputStream(urlConnection.getInputStream())
        var htmlCode = ""
        try {
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            while (true) {
                val line = reader.readLine() ?: break
                htmlCode += line
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return DeezerTokenResponse(htmlCode)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun refreshToken(listener: (String) -> Unit) {
        Handler(context.mainLooper).post {
            webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient =
                        DeezerAuthenticationWebClient(listener)
                loadUrl("$BASE_URL?app_id=${BuildConfig.DEEZER_APPLICATION_ID}&redirect_uri=$REDIRECT_URL&perms=$APPLICATION_PERMISSIONS")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun showAlertAuthentication(listener: (String) -> Unit) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle(context.getString(R.string.authorization))
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient =
                DeezerAuthenticationWebClient(listener)
            loadUrl("$BASE_URL?app_id=${BuildConfig.DEEZER_APPLICATION_ID}&redirect_uri=$REDIRECT_URL&perms=$APPLICATION_PERMISSIONS")
        }
        alert.setView(webView)
            .setNegativeButton(context.getString(R.string.close)) { dialog, _ -> dialog?.dismiss(); webView?.destroy() }
        alert.show()
    }

    fun getDeezerCookie(connection: URLConnection) {
        if (msCookieManager.cookieStore.cookies.size > 0) {
            // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
            connection.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.cookieStore.cookies))
        }
    }

    class DeezerAuthenticationWebClient(
        var loadListener: ((String) -> Unit),
        var email: String? = null,
        var password: String? = null
    ) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            url.let { unwrappedUrl ->
                if (unwrappedUrl.contains("code=")) {
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
                    "if(document.getElementsByName('continue').length!=0) { document.getElementsByName('continue')[0].click(); } else {" +
                    "document.getElementById('login_mail').value='${BuildConfig.DEFAULT_EMAIL}';" +
                    "document.getElementById('login_password').value='${BuildConfig.DEFAULT_PASSWORD}';" +
                    "document.getElementById('login_mail').dispatchEvent(new Event('input'));" +
                    "document.getElementById('login_password').dispatchEvent(new Event('input'));" +
                    "document.getElementById('login_form_submit').click(); }" + "})()"
            view?.loadUrl(jsInjectedLogin)
            super.onPageFinished(view, url)
        }
    }
}