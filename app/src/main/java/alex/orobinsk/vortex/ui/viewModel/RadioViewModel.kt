package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.domain.model.ChartTracksResponse
import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.repository.MusicRepository
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.widgets.ActionListener
import alex.orobinsk.vortex.ui.widgets.ToolbarModel
import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.MutableLiveData
import org.kodein.di.generic.instance
import java.util.*

class RadioViewModel : BaseViewModel(), ActionListener<RadioResponse.Data> {
    //val deezerRepository: DeezerRepository by instance()
    val musicRepository: MusicRepository by instance()
    val radioResponse = MutableLiveData<List<RadioResponse.Data>>()
    val trackList = ArrayDeque<String>()
    var toolbarModel: ToolbarModel? = null
    var postActivityTracks: MutableLiveData<Boolean> = MutableLiveData()
    var currentTracklist: MutableLiveData<ChartTracksResponse.Tracks> = MutableLiveData()
    val player = MediaPlayer()

    val onPlayClick = View.OnClickListener {
        /*if(player.isPlaying) {
            player.stop()
            player.reset()
        }

        player.setDataSource(trackList.poll())
        player.setOnPreparedListener {
            player.start()
        }
        player.setOnCompletionListener {
            GlobalScope.launch {
                delay(100) {
                    player.reset()
                    player.setDataSource(trackList.poll())
                    player.prepare()
                }
            }
        }
        player.prepareAsync()*/
        postActivityTracks.postValue(true)
    }

    fun checkIfMusicAvailable(track: String): Boolean {
        return track.isNotEmpty() && track.isNotBlank()
    }

    override fun onClick(data: RadioResponse.Data) {
        var trackList: ChartTracksResponse.Tracks
        musicRepository.getData<ChartTracksResponse> {chartTracksResponse ->
            trackList = chartTracksResponse.tracks
        }
      /*  deezerRepository.getData<TracksResponse>(data.id) {response ->
            response.data.forEach {track ->
                trackList.add(track)
               *//* if(checkIfMusicAvailable(track.link)) {trackList.add(track.preview)}*//*
            }.apply { currentTracklist.postValue(trackList); onPlayClick.onClick(null) }
        }*/
    }

    override fun onCreated() {
      /*  deezerRepository.getData<RadioResponse> { response ->
            radioResponse.postValue(response.data)
        }*/
        /*radioResponse.observeForever {
            if (it.data.isNotEmpty()) {
                deezerRepository.getData<TracksResponse>(it.data[0].id.toString()) {
                    it.data.forEach { trackList.add(it.preview) }
                }
            }
        }*/
    }
}
