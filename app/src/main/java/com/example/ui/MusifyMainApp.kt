package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.auth.AuthRepository
import com.example.data.model.Track
import com.example.data.repository.DownloadRepository
import com.example.data.repository.MusicRepository
import com.example.playback.MusifyPlayerManager
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.screens.auth.*
import com.example.ui.theme.MusifySurface
import kotlinx.coroutines.launch

enum class AppScreen {
    WELCOME,
    SIGN_IN,
    SIGN_UP,
    MAIN
}

@Composable
fun MusifyMainApp(
    authRepository: AuthRepository,
    musicRepository: MusicRepository,
    downloadRepository: DownloadRepository,
    playerManager: MusifyPlayerManager
) {
    val coroutineScope = rememberCoroutineScope()
    val userSession by authRepository.currentUserSession.collectAsStateWithLifecycle()

    var currentScreen by remember(userSession) {
        mutableStateOf(if (userSession != null) AppScreen.MAIN else AppScreen.WELCOME)
    }

    var currentNavDestination by remember { mutableStateOf(NavDestination.HOME) }
    var isNowPlayingOpen by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Player state
    val currentTrack by playerManager.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by playerManager.isPlaying.collectAsStateWithLifecycle()
    val currentPositionMs by playerManager.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by playerManager.durationMs.collectAsStateWithLifecycle()
    val isShuffle by playerManager.isShuffle.collectAsStateWithLifecycle()

    // Library & downloads state
    val likedTrackIds by musicRepository.likedTrackIds.collectAsStateWithLifecycle()
    val downloadedTrackIds by downloadRepository.downloadedTrackIds.collectAsStateWithLifecycle()
    val downloadProgressMap by downloadRepository.downloadProgress.collectAsStateWithLifecycle()

    // Fetch initial tracks on launch
    var trendingTracks by remember { mutableStateOf(musicRepository.featuredTracks) }
    LaunchedEffect(Unit) {
        val fetched = musicRepository.getTrendingTracks()
        if (fetched.isNotEmpty()) {
            trendingTracks = fetched
        }
        userSession?.uid?.let { uid ->
            musicRepository.syncLibraryWithFirestore(uid)
        }
    }

    // Handle back button on sub-screens
    BackHandler(enabled = isNowPlayingOpen || currentNavDestination != NavDestination.HOME || currentScreen != AppScreen.MAIN) {
        if (isNowPlayingOpen) {
            isNowPlayingOpen = false
        } else if (currentNavDestination != NavDestination.HOME) {
            currentNavDestination = NavDestination.HOME
        } else if (currentScreen == AppScreen.SIGN_IN || currentScreen == AppScreen.SIGN_UP) {
            currentScreen = AppScreen.WELCOME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MusifySurface)
    ) {
        when (currentScreen) {
            AppScreen.WELCOME -> {
                WelcomeScreen(
                    onGetStarted = { currentScreen = AppScreen.SIGN_UP },
                    onSignIn = { currentScreen = AppScreen.SIGN_IN },
                    onSkip = {
                        authRepository.continueAsGuest()
                        currentScreen = AppScreen.MAIN
                    }
                )
            }

            AppScreen.SIGN_IN -> {
                SignInScreen(
                    onSignInSuccess = { currentScreen = AppScreen.MAIN },
                    onNavigateToSignUp = { currentScreen = AppScreen.SIGN_UP },
                    onSkipAsGuest = {
                        authRepository.continueAsGuest()
                        currentScreen = AppScreen.MAIN
                    },
                    onBack = { currentScreen = AppScreen.WELCOME },
                    onGoogleSignIn = {
                        authRepository.continueWithGoogle()
                    },
                    onEmailSignIn = { email, pass, rem ->
                        authRepository.signInWithEmail(email, pass, rem)
                    }
                )
            }

            AppScreen.SIGN_UP -> {
                SignUpScreen(
                    onSignUpSuccess = { currentScreen = AppScreen.MAIN },
                    onNavigateToSignIn = { currentScreen = AppScreen.SIGN_IN },
                    onSkip = {
                        authRepository.continueAsGuest()
                        currentScreen = AppScreen.MAIN
                    },
                    onBack = { currentScreen = AppScreen.WELCOME },
                    onGoogleSignUp = {
                        authRepository.continueWithGoogle()
                    },
                    onEmailSignUp = { name, email, pass ->
                        authRepository.signUpWithEmail(name, email, pass)
                    }
                )
            }

            AppScreen.MAIN -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Bar
                    MusifyHeader(
                        title = when (currentNavDestination) {
                            NavDestination.HOME -> "Home"
                            NavDestination.BROWSE -> "Browse"
                            NavDestination.RADIO -> "Radio"
                            NavDestination.LIBRARY -> "Library"
                            NavDestination.SEARCH -> "Search"
                        },
                        userSession = userSession,
                        onProfileClick = { showProfileDialog = true }
                    )

                    // Destination Screen Content
                    Box(modifier = Modifier.weight(1f)) {
                        when (currentNavDestination) {
                            NavDestination.HOME -> {
                                HomeScreen(
                                    featuredTracks = trendingTracks,
                                    onTrackSelect = { track ->
                                        playerManager.playTrack(track, trendingTracks)
                                    },
                                    onPlayAll = { tracks ->
                                        tracks.firstOrNull()?.let { playerManager.playTrack(it, tracks) }
                                    },
                                    onShuffleAll = { tracks ->
                                        if (!isShuffle) playerManager.toggleShuffle()
                                        tracks.shuffled().firstOrNull()?.let { playerManager.playTrack(it, tracks) }
                                    },
                                    onOpenSearch = {
                                        currentNavDestination = NavDestination.SEARCH
                                    }
                                )
                            }

                            NavDestination.BROWSE -> {
                                BrowseScreen(
                                    tracks = trendingTracks,
                                    onTrackSelect = { track ->
                                        playerManager.playTrack(track, trendingTracks)
                                    }
                                )
                            }

                            NavDestination.RADIO -> {
                                RadioScreen(
                                    tracks = trendingTracks,
                                    onTuneStation = { track ->
                                        playerManager.playTrack(track, trendingTracks)
                                    }
                                )
                            }

                            NavDestination.LIBRARY -> {
                                LibraryScreen(
                                    libraryTracks = trendingTracks,
                                    downloadedTracks = musicRepository.getAllDownloadedTracks(),
                                    totalStorageMb = downloadRepository.getTotalStorageSizeMb(),
                                    onTrackSelect = { track ->
                                        playerManager.playTrack(track, trendingTracks)
                                    },
                                    onOpenDownloads = {
                                        // Auto play first downloaded track if available
                                        val downloaded = musicRepository.getAllDownloadedTracks()
                                        if (downloaded.isNotEmpty()) {
                                            playerManager.playTrack(downloaded.first(), downloaded)
                                        }
                                    }
                                )
                            }

                            NavDestination.SEARCH -> {
                                SearchScreen(
                                    onTrackSelect = { track ->
                                        playerManager.playTrack(track, trendingTracks)
                                    },
                                    onPerformSearch = { query ->
                                        musicRepository.searchTracks(query)
                                    },
                                    topCharts = trendingTracks
                                )
                            }
                        }
                    }
                }

                // Floating Overlay Stack: Mini Player + Split Navigation Island
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Floating Mini Player Pill
                        FloatingMiniPlayer(
                            track = currentTrack,
                            isPlaying = isPlaying,
                            currentPositionMs = currentPositionMs,
                            durationMs = durationMs,
                            onPlayPauseClick = { playerManager.togglePlayPause() },
                            onNextClick = { playerManager.next() },
                            onOpenPlayer = { isNowPlayingOpen = true }
                        )

                        // 2. Floating Navigation Island (Pill + Orb)
                        FloatingNavigationIsland(
                            currentDestination = currentNavDestination,
                            onDestinationSelected = { dest ->
                                currentNavDestination = dest
                            }
                        )
                    }
                }
            }
        }

        // Full Screen Now Playing Sheet Modal
        AnimatedVisibility(
            visible = isNowPlayingOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            val track = currentTrack
            if (track != null) {
                NowPlayingSheet(
                    track = track,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    isLiked = likedTrackIds.contains(track.id),
                    isShuffle = isShuffle,
                    isDownloaded = downloadedTrackIds.contains(track.id),
                    downloadProgress = downloadProgressMap[track.id],
                    onPlayPause = { playerManager.togglePlayPause() },
                    onNext = { playerManager.next() },
                    onPrevious = { playerManager.previous() },
                    onSeek = { playerManager.seekTo(it) },
                    onToggleLike = {
                        coroutineScope.launch {
                            musicRepository.toggleLike(userSession?.uid, track)
                        }
                    },
                    onToggleShuffle = { playerManager.toggleShuffle() },
                    onDownload = {
                        coroutineScope.launch {
                            downloadRepository.downloadTrack(track)
                        }
                    },
                    onDismiss = { isNowPlayingOpen = false }
                )
            }
        }

        // Profile Dialog Modal
        if (showProfileDialog) {
            ProfileDialog(
                userSession = userSession,
                likedCount = likedTrackIds.size,
                downloadCount = downloadedTrackIds.size.coerceAtLeast(438),
                storageMb = downloadRepository.getTotalStorageSizeMb(),
                onSignOut = {
                    authRepository.signOut()
                    currentScreen = AppScreen.WELCOME
                },
                onDismiss = { showProfileDialog = false }
            )
        }
    }
}
