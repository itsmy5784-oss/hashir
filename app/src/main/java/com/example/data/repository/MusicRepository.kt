package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.api.ApiConstants
import com.example.data.api.AudiusTrackDto
import com.example.data.api.RetrofitClient
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MusicRepository(
    private val context: Context,
    private val downloadRepository: DownloadRepository
) {
    private val prefs = context.getSharedPreferences("musify_library_prefs", Context.MODE_PRIVATE)

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (t: Throwable) {
        null
    }

    private val _likedTrackIds = MutableStateFlow<Set<String>>(loadLocalLikedIds())
    val likedTrackIds: StateFlow<Set<String>> = _likedTrackIds.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(getInitialPlaylists())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    val featuredTracks: List<Track>
        get() = CURATED_TRACKS

    suspend fun getTrendingTracks(): List<Track> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.audiusApi.getTrendingTracks()
            if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
                val apiTracks = response.body()!!.data!!.mapNotNull { it.toTrack() }
                if (apiTracks.isNotEmpty()) {
                    return@withContext apiTracks
                }
            }
        } catch (e: Exception) {
            Log.w("MusicRepository", "Audius API error: ${e.message}, using curated hits")
        }
        CURATED_TRACKS
    }

    suspend fun searchTracks(query: String): List<Track> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response = RetrofitClient.audiusApi.searchTracks(query = query)
            if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
                val apiTracks = response.body()!!.data!!.mapNotNull { it.toTrack() }
                if (apiTracks.isNotEmpty()) {
                    return@withContext apiTracks
                }
            }
        } catch (e: Exception) {
            Log.w("MusicRepository", "Search API error: ${e.message}, filtering local database")
        }
        CURATED_TRACKS.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true) ||
            it.genre.contains(query, ignoreCase = true)
        }
    }

    suspend fun toggleLike(userId: String?, track: Track): Boolean = withContext(Dispatchers.IO) {
        val currentLikes = _likedTrackIds.value.toMutableSet()
        val isNowLiked = if (currentLikes.contains(track.id)) {
            currentLikes.remove(track.id)
            false
        } else {
            currentLikes.add(track.id)
            true
        }
        _likedTrackIds.value = currentLikes
        saveLocalLikedIds(currentLikes)

        if (userId != null && firestore != null) {
            try {
                val userDocRef = firestore.collection("users")
                    .document(userId)
                    .collection("library")
                    .document("likes")

                userDocRef.set(
                    mapOf(
                        "likedTrackIds" to currentLikes.toList(),
                        "lastUpdated" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                Log.w("MusicRepository", "Firestore sync skipped: ${e.message}")
            }
        }

        isNowLiked
    }

    suspend fun syncLibraryWithFirestore(userId: String) = withContext(Dispatchers.IO) {
        if (firestore == null) return@withContext
        try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("library")
                .document("likes")
                .get()
                .await()

            if (doc.exists()) {
                val list = doc.get("likedTrackIds") as? List<*>
                if (list != null) {
                    val firestoreSet = list.filterIsInstance<String>().toSet()
                    val merged = _likedTrackIds.value + firestoreSet
                    _likedTrackIds.value = merged
                    saveLocalLikedIds(merged)
                }
            }
        } catch (e: Exception) {
            Log.w("MusicRepository", "Sync library error: ${e.message}")
        }
    }

    fun getAllDownloadedTracks(): List<Track> {
        val downloadedIds = downloadRepository.downloadedTrackIds.value
        return CURATED_TRACKS.filter { downloadedIds.contains(it.id) }.map {
            it.copy(
                isDownloaded = true,
                localFilePath = downloadRepository.getLocalFilePath(it.id)
            )
        }
    }

    fun getLikedTracks(): List<Track> {
        val liked = _likedTrackIds.value
        return CURATED_TRACKS.filter { liked.contains(it.id) }
    }

    private fun loadLocalLikedIds(): Set<String> {
        return prefs.getStringSet("liked_ids", setOf("curated_1", "curated_3")) ?: setOf("curated_1", "curated_3")
    }

    private fun saveLocalLikedIds(ids: Set<String>) {
        prefs.edit().putStringSet("liked_ids", ids).apply()
    }

    private fun getInitialPlaylists(): List<Playlist> {
        return listOf(
            Playlist("p1", "Favorites", "Your top loved tracks", 128, CURATED_TRACKS[0].artworkUrl, CURATED_TRACKS.take(4)),
            Playlist("p2", "Workout Energy", "High BPM power tracks", 42, CURATED_TRACKS[4].artworkUrl, CURATED_TRACKS),
            Playlist("p3", "Road Trip", "Cruising basslines & anthems", 67, CURATED_TRACKS[2].artworkUrl, CURATED_TRACKS),
            Playlist("p4", "Deep Focus", "Ambient lo-fi for productivity", 89, CURATED_TRACKS[6].artworkUrl, CURATED_TRACKS)
        )
    }

    private fun AudiusTrackDto.toTrack(): Track? {
        if (title.isBlank()) return null
        val art = artwork?.large ?: artwork?.medium ?: artwork?.small ?: "https://picsum.photos/400"
        val stream = "https://discoveryprovider.audius.co/v1/tracks/$id/stream?app_name=${ApiConstants.APP_NAME}"
        return Track(
            id = id,
            title = title,
            artist = user?.name ?: "Musify Artist",
            album = "Single",
            durationSeconds = duration ?: 180,
            streamUrl = stream,
            artworkUrl = art,
            genre = genre ?: "Pop",
            isLossless = true,
            isExplicit = false
        )
    }

    companion object {
        val CURATED_TRACKS: List<Track> = listOf(
            Track(
                id = "curated_1",
                title = "TRASHMAN.",
                artist = "Kesha",
                album = "Single",
                durationSeconds = 192,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDW8IHNR2lKFYKtbZAMiBLH1i3QyQmKs7QHe78-vtGTQx-u4WrdqLfjfFlTxmjouRLgsSvxXUvCWkUauJydJOBt0EBCrY2h-wCrr0RHGIIm6NCcl_LoLixjuSp5DVXiqWHSdtzLRXOmoMwMuTAKMOVIQ96h1g5grsCHWfNDxuPctFOLYlZrzP2-ov_UPTlgu_q4EQmSPSt8RTyQT-2vHmMOPpgFd4YnWkkSp_kIn49YZ6GPPQJVUjyH",
                genre = "Pop",
                isLossless = true,
                isExplicit = true
            ),
            Track(
                id = "curated_2",
                title = "BIRDS OF A FEATHER",
                artist = "Billie Eilish",
                album = "HIT ME HARD AND SOFT",
                durationSeconds = 196,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAu0XLlfotSk0TeU_61J0gfxsaZrcX4zCiuCixjMtcCwVnwHTV-Gt0rH2W7t1QaTTwZZy39BcahM5GzrI8xEhAnAaXPn461gTfR0KF0F_LW9b4X4ByrzbKxpw0Jnd_PG0OKFTSFVgGl4_X6CeUBxRRN1vaM5ExCMlHirbu2P1kv9LodDgnKByHV3-CXAVHMcCn7YGF3__Lb_EWVZ4mVzV_rlxHwLFxznZ5lIu48xA2JJoCsk_-eA7F0",
                genre = "Alternative",
                isLossless = true,
                isExplicit = true
            ),
            Track(
                id = "curated_3",
                title = "Timeless",
                artist = "The Weeknd & Playboi Carti",
                album = "Hurry Up Tomorrow",
                durationSeconds = 256,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD8qq3zwyTj3BT2-kKXy4tF0TpUyPdVRUFTa8w-8_8WdxE0p6d8D2Lyyfo284CogIMeT91VW7smMDK3NmE8ZGyF1ddTAX3aL-w2C8vCjjyYNPBsXAT3lDZq53M75Yqwvq175QHiFPMNYyUWSAGakiTRNyAHWmj_MCRIJYcJslWn2dRnR8R6Yr1HoxPKFzQ9cVCUZf3dO7WNBDtBcCszE1Zhwc3uviphMlvttMfZ7JMuCGHXWjx9QJ2-",
                genre = "R&B / Hip-Hop",
                isLossless = true,
                isExplicit = false
            ),
            Track(
                id = "curated_4",
                title = "Espresso",
                artist = "Sabrina Carpenter",
                album = "Short n' Sweet",
                durationSeconds = 175,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDeDbCMcgGByFkxQNM1ajsW594GGMXZVQiZzb5bNqxghRo9V12u_28DioRxWDt8XKENSrWceHoX4QY4wuFgBedyXFRs5-OK_n3-vnQsLJYIEzNauCgMO_soxtv3lw-fWv3Bk-m_7B5KUcU4PpXo4H-cIW1uRTZU5Nd8Ct62OUx0frqA6XnU9lk9By5qlIQOuuFqijhzjpc_XDkAT_akQugHcVPWNExzKfaqdqYsQSJjp3lwVrgOUl0z",
                genre = "Pop",
                isLossless = true,
                isExplicit = false
            ),
            Track(
                id = "curated_5",
                title = "Tears Don't Fall",
                artist = "Bullet for My Valentine",
                album = "The Poison",
                durationSeconds = 348,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDOwr2mv70E5rAUKZxdeWdhxhUb-MwdwBm-ud2xsb8INfYR3r72SplJZfc3xNsp_vsBxt-6Xt_j3ifPP_9VIOL_5zxUojzlN7f1GTRv43bhzFXoMTK-lCJWzywbAy0xQokAA1uDoVynLD4pas1KR_j2vWHfg31ayaneNwJlVMOqPqp_vOOXly6P4MENeN50Pfplay_hRg2H17OYRSDXZUXa6qMu28xt6wM-q6H19TntF6ZGEXEflwUb",
                genre = "Rock / Metal",
                isLossless = true,
                isExplicit = false
            ),
            Track(
                id = "curated_6",
                title = "After Hours (Deluxe)",
                artist = "The Weeknd",
                album = "After Hours",
                durationSeconds = 361,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDNzZpYI_jGxDl7KqliNZ-FESgIyKoBDAazYqAQHIU8BHnvrzPvYJaRRqtmRhoK96HuEJWpNkuFbaRDLFgNIi2IUJu8qMt8dE2w4Ng3n80N2nYjhJ8CVEyyrxH8QhqK0NJ-GWc8R5P6X2LX66ESOGfxzyMiHlZBhW2IoEZTAyipy5s9RwDS8ZUvhlmeTrzEsiZHwCzBmXmXcv_9ru2bJDPgFx4CNl0QVmNmjAQlggAPiSY1b9AYtCkS",
                genre = "R&B",
                isLossless = true,
                isExplicit = true
            ),
            Track(
                id = "curated_7",
                title = "Chill Lo-Fi Midnight",
                artist = "Musify Editorial",
                album = "Midnight Sessions",
                durationSeconds = 188,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCIcnpdYQR9VY8ZaFRxiq7DCBeAQs2vG3qj3xSSt2Y9ut1L2PtiyCZ5PFKzVttv9leoeJ3-RQQ2Oo3ILuZjepQnSH_HsDS7QyOXohtyx7rb9_eOBKGBc2ZhrU3b-ZOl6th1O6C4khNQ2G48hmQHVndDgqSxd6XncsWQHb2o4hxlhaLvVxgtO9qoNwaxHUeg6pfe_vvgdDy6ISBQDgzEHQQZ8ZUg4w2x2qJPqXuCa72lwsPAXeH-7-ht",
                genre = "Lo-Fi Beats",
                isLossless = true,
                isExplicit = false
            )
        )
    }
}
