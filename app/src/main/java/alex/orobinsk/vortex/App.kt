package alex.orobinsk.vortex

import alex.orobinsk.vortex.modules.AppModule
import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.deezer.sdk.network.connect.DeezerConnect
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class App: Application(), KodeinAware {
    companion object {
        lateinit var singletonKodein: Kodein
    }
    override val kodein = Kodein {
        import(AppModule.module)
        bind<App>() with singleton { this@App }
        bind<DeezerConnect>() with singleton { DeezerConnect(this@App, BuildConfig.DEEZER_APPLICATION_ID) }
        bind<SharedPreferences>() with singleton { PreferenceManager.getDefaultSharedPreferences(this@App) }
    }

    override fun onCreate() {
        super.onCreate()
        singletonKodein = kodein
    }
}