package alex.orobinsk.vortex.domain.api.lastfm

import alex.orobinsk.vortex.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog


class LastFmTrackResolver private constructor(private val context: Context) {
    var webView: WebView? = null

    companion object {
        fun with(context: Context): LastFmTrackResolver {
            return LastFmTrackResolver(context)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    fun getMusic(trackId: String) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle(context.getString(R.string.authorization))
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient =
                LastFmResolverWebClient()
            addJavascriptInterface(this@LastFmTrackResolver, "android")
            loadUrl("https://youtube2mp3api.com/@api/button/mp3/$trackId")
            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                context.startActivity(i)
            }
        }
        alert.setView(webView)
            .setNegativeButton(context.getString(R.string.close)) { dialog, _ -> dialog?.dismiss(); webView?.destroy() }
        alert.show()
    }

    class LastFmResolverWebClient() : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            view.loadUrl(url)
            return true
        }



        override fun onPageFinished(view: WebView?, url: String?) {
            val jsInjectedLogin = "javascript:(function() {" +
                    "while(true) {\n" +
                    " if(document.getElementsByClassName(\"fa fa-download\")[0]==null) {\n" +
                    "  await new Promise(r => setTimeout(r, 1000));\n" +
                    " } else {\n" +
                    "javascript:android.onData(document.getElementsByClassName(\"fa fa-download\")[0].parentElement.parentElement.href)\n" +
                    "break;\n" +
                    " }\n" +
                    "}" + "})()"
            view?.loadUrl(jsInjectedLogin)
            super.onPageFinished(view, url)
        }
    }
}