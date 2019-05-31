package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.databinding.ActivitySplashBinding
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.SplashLoginViewModel
import alex.orobinsk.vortex.util.ViewModelFactory
import alex.orobinsk.vortex.util.getImei
import alex.orobinsk.vortex.util.startActivity
import androidx.lifecycle.ViewModelProviders
import com.tbruyelle.rxpermissions2.RxPermissions
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance


class SplashLoginActivity : BaseActivity<ActivitySplashBinding, SplashLoginViewModel>(), KodeinAware {
    override val kodein: Kodein = App.singletonKodein
    override val viewModel: SplashLoginViewModel by instance()

    override fun getLayoutID(): Int = R.layout.activity_splash

    private var androidID: String? = null

    override fun init() {
        requestPermissions()
        viewModel.apply {
            androidID.postValue(getImei())
            loginSucceeded.observeForever{
                startActivity<MainActivity>()
            }
        }
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
