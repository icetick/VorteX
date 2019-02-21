package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.service.MusicPlayerService
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.viewModel.MainViewModel
import alex.orobinsk.vortex.util.MediaList
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder

class MainActivity: BaseActivity() {
    private lateinit var servicePlayer: MusicPlayerService
    var isMusicPlayerBound: Boolean = false

    private var serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.LocalBinder
            servicePlayer = binder.service
            isMusicPlayerBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isMusicPlayerBound = false
        }

        override fun onBindingDied(name: ComponentName?) {
            isMusicPlayerBound = false
            super.onBindingDied(name)
        }
    }

    override fun init() {
        binder.bind<MainActivity, MainViewModel>(R.layout.activity_main, this) {
            it.apply {

            }
        }
    }

    fun playAudio(vararg items: String) {
        if(!isMusicPlayerBound) {
            val playerIntent = Intent(this, MusicPlayerService::class.java)
            playerIntent.putExtra(MusicPlayerService.DEFAULT_ITEMSET, items)
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                startForegroundService(playerIntent)
            } else {
                startService(playerIntent)
            }
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            servicePlayer.mediaList = MediaList(*items)
        }
    }


    private fun toggleMenu() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReleaseResources() {
        binder.destroy()
        isMusicPlayerBound = false
    }
}
