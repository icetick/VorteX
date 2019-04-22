package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.R
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class FirebaseConfig {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private var configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
        .setDeveloperModeEnabled(BuildConfig.DEBUG)
        .build()
    private var cacheExpiration: Long = 43200
    private var verCode = ""

    init {
        mFirebaseRemoteConfig.setConfigSettings(configSettings)
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_version_code)
    }

    fun fetchLatestVersionCode(context: Context, callBack: (versionCode: String) -> Unit) {
        mFirebaseRemoteConfig.fetch(getCacheExpiration())
            .addOnCompleteListener(
                context as Activity
            ) { task ->
                // If is successful, activated fetched
                if (task.isSuccessful) {
                    mFirebaseRemoteConfig.activate()
                } else {
                    Log.d("UPDATE", "Update failed ${task.exception?.localizedMessage}")
                }
                verCode = mFirebaseRemoteConfig.getString("latestVersionCode")
                callBack.invoke(verCode)
            }
    }

    private fun getCacheExpiration(): Long {
        // If is developer mode, cache expiration set to 0, in order to test
        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }
        return cacheExpiration
    }
}