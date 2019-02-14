package alex.orobinsk.vortex.domain.api.deezer

import java.lang.IndexOutOfBoundsException

class DeezerTokenResponse(private val htmlResponse: String) {
    val token
        get() = try {
            htmlResponse.split("access_token=", "&expires=")[1]
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
            htmlResponse
        }

    val expirationTime
        get() = try {
            htmlResponse.split("&expires=")[1]
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
            htmlResponse
        }
}