package alex.orobinsk.vortex.domain.api

import java.net.URLConnection

interface AuthenticationHelper<T> {
    fun authenticate(email: String, password: String, listener: (String) -> Unit)
    fun getTokenResponse(vararg additionalParams: String?): T
    fun refreshToken(listener: (String) -> Unit)
    fun setCookie(connection: URLConnection)
}