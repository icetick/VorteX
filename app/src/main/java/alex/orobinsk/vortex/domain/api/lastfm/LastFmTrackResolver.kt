package alex.orobinsk.vortex.domain.api.lastfm

import alex.orobinsk.vortex.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.webkit.JavascriptInterface
import android.content.DialogInterface
import android.app.Dialog


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
        var valueCallback: ValueCallback<String>? = null
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            view.loadUrl(url)
            return true
        }



        override fun onPageFinished(view: WebView?, url: String?) {
           /* val jsInjectedLogin = "javascript:(function() {" +
                    "while(true) {\n" +
                    " if(document.getElementsByClassName(\"fa fa-download\")[0]==null) {\n" +
                    "  await new Promise(r => setTimeout(r, 1000));\n" +
                    " } else {\n" +
                    "javascript:Android.androidAlert(document.getElementsByClassName(\"fa fa-download\")[0].parentElement.parentElement.href)\n" +
                    "break;\n" +
                    " }\n" +
                    "}" + "})()"*/
            val jsInjectedLogin = "javascript: ( function() { " +
                    "if(document.getElementsByClassName('fa fa-download')[0]==null)" +
                     "{ return 0 } else { return document.getElementsByClassName(\"fa fa-download\")[0].parentElement.parentElement.href) }"
                    " }) ()"
            val valueResolver = ValueCallback<String> { value ->
                if (value == "null") {
                    evaluateMp3Resolver(view, jsInjectedLogin, valueCallback)
                } else {
                    Toast.makeText(view?.context, value, Toast.LENGTH_SHORT).show()
                }
            }
            valueCallback = valueResolver
            view?.evaluateJavascript(jsInjectedLogin, valueResolver)

            super.onPageFinished(view, url)
        }

        fun evaluateMp3Resolver(view: WebView?, script: String?, callback: ValueCallback<String>?) {
            view?.handler?.postDelayed ({
                view.evaluateJavascript(script, callback)
            }, 500)
        }
    }
}