package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.base.BaseView
import alex.orobinsk.vortex.ui.base.BaseViewModel
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelFactory : ViewModelProvider.Factory {
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