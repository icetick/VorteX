package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.repository.DeezerRepository
import alex.orobinsk.vortex.ui.adapter.recycler.DataBindingViewHolder
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.widgets.ActionListener
import alex.orobinsk.vortex.ui.widgets.ToolbarModel
import alex.orobinsk.vortex.ui.widgets.ToolbarModelBuilder
import alex.orobinsk.vortex.util.delay
import alex.orobinsk.vortex.util.toast
import android.media.MediaPlayer
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import java.util.*

class RadioViewModel : BaseViewModel(), ActionListener<RadioResponse.Data> {
    val deezerRepository: DeezerRepository by instance()
    val radioResponse = MutableLiveData<List<RadioResponse.Data>>()
    val trackList = ArrayDeque<String>()
    var toolbarModel: ToolbarModel? = null
    val player = MediaPlayer()

    val onPlayClick = View.OnClickListener {
        if(player.isPlaying) {
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
        player.prepareAsync()
    }

    fun checkIfMusicAvailable(track: String): Boolean {
        return track.isNotEmpty() && track.isNotBlank()
    }

    override fun onClick(data: RadioResponse.Data) {
        deezerRepository.getData<TracksResponse>(data.id) {response ->
            response.data.forEach {track ->
                if(checkIfMusicAvailable(track.preview)) {trackList.add(track.preview)}
            }.apply { onPlayClick.onClick(null) }
        }
    }

    override fun onCreated() {
        deezerRepository.getData<RadioResponse> { response ->
            radioResponse.postValue(response.data)
        }
        /*radioResponse.observeForever {
            if (it.data.isNotEmpty()) {
                deezerRepository.getData<TracksResponse>(it.data[0].id.toString()) {
                    it.data.forEach { trackList.add(it.preview) }
                }
            }
        }*/
    }
}
