package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseViewModel
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//@ObservableVM //Automatically creates observable class for all livedata members
class SplashViewModel : BaseViewModel() {
    private val SPLASH_END_TIME: Long = 6000
    val endSplash = MutableLiveData<Boolean>()

    init {
        endSplash.postValue(false)
        GlobalScope.launch(Dispatchers.IO) {
            delay(SPLASH_END_TIME) {
                endSplash.postValue(true)
            }
        }
    }

    suspend fun delay(time: Long, action: ()->Unit){
        delay(time)
        action()
    }
}