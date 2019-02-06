package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.ui.base.SingletonHolder
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import org.kodein.di.bindings.Singleton
import java.io.*
import java.lang.Exception
import java.net.*

/**
 * This class is used to prevent deezer login to user. In out application we need to use internal login/password field, but deezer requires
 * to use their own interface. In this case we are making workaround to pass login/password to deezer login webpage
 */
object DeezerAuthenticationHelper {
    val BASE_URL = "https://connect.deezer.com/oauth/auth.php"
    val REDIRECT_URL = "http://www.alexorovortex.com"
    val APPLICATION_PERMISSIONS = "basic_access,email"
    val contentType = "Content-Type"
    val authenticationContentType = "application/x-www-form-urlencoded"
    val idInputEmail = "login_email"
    val idInputPassword = "login_password"
    val COOKIES_HEADER = "Set-Cookie"
    var msCookieManager = CookieManager()
    val idSubmitForm = "login_form_submit"
    val jsProcessor = LoadProcessor()

    @Throws(IOException::class)
    fun authenticate(email: String, password: String, listener: ()-> Unit): String {
        val url = URL("https://connect.deezer.com/oauth/auth.php?app_id=327262&redirect_uri=http://www.alexorovortex.com&perms=basic_access,email")
        val connection = url.openConnection()
        connection.doInput = true
        connection.doOutput = true
        connection.setRequestProperty(contentType, authenticationContentType)
        setDeezerCookie(connection)

        /* val dataOutputStream = DataOutputStream(connection.getOutputStream())
        dataOutputStream.writeBytes(idInputEmail+"="+URLEncoder.encode(email, "UTF-8"))
        dataOutputStream.writeBytes(idInputPassword+"="+URLEncoder.encode(password, "UTF-8"))
        dataOutputStream.flush()
        dataOutputStream.close()*/

        val inputStream = DataInputStream(connection.getInputStream())
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
        Log.e("HTML", htmlCode)
        return htmlCode
        /*webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }*/
    }

    fun setDeezerCookie(connection: URLConnection) {
        val headerFields = connection.headerFields
        val cookiesHeader = headerFields[COOKIES_HEADER]
        if (cookiesHeader != null) {
            for (cookie in cookiesHeader) {
                msCookieManager.cookieStore.add(null, HttpCookie.parse(cookie).get(0))
            }
        }
    }

    fun getDeezerCookie(connection: URLConnection) {
        if (msCookieManager.cookieStore.cookies.size > 0) {
            // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
            connection.setRequestProperty("Cookie", TextUtils.join(";",  msCookieManager.cookieStore.cookies))
        }
    }

    class LoadProcessor {
        public fun processHTML(html: String) {
            Log.e("result", html)
        }
    }
}