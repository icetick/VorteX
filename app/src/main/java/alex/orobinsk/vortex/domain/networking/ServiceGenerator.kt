package alex.orobinsk.vortex.domain.networking

import alex.orobinsk.vortex.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.text.TextUtils
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

class ServiceGenerator {
    companion object {
        private lateinit var retrofit: Retrofit
        private val BASE_URL = BuildConfig.LAST_FM_API_ENDPOINT//BuildConfig.API_ENDPOINT
        private val httpClient = OkHttpClient.Builder()

        private val builder = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())

        fun<S> createService(servicesClass: Class<S>): S {
            return createService(servicesClass, null)
        }

        fun <S> createService(serviceClass: Class<S>, authToken: String?): S {
            authToken?.let {
                if (!TextUtils.isEmpty(authToken)) {
                    val interceptor = DeezerAuthInterceptor(authToken)

                    if (!httpClient.interceptors().contains(interceptor)) {
                        httpClient.addInterceptor(interceptor)

                        builder.client(httpClient.build())
                        retrofit = builder.build()
                    }
                }
            } ?: run {
                retrofit = builder.build()
            }
            return retrofit.create(serviceClass)
        }
    }
}