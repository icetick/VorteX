package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.SplashLoginViewModel
import alex.orobinsk.vortex.util.FirebaseConfig
import alex.orobinsk.vortex.util.UpdateUtils
import alex.orobinsk.vortex.util.getImei
import alex.orobinsk.vortex.util.startActivity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.tbruyelle.rxpermissions2.RxPermissions


class SplashLoginActivity : BaseActivity() {
    private var androidID: String? = null
    private var remoteConfig: FirebaseConfig = FirebaseConfig()
    private var updateUtils: UpdateUtils = UpdateUtils()

    override fun init() {
        requestPermissions()

        binder.bind<SplashLoginActivity, SplashLoginViewModel>(
            R.layout.activity_splash,
            { viewModel ->
                viewModel.apply {
                    androidID.postValue(getImei())
                    loginSucceeded.observeForever {
                        startActivity<MainActivity>()
                    }
                    offlineLoginSucceded.observeForever {
                        //startActivity<ShareActivity>()
                    }
                    splashEnded.observeForever {
                        if(it) {
                            with(this@SplashLoginActivity) {
                               if (updateUtils.updateCacheExisting(this)) {
                                   updateUtils.clearUpdateData(this)
                               }
                            }
                            fetchUpdateInfo(this.updateProgress)
                        }
                    }

                }
            })

    }

    private fun fetchUpdateInfo(progressLiveData: MutableLiveData<Int>) {
        remoteConfig.fetchLatestVersionCode(this) {
            if (it.toInt() > BuildConfig.VERSION_CODE) {
                AlertDialog.Builder(this@SplashLoginActivity)
                    .setMessage("New update is available")
                    .setPositiveButton("Update") { _, which ->
                        updateUtils.selfUpdate(this, progressLiveData)
                    }.setNegativeButton("Cancell", null).show()
            }
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
    }

    override fun requestPermissions() {
        super.requestPermissions()

        //GOOGLE PLAY REQUIRES DEVICE ID -> deprecated in deezer migration
        RxPermissions(this).requestEach(
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it.granted) {
                androidID = getImei()
            } else {
                requestPermissions()
            }
        }.dispose()
    }
}
