package alex.orobinsk.vortex.util

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
        try {
            return modelClass.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }
}