package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.domain.api.deezer.DeezerAuthenticationHelper
import alex.orobinsk.vortex.model.shared.PreferencesStorage
import android.content.Context
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import java.util.*
import java.util.concurrent.TimeUnit

class TokenExpireHandler(prefs: PreferencesStorage) {
    val work = PeriodicWorkRequest.Builder(TokenExpireWorker::class.java, prefs.getExpirationTime(), TimeUnit.SECONDS)
}

class TokenExpireWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    val prefs: PreferencesStorage by App.singletonKodein.instance()

    override fun doWork(): Result = try {
        DeezerAuthenticationHelper.with(context).let { authHelper ->
            authHelper.refreshToken { refreshingCode ->
                GlobalScope.launch(Dispatchers.IO) {
                    authHelper.getTokenResponse(refreshingCode).let { deezerTokenResponse ->
                        prefs.storeToken(deezerTokenResponse.token)
                        prefs.storeExpirationTime(deezerTokenResponse.expirationTime.toLong())
                        prefs.storeLatestTokenUpdate(System.currentTimeMillis())
                        Log.i(
                            this::class.java.simpleName,
                            "Token updated: " + deezerTokenResponse.token + " at time: " + Date(System.currentTimeMillis())
                        )
                    }
                }
            }
        }
        Result.success()
    } catch (ex: Exception) {
        ex.printStackTrace()
        Result.failure()
    }
}