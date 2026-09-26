package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class AudiusTrackResponse(
    @Json(name = "data") val data: List<AudiusTrackDto>?
)

@JsonClass(generateAdapter = true)
data class AudiusTrackDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "duration") val duration: Int?,
    @Json(name = "genre") val genre: String?,
    @Json(name = "artwork") val artwork: AudiusArtworkDto?,
    @Json(name = "user") val user: AudiusUserDto?
)

@JsonClass(generateAdapter = true)
data class AudiusArtworkDto(
    @Json(name = "150x150") val small: String?,
    @Json(name = "480x480") val medium: String?,
    @Json(name = "1000x1000") val large: String?
)

@JsonClass(generateAdapter = true)
data class AudiusUserDto(
    @Json(name = "name") val name: String?,
    @Json(name = "handle") val handle: String?
)

interface AudiusApiService {
    @GET("tracks/trending")
    suspend fun getTrendingTracks(
        @Query("app_name") appName: String = ApiConstants.APP_NAME,
        @Query("limit") limit: Int = 20
    ): Response<AudiusTrackResponse>

    @GET("tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("app_name") appName: String = ApiConstants.APP_NAME,
        @Query("limit") limit: Int = 20
    ): Response<AudiusTrackResponse>
}
