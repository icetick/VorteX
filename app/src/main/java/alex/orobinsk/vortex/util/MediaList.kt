package alex.orobinsk.vortex.util

import alex.orobinsk.vortex.domain.model.TracksResponse
import android.os.Parcelable
import java.lang.Exception

// 10 - initial size
class MediaList<T>(vararg items: T) : Collection<T> {
    companion object {
        inline fun <reified T> of(items: List<T>): MediaList<T> {
            return MediaList(*items.toTypedArray())
        }
    }

    override var size: Int = items.size
    private var currentPointer = -1

    var items = items
        get() {
            if (currentPointer == -1)
                currentPointer = 0
            return field
        }
        set(value) {
            if (currentPointer == -1)
                currentPointer = 0
            field = value
        }

    fun current(): T? {
        return try {
            if (currentPointer == -1) {
                items[0]
            }
            items[currentPointer]
        } catch (ex: Exception) {
            null
        }
    }

    fun next(): T? {
        currentPointer++
        if (currentPointer == items.size) {
            currentPointer = 0
        }
        return items[currentPointer]
    }

    fun previous(): T? {
        currentPointer--
        return if (currentPointer < 0) {
            null
        } else {
            items[currentPointer]
        }
    }

    fun currentPosition(): Int {
        return currentPointer
    }

    override fun contains(element: T): Boolean {
        return items.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val iterator = elements.iterator()
        while (iterator.hasNext())
            if (!contains(iterator.next()))
                return false
        return true
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return items.iterator()
    }
}

fun MediaList<TracksResponse.Data>.firstAvailable(): TracksResponse.Data? {
    for (item in this.items) {
        if (item.preview.isBlank()) {
            continue
        } else {
            return item
        }
    }
    return null
}