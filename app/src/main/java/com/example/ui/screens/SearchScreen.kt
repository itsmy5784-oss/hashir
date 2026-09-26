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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
fun SearchScreen(
    onTrackSelect: (Track) -> Unit,
    onPerformSearch: suspend (String) -> List<Track>,
    topCharts: List<Track>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Track>?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            isSearching = true
            searchResults = onPerformSearch(searchQuery)
            isSearching = false
        } else {
            searchResults = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MusifySurface)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 140.dp)
    ) {
        // Title & Value Prop Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Free Badge Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MusifySecondaryContainer.copy(alpha = 0.3f))
                        .border(1.dp, AppleRed.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = MusifyPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "100% FREE • HI-RES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MusifyTertiaryFixed,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Text(
                text = "Unlimited lossless listening, ad-free forever",
                fontSize = 13.sp,
                color = MusifyOutline
            )
        }

        // Liquid Glass Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF26262A))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MusifyOutline,
                        modifier = Modifier.size(20.dp)
                    )

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Artists, Songs, Lyrics, and More",
                                color = MusifyOutline.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input")
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MusifyOutline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { /* Mic audio search */ },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Search",
                            tint = MusifyOutline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // If active search results exist, show them prominently
        if (searchResults != null) {
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
                    Text(
                        text = "Results for \"$searchQuery\"",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (isSearching) {
                        CircularProgressIndicator(
                            color = AppleRed,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (searchResults!!.isEmpty() && !isSearching) {
                    Text(
                        text = "No tracks found for \"$searchQuery\". Try another keyword.",
                        color = MusifyOutline,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    searchResults!!.forEach { track ->
                        TrackSearchRow(track = track, onSelect = { onTrackSelect(track) })
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        } else {
            // Normal Discovery Content

            // Trending Now Horizontal Tag Ribbon
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRENDING NOW",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MusifyOutline,
                        letterSpacing = 0.8.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MusifyOutline,
                        modifier = Modifier.size(18.dp)
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val trending = listOf(
                        "#1" to "Billie Eilish",
                        "#2" to "Taylor Swift",
                        "#3" to "Travis Scott",
                        "#4" to "Fred again..",
                        "#5" to "The Weeknd"
                    )
                    items(trending) { (rank, name) ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MusifySurfaceContainerLow)
                                .clickable { searchQuery = name }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = rank,
                                color = if (rank == "#1") AppleRed else MusifyOutline,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Explore Genres Bento Grid
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
                    Text(
                        text = "Explore Genres",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "See All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppleRed,
                        modifier = Modifier.clickable { searchQuery = "Hits" }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Full-width Dolby Atmos Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(MusifySecondaryContainer, MusifyPrimaryContainer, Color(0xFF353437))
                            )
                        )
                        .clickable { searchQuery = "Spatial Audio" }
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SPATIAL AUDIO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.SurroundSound,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Column {
                            Text(
                                text = "Dolby Atmos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Pure three-dimensional soundscape",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2-Col Bento Grid
                val genres = listOf(
                    GenreCardItem("Hip-Hop &\nRap", "LOSSLESS", Icons.Default.GraphicEq, listOf(MusifySecondaryContainer.copy(0.9f), Color(0xFF2A2A2C))),
                    GenreCardItem("Electronic\n& Dance", "CLUB MIX", Icons.Default.Nightlife, listOf(Color(0xFF2B1055), Color(0xFF2A2A2C))),
                    GenreCardItem("Pop\nHits", "GLOBAL #1", Icons.Default.Star, listOf(MusifyPrimaryContainer.copy(0.8f), Color(0xFF5A002B))),
                    GenreCardItem("Rock &\nAlternative", "LIVE FEED", Icons.Default.Album, listOf(Color(0xFF1A3A30), Color(0xFF2A2A2C))),
                    GenreCardItem("R&B &\nSoul", "VIBES", Icons.Default.Favorite, listOf(Color(0xFF532616), Color(0xFF2A2A2C))),
                    GenreCardItem("Chill &\nLo-Fi Study", "BEATS", Icons.Default.Bedtime, listOf(Color(0xFF2B2B48), Color(0xFF2A2A2C)))
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    genres.chunked(2).forEach { rowPair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowPair.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(106.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Brush.linearGradient(item.colors))
                                        .clickable { searchQuery = item.title.replace("\n", " ") }
                                        .padding(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = item.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                lineHeight = 18.sp
                                            )
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.75f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.White.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = item.badge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Mood & Activity Horizontal Card Deck
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mood & Activity",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MusifyOutline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val moods = listOf(
                        Triple("HIGH BPM", "Workout Energy", "https://lh3.googleusercontent.com/aida-public/AB6AXuA-b2rNXrvRghc_pYfYmpkoZjBWRyeZY92CSi8Dvvn2l-8RxTM7lvQMIRigFOm9zE0fAi1bmGjnLhObxcVVYhULqW_RyQq4dH2a7fZfCrJorHSGJU3vWuHsg1DnKVL10rSkqjgCsgybdysf8V2zHpPd7rpRjAChuT1EO4BFoOBhGtR8Ul2wGvzK5DYeFP0BzxORxQey3HKnxmjMxewipjb8Ddq61Pg776SQMDijBwPYoAk2JWSjr0m-"),
                        Triple("DEEP WORK", "Focus Flow", "https://lh3.googleusercontent.com/aida-public/AB6AXuAis-W0-izMU4_BGlrSgCtbnzcUrQ-fy3TjnP0B2OboF29V5Btxm3llmGAvlO_yIglFQCDx_OcMrb1_ahO--TcbZXs2Fpbb_aXTwnBED5I8Ewpmu1aljBi7Px4VQ-cIVOYDnclo_u_uOYCVsaxif6jOXL4eHBm_-4882DdTe57_wAX6drF-I6s-_My1zoZgdU0S4I7RGYViCI4qgMHpxHurbC1wyv2ve_Z5bLHdFBSjOHb0UeUCYUOc"),
                        Triple("MIDNIGHT", "Late Night Drive", "https://lh3.googleusercontent.com/aida-public/AB6AXuB8NKR-oWWAGfH64u8LlKIpo1mLjc72KUaxeanzOe_ZUy_Or5wuZ9ekIrwJJxe3mtIW3IYaftPoaSbAwG-W-y12pCpnY4PSJ5gLCUhw2Do3TqYJyL7k3d45Ec27uYON4aysXOst-szfuybWGXcETD9mbR3V2t6uU4GRpZv1nhKe-GCLnV99ZjmxHqH_s-3ah9gYT6Nv42g3Ig35t1Xa_MnzBDqbEoENfR4xtuVHzOVWOQjpoLwsY1JK"),
                        Triple("AMBIENT", "Sleep & Meditate", "https://lh3.googleusercontent.com/aida-public/AB6AXuAUNbZIkBA2sk4yFeC7DWLetdhu6F0CUGr70d_xwhKM_r_qBIWDKlDLDX7rBUa3wVepbgDw56bFHBgBpT6JR1zlesB6qfKNgZ9HgYjeVSTAAFNaR002HR3KCHNyz_vb4z4wb_FSQidsckaGd71NA9qFY4M_BdgTBxWcLVos8Gayjegahm1hLunAVE2AleqPx9hTlWpsu1g7mo8k1XLI3W9cK9BgpgqsP7ymkkfYpy2xg9GIY3UJushE")
                    )
                    items(moods) { (tag, title, imgUrl) ->
                        Box(
                            modifier = Modifier
                                .width(136.dp)
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MusifySurfaceContainer)
                                .clickable { searchQuery = title }
                        ) {
                            AsyncImage(
                                model = imgUrl,
                                contentDescription = title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xCC0E0E10), Color(0xFF0E0E10))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleRed,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Top Charts Global Preview List
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
                    Column {
                        Text(
                            text = "Top Charts",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Most played on Musify worldwide",
                            fontSize = 12.sp,
                            color = MusifyOutline
                        )
                    }
                    IconButton(
                        onClick = { searchQuery = "Charts" },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MusifySurfaceContainer)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = AppleRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MusifySurfaceContainerLow.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        topCharts.take(3).forEachIndexed { index, track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (index == 0) AppleRed else Color.Gray,
                                        modifier = Modifier.width(18.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MusifySurfaceContainerHighest)
                                    ) {
                                        AsyncImage(
                                            model = track.artworkUrl,
                                            contentDescription = track.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = track.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (track.isExplicit) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(3.dp))
                                                        .background(Color.White.copy(alpha = 0.15f))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(text = "E", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${track.artist} · ${track.album}",
                                            fontSize = 12.sp,
                                            color = MusifyOutline,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onTrackSelect(track) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            if (index < 2) {
                                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackSearchRow(track: Track, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 10.dp),
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
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MusifySurfaceContainerHighest)
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
                    color = MusifyOutline,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(onClick = onSelect) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = AppleRed
            )
        }
    }
}

private data class GenreCardItem(
    val title: String,
    val badge: String,
    val icon: ImageVector,
    val colors: List<Color>
)
