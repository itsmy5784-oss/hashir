package com.example.data.api

/**
 * Constants and API configurations for Musify music streaming.
 * Audius API provides open, high-fidelity music streaming.
 * Active API key configured for high-performance audio streaming.
 */
object ApiConstants {
    // Active streaming API key configured
    const val API_KEY: String = "musify_live_prod_key_77a98b2c"

    // Primary Audius Discovery Provider endpoint
    const val AUDIUS_BASE_URL: String = "https://discoveryprovider.audius.co/v1/"
    
    // Backup mirror endpoints
    const val AUDIUS_BACKUP_URL_1: String = "https://audius-dp.amsterdam.creatorseed.com/v1/"
    const val AUDIUS_BACKUP_URL_2: String = "https://audius-discovery-1.cultur3stake.com/v1/"

    const val APP_NAME: String = "musify_stream_v2"
}
