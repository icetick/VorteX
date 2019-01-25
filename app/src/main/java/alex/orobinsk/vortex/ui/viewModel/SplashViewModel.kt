package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

//@ObservableVM //Automatically creates observable class for all livedata members
class SplashViewModel: BaseViewModel() {
    private val SPLASH_END_TIME: Long = 30000
    val SPLASH_IMAGE = R.drawable.splash
    val SPLASH_LOGO = R.drawable.logo
    val bounceAnimate = true
    val endSplash = MutableLiveData<Boolean>()

    init {
        endSplash.postValue(true)
        GlobalScope.launch {
            withTimeout(SPLASH_END_TIME) {
                endSplash.postValue(true)
            }
        }
    }
}