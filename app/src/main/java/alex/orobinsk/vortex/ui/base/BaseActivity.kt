package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.BR
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.util.Logger
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomappbar.BottomAppBar
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.generic.instance

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel>: AppCompatActivity(),KodeinAware, BaseFragment.Callback {
    override val kodein: Kodein by closestKodein()

    abstract fun init()
    abstract fun onReleaseResources()

    abstract fun getLayoutID(): Int
    abstract val viewModel: V

    var binding: T? = null

    open fun requestPermissions() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
        performDataBinding()
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    private fun getAppBar(): BottomAppBar? = binding?.root?.findViewById(R.id.bottom_app_bar)

    fun getStatusBarHeight(): Int {
        var statusBarHeight = 0

        try {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }
        } catch (ex: Exception) {
            Logger.log(ex)
        }

        return statusBarHeight
    }

    fun getActionBarHeight(): Int {
        var actionBarHeight = 0
        try {
            val styledAttributes = theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.actionBarSize)
            )
            actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
            styledAttributes.recycle()
        } catch (ex: Exception) {
            Logger.log(ex)
        }
        return actionBarHeight
    }

    fun getNavigationBarHeight(): Int {
        var navigationBarHeight = 0
        try {
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                navigationBarHeight = resources.getDimensionPixelSize(resourceId)
            }
        } catch (ex: Exception) {
            Logger.log(ex)
        }
        if (getAppBar() != null) {
            return 0
        }
        return navigationBarHeight
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutID())
        binding?.setVariable(BR.viewModel, viewModel)
        viewModel.onCreated()
        binding?.executePendingBindings()
    }

    override fun onDestroy() {
        super.onDestroy()
        onReleaseResources()
        this.overridePendingTransition(0, R.anim.vortex_animation)
    }

    override fun onStop() {
        super.onStop()
        onReleaseResources()
    }

    override fun onBackPressed() {
        showExitAlert()
    }

    inline fun <reified VM : ViewModel, T> T.viewModel(): Lazy<VM> where T : KodeinAware, T : FragmentActivity {
        return lazy { ViewModelProviders.of(this, direct.instance()).get(VM::class.java) }
    }

    private fun showExitAlert() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_message)
            .setPositiveButton(R.string.exit_yes) { dialogInterface, clickedButton ->
                this.finish()
            }
            .setNegativeButton(R.string.exit_no) { dialogInterface, clickedButton ->
                dialogInterface.dismiss()
            }.show()
    }



    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String?) {

    }
}