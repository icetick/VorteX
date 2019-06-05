package alex.orobinsk.vortex.modules

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.domain.networking.MusicAPI
import alex.orobinsk.vortex.domain.networking.ServiceGenerator
import alex.orobinsk.vortex.domain.repository.DeezerRepository
import alex.orobinsk.vortex.model.shared.PreferencesStorage
import alex.orobinsk.vortex.player.MediaPlayer
import alex.orobinsk.vortex.player.MusicPlayer
import alex.orobinsk.vortex.ui.viewModel.MainViewModel
import alex.orobinsk.vortex.ui.viewModel.RadioViewModel
import alex.orobinsk.vortex.ui.viewModel.SplashLoginViewModel
import alex.orobinsk.vortex.util.TokenExpireHandler
import alex.orobinsk.vortex.util.ViewModelFactory
import android.app.Activity
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.Job
import org.kodein.di.Kodein
import org.kodein.di.android.AndroidComponentsWeakScope
import org.kodein.di.bindings.WeakContextScope
import org.kodein.di.direct
import org.kodein.di.generic.*

object AppModule {
    val module = Kodein.Module("app") {
       bind<MusicAPI>() with provider { ServiceGenerator.createService(MusicAPI::class.java) }
       bind<PreferencesStorage>() with singleton { PreferencesStorage() }
       bind<DeezerRepository>() with singleton { DeezerRepository(App.singletonKodein, Job()) }
       bind<TokenExpireHandler>() with singleton { TokenExpireHandler(instance()) }
       bind<MediaPlayer>() with singleton { MusicPlayer(instance()) }

       bind<ViewModelProvider.Factory>() with singleton { ViewModelFactory(kodein.direct) }
    }
}