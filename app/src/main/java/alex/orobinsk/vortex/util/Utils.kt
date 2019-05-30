package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.service.PlayerNotificationModel
import alex.orobinsk.vortex.service.PlayerNotificationModelBuilder

object Utils {
    fun playerModelOf(current: TracksResponse.Data): PlayerNotificationModel {
        return PlayerNotificationModelBuilder().appName("Vortex").title(current.title).author(current.artist.name).pauseResumeToggleIcon(
            R.drawable.ic_pause_circle_outline).image(current.album.cover_medium).build()
    }

}