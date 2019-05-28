package alex.orobinsk.vortex.model.shared

import alex.orobinsk.vortex.App
import android.content.SharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class PreferencesStorage(override val kodein: Kodein = App.singletonKodein): KodeinAware {
    private val preferences: SharedPreferences by instance()

    private val token_key = "AuthToken"
    private val expiration_key = "ExpirationTime"
    private val expiration_update_key = "LatestTokenUpdate"

    fun storeToken(token: String) = preferences.edit().putString(token_key, token).apply()
    fun getToken(): String? = preferences.getString(token_key, null)

    fun storeExpirationTime(time: Long) = preferences.edit().putLong(expiration_key, time).apply()
    fun getExpirationTime(): Long = preferences.getLong(expiration_key, 10)

    fun storeLatestTokenUpdate(time: Long) = preferences.edit().putLong(expiration_update_key, time).apply()
    fun getLatestTokenUpdate() = preferences.getLong(expiration_update_key, 0)

    fun removeToken() = preferences.edit().remove(token_key).apply()

    fun hasUnexpiredToken(): Boolean = preferences.contains(token_key) && preferences.contains(expiration_update_key) && (System.currentTimeMillis() < getLatestTokenUpdate()+getExpirationTime())
    fun alreadyHadToken(): Boolean = preferences.contains(token_key) && preferences.contains(expiration_key)
}
