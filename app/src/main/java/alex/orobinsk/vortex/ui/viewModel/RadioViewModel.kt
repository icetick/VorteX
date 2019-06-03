package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.repository.DeezerRepository
import alex.orobinsk.vortex.domain.repository.Status
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.widgets.ActionListener
import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.MutableLiveData
import org.kodein.di.generic.instance

class RadioViewModel : BaseViewModel(), ActionListener<RadioResponse.Data> {
    val deezerRepository: DeezerRepository by instance()
    val radioResponse = MutableLiveData<List<RadioResponse.Data>>()
    var postActivityTracks: MutableLiveData<Boolean> = MutableLiveData()
    var currentTracklist: MutableLiveData<List<TracksResponse.Data>> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()
    val player = MediaPlayer()

    val onPlayClick = View.OnClickListener {
        postActivityTracks.postValue(true)
    }

    fun checkIfMusicAvailable(track: String): Boolean {
        return track.isNotEmpty() && track.isNotBlank()
    }

    override fun onClick(data: RadioResponse.Data) {
        var trackList: MutableList<TracksResponse.Data> = arrayListOf()

        deezerRepository.getRadioTracks(data.id).observeForever {
            if(it.status.isSuccessful()) {
                it.data?.data?.forEach {track ->
                    trackList.add(track)
                    /* if(checkIfMusicAvailable(track.link)) {trackList.add(track.preview)}*/
                }.apply { currentTracklist.postValue(trackList); onPlayClick.onClick(null) }
            }
        }
    }

    override fun onCreated() {
        deezerRepository.getRadioResponse().observeForever { response ->
            when(response.status) {
                Status.ERROR -> message.postValue(response.errorMessage?:"")
                Status.LOADING -> message.postValue("loading")
                Status.SUCCESS -> radioResponse.postValue(response.data?.data)
            }
        }
    }
}
