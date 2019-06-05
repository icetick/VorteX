package alex.orobinsk.vortex.util

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.DKodein
import org.kodein.di.generic.instanceOrNull


class ViewModelFactory(private val injector: DKodein) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
        try {
            return injector.instanceOrNull<ViewModel>(tag = modelClass.simpleName) as T? ?: modelClass.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }
}