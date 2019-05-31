package alex.orobinsk.vortex.ui.base

import alex.orobinsk.vortex.BR
import alex.orobinsk.vortex.R
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel>: AppCompatActivity(), BaseFragment.Callback {
    abstract fun init()
    abstract fun onReleaseResources()

    abstract fun getLayoutID(): Int
    abstract val viewModel: V

    var binding: T? = null

    open fun requestPermissions() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        performDataBinding()
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutID())
        binding?.setVariable(BR.viewModel, viewModel)
        viewModel.onCreated()
        binding?.executePendingBindings()
    }

    override fun onBackPressed() {
        showExitAlert()
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

    override fun onDestroy() {
        super.onDestroy()
        onReleaseResources()
        this.overridePendingTransition(0, R.anim.vortex_animation)
    }

    override fun onStop() {
        super.onStop()
        onReleaseResources()
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String?) {

    }
}