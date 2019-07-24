package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.databinding.ActivitySplashBinding
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.SplashLoginViewModel
import alex.orobinsk.vortex.util.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance


class SplashLoginActivity : BaseActivity<ActivitySplashBinding, SplashLoginViewModel>(), KodeinAware {
    private val STABLE_RENDER_DELAY: Long = 1000
    override val kodein: Kodein = App.singletonKodein
    override val viewModel: SplashLoginViewModel by viewModel()

    val remoteConfig: FirebaseConfig by instance()
    val updateUtils: UpdateUtils by instance()

    override fun getLayoutID(): Int = R.layout.activity_splash

    private var androidID: String? = null

    override fun init() {
        requestPermissions()
        viewModel.apply {
            androidID.postValue(getImei())
            loginSucceeded.observeForever {
                startActivity<MainActivity>()
            }
            splashEnded.observeForever {
                GlobalScope.launch(Dispatchers.IO) {
                    if (it) {
                        with(this@SplashLoginActivity) {
                            if (updateUtils.updateCacheExisting(this)) {
                                updateUtils.clearUpdateData(this)
                            }
                        }

                        delay(STABLE_RENDER_DELAY)
                        withContext(Dispatchers.Main) { fetchUpdateInfo(viewModel.updateProgress) }
                    }
                }
            }
        }
    }

    private fun fetchUpdateInfo(progressLiveData: MutableLiveData<Int>) {
       /* remoteConfig.fetchLatestVersionCode(this) {
            if (it.toInt() > BuildConfig.VERSION_CODE) {
                AlertDialog.Builder(this@SplashLoginActivity)
                    .setMessage("New update is available")
                    .setPositiveButton("Update") { _, which ->
                        updateUtils.selfUpdate(this@SplashLoginActivity, progressLiveData)
                    }.setNegativeButton("Cancel", null).show()
            }
        }*/
    }

    override fun onReleaseResources() {
    }

    override fun requestPermissions() {
        super.requestPermissions()

        //GOOGLE PLAY REQUIRES DEVICE ID -> deprecated in deezer migration
        RxPermissions(this).requestEach(android.Manifest.permission.READ_PHONE_STATE).subscribe {
            if (it.granted) {
                androidID = getImei()
            } else {
                requestPermissions()
            }
        }.dispose()
    }
}
