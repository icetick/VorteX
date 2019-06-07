package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.ui.base.BaseViewModel
import androidx.lifecycle.MutableLiveData

class MediaViewModel: BaseViewModel() {
    val currentMediaName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val currentPlayState: MutableLiveData<PlayerState> by lazy { MutableLiveData<PlayerState>() }

    override fun onCreated() {
        currentMediaName.postValue("Some Music")
    }

}
enum class PlayerState {
    PLAYING, PAUSE, STOPPED
}
