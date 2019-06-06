package alex.orobinsk.vortex.domain.networking

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DeezerAuthInterceptor(private val authToken: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val endUrl = original.url().newBuilder().addQueryParameter("access_token", authToken).build()

        val request = original.newBuilder().url(endUrl).build()
        return chain.proceed(request)
    }
}