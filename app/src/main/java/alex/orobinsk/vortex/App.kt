package alex.orobinsk.vortex

import alex.orobinsk.vortex.model.shared.PreferencesStorage
import alex.orobinsk.vortex.modules.AppModule
import alex.orobinsk.vortex.util.TokenExpireHandler
import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class App: Application(), KodeinAware {
    private val preferences: PreferencesStorage by instance()
    private val expirationHandler: TokenExpireHandler by instance()
    companion object {
        lateinit var singletonKodein: Kodein
    }
    override val kodein = Kodein {
        import(AppModule.module)
        bind<App>() with singleton { this@App }
        bind<SharedPreferences>() with singleton { PreferenceManager.getDefaultSharedPreferences(this@App) }
    }

    override fun onCreate() {
        super.onCreate()
        singletonKodein = kodein
        enqueueTokenRefresh()
    }

    fun enqueueTokenRefresh() {
        if(preferences.alreadyHadToken()) {
            WorkManager.getInstance().enqueueUniquePeriodicWork(expirationHandler.javaClass.simpleName,
                ExistingPeriodicWorkPolicy.REPLACE, expirationHandler.work.build())
        }
    }
}