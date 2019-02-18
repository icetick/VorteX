package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.domain.model.RadioResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.domain.repository.DeezerRepository
import alex.orobinsk.vortex.ui.adapter.recycler.DataBindingViewHolder
import alex.orobinsk.vortex.ui.base.BaseViewModel
import alex.orobinsk.vortex.ui.widgets.ActionListener
import android.media.MediaPlayer
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import org.kodein.di.generic.instance
import java.util.*

class RadioViewModel : BaseViewModel(), ActionListener<RadioResponse> {
    val deezerRepository: DeezerRepository by instance()
    val radioResponse = MutableLiveData<RadioResponse>()
    val trackList = ArrayDeque<String>()

    val onPlayClick = View.OnClickListener {
        val player = MediaPlayer()
        player.setDataSource(trackList.poll())
        player.setOnPreparedListener {
            player.start()
        }
        player.setOnCompletionListener { it.setDataSource(trackList.poll()); it.prepareAsync(); }
        player.prepareAsync()
    }

    override fun onClick(data: RadioResponse) {
        super.onClick(data)
    }

    override fun onCreated() {
        deezerRepository.getData<RadioResponse> { response ->
            radioResponse.value = response
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
