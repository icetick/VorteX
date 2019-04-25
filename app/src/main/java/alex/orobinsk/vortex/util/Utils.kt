package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.ChartTracksResponse
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.service.PlayerNotificationModel
import alex.orobinsk.vortex.service.PlayerNotificationModelBuilder

object Utils {
    fun PlayerModelOf(current: ChartTracksResponse.Track): PlayerNotificationModel {
        return PlayerNotificationModelBuilder().appName("Vortex").title(current.name).author(current.artist.name).pauseResumeToggleIcon(
            R.drawable.ic_pause_circle_outline).image(current.image[0].text).build()
    }

}