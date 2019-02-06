package alex.orobinsk.vortex.modules

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.domain.Repository
import alex.orobinsk.vortex.domain.model.Track
import alex.orobinsk.vortex.domain.networking.MusicAPI
import alex.orobinsk.vortex.domain.networking.ServiceGenerator
import alex.orobinsk.vortex.domain.repository.MusicRepository
import alex.orobinsk.vortex.model.shared.PreferencesStorage
import alex.orobinsk.vortex.ui.viewModel.SplashViewModel
import alex.orobinsk.vortex.util.ViewModelFactory
import com.deezer.sdk.network.connect.DeezerConnect
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

object AppModule {
    val module = Kodein.Module("app") {
       bind<MusicAPI>() with provider { ServiceGenerator.createService(MusicAPI::class.java) }
       bind<PreferencesStorage>() with singleton { PreferencesStorage() }
       bind<SplashViewModel>() with singleton { ViewModelFactory().create(SplashViewModel::class.java) }
       bind<Repository<Track>>() with singleton { MusicRepository() }
    }
}