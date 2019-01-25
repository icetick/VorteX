package alex.orobinsk.vortex.domain.repository

import alex.orobinsk.vortex.App
import alex.orobinsk.vortex.domain.Repository
import alex.orobinsk.vortex.domain.model.Track
import alex.orobinsk.vortex.domain.networking.MusicAPI
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class MusicRepository : Repository<Track>, KodeinAware {
    override val kodein: Kodein
        get() = App.singletonKodein

    private val api: MusicAPI by instance()
    override suspend fun get(id: String) = api.getTrack(id)
    override fun create(vararg items: Track) = items.forEach { api.addMusicTrack(it) }
    override suspend fun update(vararg items: Track) = items.forEach{ api.updateTrack(it).await() }
    override suspend fun remove(vararg ids: String) = ids.forEach { api.deleteTrack(it) }

    override suspend fun removeAll() = api.deleteAllTracks()
    override suspend fun getAll() = api.getMusicFeed()
}