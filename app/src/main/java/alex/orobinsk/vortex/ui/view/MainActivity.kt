package alex.orobinsk.vortex.ui.view

import alex.orobinsk.vortex.BR
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.domain.model.TracksResponse
import alex.orobinsk.vortex.service.MusicPlayerService
import alex.orobinsk.vortex.ui.adapter.viewpager.MainScreenAdapter
import alex.orobinsk.vortex.ui.base.BaseActivity
import alex.orobinsk.vortex.ui.base.FragmentFactory
import alex.orobinsk.vortex.ui.viewModel.MainViewModel
import alex.orobinsk.vortex.ui.widgets.ResideLayout
import alex.orobinsk.vortex.util.MediaList
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.ArrayAdapter
import com.flaviofaria.kenburnsview.KenBurnsView
import com.google.gson.Gson

class MainActivity: BaseActivity() {
    private lateinit var servicePlayer: MusicPlayerService
    var isMusicPlayerBound: Boolean = false

    var pagerAdapter: MainScreenAdapter? = null
    var resideAdapter: ArrayAdapter<String>? = null
    var resideListener = object: ResideLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {}
        override fun onPanelOpened(panel: View?) {
            // pagerAdapter?.pauseCurrentFragment()
            panel?.findViewById<KenBurnsView>(R.id.splashView)?.resume()
        }
        override fun onPanelClosed(panel: View?) {
            //pagerAdapter?.resumeCurrentFragment()
            panel?.findViewById<KenBurnsView>(R.id.splashView)?.pause()
        }
    }

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
        binder.bind<MainActivity, MainViewModel>(R.layout.activity_main) {
            it.apply {
                pagerAdapter = MainScreenAdapter(this@MainActivity)
                resideAdapter = ArrayAdapter(this@MainActivity.applicationContext, R.layout.item_reside_menu, arrayOf("Main", "Settings", "Exit"))
                pagerAdapter?.add(FragmentFactory.create<RadioFragment>())
                pagerAdapter?.notifyDataSetChanged()
            }
        }.withVariables {
            it.setVariable(BR.resideListener, resideListener)
            it.setVariable(BR.resideAdapter, resideAdapter)
            it.setVariable(BR.pagerAdapter, pagerAdapter)
        }
    }

    fun playAudio(items: List<TracksResponse.Data>) {
        if(!isMusicPlayerBound) {
            val playerIntent = Intent(this, MusicPlayerService::class.java)
            playerIntent.putExtra(MusicPlayerService.DEFAULT_ITEMSET, Gson().toJson(items))
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
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
        binder.destroy()
        isMusicPlayerBound = false
    }
}
