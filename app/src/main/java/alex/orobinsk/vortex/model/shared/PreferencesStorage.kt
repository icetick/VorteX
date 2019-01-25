package alex.orobinsk.vortex.model.shared

import alex.orobinsk.vortex.App
import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class PreferencesStorage(override val kodein: Kodein = App.singletonKodein): KodeinAware {
    private val preferences: SharedPreferences by instance()

    private val token_key = "AuthToken"

    fun storeToken(token: String) {
        preferences.edit().putString(token_key, token).apply()
    }
    fun removeToken() {
        preferences.edit().remove(token_key).apply()
    }
}
