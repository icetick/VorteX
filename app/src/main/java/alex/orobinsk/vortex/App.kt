package alex.orobinsk.vortex

import alex.orobinsk.vortex.modules.AppModule
import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
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
        bind<Application>() with singleton { this@App }
        bind<SharedPreferences>() with singleton { PreferenceManager.getDefaultSharedPreferences(this@App) }
    }

    override fun onCreate() {
        super.onCreate()
        singletonKodein = kodein
    }
}