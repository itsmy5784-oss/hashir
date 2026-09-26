package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationSeconds: Int = 180,
    val streamUrl: String,
    val artworkUrl: String = "",
    val genre: String = "Pop",
    val isLossless: Boolean = true,
    val isExplicit: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val lyrics: String? = null
) {
    fun getFormattedDuration(): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    /**
     * Converts to a map representation for Firebase Firestore storage
     */
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "artist" to artist,
            "album" to album,
            "durationSeconds" to durationSeconds,
            "streamUrl" to streamUrl,
            "artworkUrl" to artworkUrl,
            "genre" to genre,
            "isLossless" to isLossless,
            "isExplicit" to isExplicit
        )
    }

    companion object {
        fun fromFirestoreMap(map: Map<String, Any?>): Track {
            return Track(
                id = map["id"] as? String ?: "",
                title = map["title"] as? String ?: "Unknown Title",
                artist = map["artist"] as? String ?: "Unknown Artist",
                album = map["album"] as? String ?: "",
                durationSeconds = (map["durationSeconds"] as? Number)?.toInt() ?: 180,
                streamUrl = map["streamUrl"] as? String ?: "",
                artworkUrl = map["artworkUrl"] as? String ?: "",
                genre = map["genre"] as? String ?: "Pop",
                isLossless = map["isLossless"] as? Boolean ?: true,
                isExplicit = map["isExplicit"] as? Boolean ?: false
            )
        }
    }
}

data class Playlist(
    val id: String,
    val name: String,
    val description: String = "",
    val trackCount: Int = 0,
    val coverUrl: String = "",
    val tracks: List<Track> = emptyList()
)

data class UserSession(
    val uid: String,
    val email: String,
    val displayName: String,
    val isGuest: Boolean = false,
    val photoUrl: String? = null
)
