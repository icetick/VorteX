package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.ui.widgets.ActionListener
import androidx.lifecycle.ViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

abstract class BaseViewModel: ViewModel(), KodeinAware {
    override val kodein: Kodein = App.singletonKodein
    //This method is needed to know when activity is created and bounded
    abstract fun onCreated() 
}