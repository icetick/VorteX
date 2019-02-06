package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.App
import androidx.lifecycle.ViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

abstract class BaseViewModel: ViewModel(), KodeinAware {
    override val kodein: Kodein = App.singletonKodein
    var bindedActivity: BaseActivity? = null
}