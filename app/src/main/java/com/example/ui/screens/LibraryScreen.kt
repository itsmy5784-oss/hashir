package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Track
import com.example.ui.theme.*

@Composable
fun LibraryScreen(
    libraryTracks: List<Track>,
    downloadedTracks: List<Track>,
    totalStorageMb: Double,
    onTrackSelect: (Track) -> Unit,
    onOpenDownloads: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isGridView by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MusifySurface)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 140.dp)
    ) {
        // Library Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "MUSIFY PURE AUDIO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleRed,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Library",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = { isEditing = !isEditing },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) AppleRed else Color(0xFF26262A),
                    contentColor = if (isEditing) Color.White else AppleRed
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("library_edit_button")
            ) {
                Text(
                    text = if (isEditing) "Done" else "Edit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Primary Library Hub Categories (Grouped Inset List)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xBF1F1F21))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
        ) {
            Column {
                HubCategoryRow(
                    icon = Icons.Default.QueueMusic,
                    title = "Playlists",
                    badge = "24",
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.White.copy(alpha = 0.06f))

                HubCategoryRow(
                    icon = Icons.Default.Mic,
                    title = "Artists",
                    badge = null,
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.White.copy(alpha = 0.06f))

                HubCategoryRow(
                    icon = Icons.Default.Album,
                    title = "Albums",
                    badge = null,
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.White.copy(alpha = 0.06f))

                HubCategoryRow(
                    icon = Icons.Default.DownloadDone,
                    title = "Downloaded",
                    subtitle = "100% Free Offline",
                    badge = "${downloadedTracks.size.coerceAtLeast(438)} Songs",
                    onClick = onOpenDownloads
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.White.copy(alpha = 0.06f))

                HubCategoryRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Made for You",
                    badge = null,
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Smoked Glass Feature Banner: Offline Sync
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xB32A2A2C))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OfflineBolt,
                                contentDescription = null,
                                tint = AppleRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "FREE MUSIFY SYNC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleRed,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Zero Ads. 100% Offline.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${downloadedTracks.size.coerceAtLeast(438)} high-res master tracks cached locally. Listen anywhere without cellular data.",
                            fontSize = 12.sp,
                            color = MusifyOnSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MusifyPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = MusifyOnPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Storage progress meter
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF353437))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = 0.6f)
                            .clip(CircleShape)
                            .background(AppleRed)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Storage: 6.4 GB Hi-Res",
                        fontSize = 11.sp,
                        color = MusifyOnSurfaceVariant
                    )
                    Text(
                        text = "Unlimited Cache",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppleRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pinned Mixes Horizontal Strip
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pinned Mixes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleRed,
                    modifier = Modifier.clickable { }
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val pins = listOf(
                    PinItem("Favorites", "128 tracks", Icons.Default.Favorite, "https://lh3.googleusercontent.com/aida-public/AB6AXuDW8IHNR2lKFYKtbZAMiBLH1i3QyQmKs7QHe78-vtGTQx-u4WrdqLfjfFlTxmjouRLgsSvxXUvCWkUauJydJOBt0EBCrY2h-wCrr0RHGIIm6NCcl_LoLixjuSp5DVXiqWHSdtzLRXOmoMwMuTAKMOVIQ96h1g5grsCHWfNDxuPctFOLYlZrzP2-ov_UPTlgu_q4EQmSPSt8RTyQT-2vHmMOPpgFd4YnWkkSp_kIn49YZ6GPPQJVUjyH", true),
                    PinItem("Workout", "42 tracks", Icons.Default.Bolt, "https://lh3.googleusercontent.com/aida-public/AB6AXuDOwr2mv70E5rAUKZxdeWdhxhUb-MwdwBm-ud2xsb8INfYR3r72SplJZfc3xNsp_vsBxt-6Xt_j3ifPP_9VIOL_5zxUojzlN7f1GTRv43bhzFXoMTK-lCJWzywbAy0xQokAA1uDoVynLD4pas1KR_j2vWHfg31ayaneNwJlVMOqPqp_vOOXly6P4MENeN50Pfplay_hRg2H17OYRSDXZUXa6qMu28xt6wM-q6H19TntF6ZGEXEflwUb", false),
                    PinItem("Road Trip", "67 tracks", Icons.Default.DirectionsCar, "https://lh3.googleusercontent.com/aida-public/AB6AXuDjBLMgkc2O_hijF8hbmbzA8m3FBMVJ7uuxWM5JZaBPO8XYAX27kthozxv_gNbhzhZFMwOACFkAln90WN-a5Mk_n829WGeF6Mb8ieoO5dAe4PEZEleSTN1PsPbP4wmBfgUOSmeO6SJXsVSMEws9undyHnLYi_W6lW76qWx9yLNoEL5JVMrIoXQA-ehEhj6DE0zgS3xTh9KALUQLTS8Wg7TygbdXQmAO8tEw5VG4IO_7Q3EWGgjfHECO", false),
                    PinItem("Deep Focus", "89 tracks", Icons.Default.Headphones, "https://lh3.googleusercontent.com/aida-public/AB6AXuCIyKEPrKVcgzYCKVWQTi5qsT-K5GXvxEQhJ9TppYd-bjJ5_0UoetB0ji8Z8kJ86e2QaUJTiABGjslVhYckx012X1ebKLac69165VqGi8u-t5TOdqeOm4uJbULOfT52ndPU_aUE6wH45JEYVCYDL6ictwOCwdKXQnbLq035oCKzdeXIYpq3jdH-Z7IZ-yjkiI4whoVgi5aCjLT1LjUC3_oBdrWn77uusrm7SOSoxx6VUTRUPBi1X9tG", false)
                )

                items(pins) { pin ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(72.dp)
                            .clickable {
                                libraryTracks.firstOrNull()?.let { onTrackSelect(it) }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF26262A)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pin.isHeart) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                listOf(MusifyPrimaryContainer, MusifySecondaryContainer)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            } else {
                                AsyncImage(
                                    model = pin.imgUrl,
                                    contentDescription = pin.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = pin.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pin.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = pin.subtitle,
                            color = MusifyOnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                // Add new pin
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(72.dp)
                            .clickable { }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A2A2E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add pin",
                                tint = Color.Gray,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "New Pin",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Recently Added with Layout Toggle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Recently Added",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MusifyOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Layout Controls
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2A2A2C))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isGridView) Color(0xFF353437) else Color.Transparent)
                            .clickable { isGridView = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Grid View",
                            tint = if (isGridView) AppleRed else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (!isGridView) Color(0xFF353437) else Color.Transparent)
                            .clickable { isGridView = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewAgenda,
                            contentDescription = "List View",
                            tint = if (!isGridView) AppleRed else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isGridView) {
                // 2x2 Squircle Artwork Grid
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    libraryTracks.chunked(2).forEach { rowTracks ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowTracks.forEach { track ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onTrackSelect(track) }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MusifySurfaceContainer)
                                    ) {
                                        AsyncImage(
                                            model = track.artworkUrl,
                                            contentDescription = track.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Badge
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (track.genre.contains("Lo-Fi")) "HI-RES" else "LOSSLESS",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Play hover
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(8.dp)
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(AppleRed),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = track.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = track.artist,
                                        color = MusifyOnSurfaceVariant,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            if (rowTracks.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // List View
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    libraryTracks.forEach { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onTrackSelect(track) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MusifySurfaceContainer)
                                ) {
                                    AsyncImage(
                                        model = track.artworkUrl,
                                        contentDescription = track.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${track.artist} · ${track.genre}",
                                        color = MusifyOnSurfaceVariant,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = AppleRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HubCategoryRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badge: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MusifyPrimaryContainer.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppleRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = AppleRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF353437))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = MusifyOnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private data class PinItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val imgUrl: String,
    val isHeart: Boolean
)
