package alex.orobinsk.vortex.domain.model

import alex.orobinsk.annotation.ModelBuilder

@ModelBuilder
data class Track(val id: String, val name: String, val timestamp: Long, val album: Album)

@ModelBuilder
data class Album(val id: String, val name: String, val timestamp: Long, val totalCount: Int)