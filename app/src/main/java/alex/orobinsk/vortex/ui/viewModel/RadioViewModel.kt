package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.domain.api.lastfm.LastFmTrackResolver
import alex.orobinsk.vortex.domain.model.ChartTracksResponse
import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.networking.ServiceGenerator
import alex.orobinsk.vortex.domain.repository.MusicRepository
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.widgets.ActionListener
import alex.orobinsk.vortex.ui.widgets.ToolbarModel
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import org.kodein.di.generic.instance
import java.util.*

class RadioViewModel : BaseViewModel(), ActionListener<ChartTracksResponse.Track> {
    //val deezerRepository: DeezerRepository by instance()
    val musicRepository: MusicRepository by instance()
    val radioResponse = MutableLiveData<List<ChartTracksResponse.Track>>()
    var trackList = ArrayDeque<String>()
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

    override fun onClick(data: ChartTracksResponse.Track) {
        var trackList: ChartTracksResponse.Tracks


        /*  deezerRepository.getData<TracksResponse>(data.id) {response ->
              response.data.forEach {track ->
                  trackList.add(track)
                 *//* if(checkIfMusicAvailable(track.link)) {trackList.add(track.preview)}*//*
            }.apply { currentTracklist.postValue(trackList); onPlayClick.onClick(null) }
        }*/
    }

    override fun onCreated() {
        musicRepository.getData<ChartTracksResponse> { chartTracksResponse ->
            /*chartTracksResponse.tracks.track.forEach {
                musicRepository.getData<ResponseBody>(it.url) {
                }
            }*/
            radioResponse.postValue(chartTracksResponse.tracks.track)
        }
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
