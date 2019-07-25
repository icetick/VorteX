package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.ui.base.BaseViewModel
import androidx.lifecycle.MutableLiveData

class MediaViewModel: BaseViewModel() {
    val currentMediaName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val currentPlayState: MutableLiveData<PlayerState> by lazy { MutableLiveData<PlayerState>() }

    override fun onCreated() {
        currentMediaName.postValue("Some Music")
        currentPlayState.postValue(PlayerState.PAUSE)
    }

    fun toggleMediaState() {
        currentPlayState.value?.let {
            when(it) {
                PlayerState.PLAYING -> currentPlayState.postValue(PlayerState.PAUSE)
                PlayerState.PAUSE -> currentPlayState.postValue(PlayerState.PLAYING)
                PlayerState.STOPPED -> currentPlayState.postValue(PlayerState.PLAYING)
            }
        }
    }

}
enum class PlayerState {
    PLAYING, PAUSE, STOPPED
}
