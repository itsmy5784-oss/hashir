package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.auth.AuthRepository
import com.example.data.repository.DownloadRepository
import com.example.data.repository.MusicRepository
import com.example.playback.MusifyPlayerManager
import com.example.ui.MusifyMainApp
import com.example.ui.theme.MusifySurface
import com.example.ui.theme.MusifyTheme

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var downloadRepository: DownloadRepository
    private lateinit var musicRepository: MusicRepository
    private lateinit var playerManager: MusifyPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authRepository = AuthRepository(applicationContext)
        downloadRepository = DownloadRepository(applicationContext)
        musicRepository = MusicRepository(applicationContext, downloadRepository)
        playerManager = MusifyPlayerManager(applicationContext, downloadRepository)

        setContent {
            MusifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MusifySurface
                ) {
                    MusifyMainApp(
                        authRepository = authRepository,
                        musicRepository = musicRepository,
                        downloadRepository = downloadRepository,
                        playerManager = playerManager
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}
