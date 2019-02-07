package alex.orobinsk.vortex.modules

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.domain.Repository
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.networking.MusicAPI
import alex.orobinsk.vortex.domain.networking.ServiceGenerator
import alex.orobinsk.vortex.domain.repository.DeezerRepository
import alex.orobinsk.vortex.model.shared.PreferencesStorage
import alex.orobinsk.vortex.ui.viewModel.SplashLoginViewModel
import alex.orobinsk.vortex.util.ViewModelFactory
import kotlinx.coroutines.Job
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

object AppModule {
    val module = Kodein.Module("app") {
       bind<MusicAPI>() with provider { ServiceGenerator.createService(MusicAPI::class.java) }
       bind<PreferencesStorage>() with singleton { PreferencesStorage() }
       bind<SplashLoginViewModel>() with singleton { ViewModelFactory().create(SplashLoginViewModel::class.java) }
       bind<DeezerRepository>() with singleton { DeezerRepository(App.singletonKodein, Job()) }
    }
}