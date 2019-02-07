package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.SplashLoginViewModel
import alex.orobinsk.vortex.util.getImei
import alex.orobinsk.vortex.util.startActivity
import com.tbruyelle.rxpermissions2.RxPermissions


class SplashLoginActivity : BaseActivity() {
    private var androidID: String? = null

    override fun init() {
        requestPermissions()

        binder.bind<SplashLoginActivity, SplashLoginViewModel>(R.layout.activity_splash, this) {
            it.apply {
                androidID.postValue(getImei())
                loginSucceeded.observeForever{
                    startActivity<MainActivity>()
                }
            }
        }
    }

    override fun onReleaseResources() {
        binder.destroy()
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
