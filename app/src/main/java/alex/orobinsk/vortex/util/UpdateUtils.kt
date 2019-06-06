package alex.orobinsk.vortex.util

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UpdateUtils {
    fun installApk(context: Context, apk: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "alex.orobinsk.vortex.provider",
            apk
        )
        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    fun selfUpdate(context: Context, progressLiveData: MutableLiveData<Int>) {
        val storage = FirebaseStorage.getInstance()
            .getReference("app-debug.apk")
        val localApk = File.createTempFile("app-debug", "apk")

        storeTempFilesPath(context, localApk)

        storage.getFile(localApk).addOnSuccessListener {
            installApk(context, localApk)
        }.addOnFailureListener {
            it.printStackTrace()
        }.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
            //displaying percentage in progress dialog
            progressLiveData.postValue(progress.toInt())
        }
    }

    fun storeTempFilesPath(context: Context, apk: File) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.
            edit().putString("tempUpdateFile", apk.absolutePath).apply()
    }

    fun clearUpdateData(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val tempFile = preferences.getString("tempUpdateFile", null)
        tempFile?.let {
            with(File(it)) {
                delete()
                preferences
                    .edit().putString("tempUpdateFile", null).apply()
            }
        }
    }

    fun updateCacheExisting(context: Context) = PreferenceManager
        .getDefaultSharedPreferences(context)
        .getString("tempUpdateFile", null)!=null
}