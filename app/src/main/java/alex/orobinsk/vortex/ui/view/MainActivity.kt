package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.databinding.ActivityMainBinding
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.service.MusicPlayerService
import alex.orobinsk.vortex.ui.adapter.viewpager.MainScreenAdapter
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.base.FragmentFactory
import alex.orobinsk.vortex.ui.viewModel.MainViewModel
import alex.orobinsk.vortex.ui.viewModel.MediaViewModel
import alex.orobinsk.vortex.util.MediaList
import alex.orobinsk.vortex.util.ViewModelFactory
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.widget.ArrayAdapter
import com.google.gson.Gson
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), KodeinAware {
    override val kodein: Kodein = App.singletonKodein
    override val viewModel: MainViewModel by viewModel()

    override fun getLayoutID(): Int = R.layout.activity_main

    private lateinit var servicePlayer: MusicPlayerService
    var isMusicPlayerBound: Boolean = false

    private var serviceConnection = object : ServiceConnection {
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
        viewModel.apply {
            pagerAdapter = MainScreenAdapter(this@MainActivity)
            resideAdapter =
                ArrayAdapter(applicationContext, R.layout.item_reside_menu, arrayOf("Main", "Settings", "Exit"))
            pagerAdapter?.add(FragmentFactory.create<RadioFragment>())
            pagerAdapter?.notifyDataSetChanged()
        }
    }

    fun playAudio(items: List<TracksResponse.Data>) {
        if (!isMusicPlayerBound) {
            val playerIntent = Intent(this, MusicPlayerService::class.java)
            playerIntent.putExtra(MusicPlayerService.DEFAULT_ITEMSET, Gson().toJson(items))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(playerIntent)
            } else {
                startService(playerIntent)
            }
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            servicePlayer.mediaList = MediaList.of(items)
        }
    }


    private fun toggleMenu() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReleaseResources() {
        isMusicPlayerBound = false
    }
}
