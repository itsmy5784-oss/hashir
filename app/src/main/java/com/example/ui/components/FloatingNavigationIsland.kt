package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class NavDestination(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    BROWSE("Browse", Icons.Default.GridView),
    RADIO("Radio", Icons.Default.Radio),
    LIBRARY("Library", Icons.Default.LibraryMusic),
    SEARCH("Search", Icons.Default.Search)
}

@Composable
fun FloatingNavigationIsland(
    currentDestination: NavDestination,
    onDestinationSelected: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left Frosted Pill with 4 tabs
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(29.dp), ambientColor = Color.Black, spotColor = Color.Black)
                .clip(RoundedCornerShape(29.dp))
                .background(Color(0xE6242428))
                .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(29.dp))
                .padding(horizontal = 6.dp, vertical = 5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf(
                    NavDestination.HOME,
                    NavDestination.BROWSE,
                    NavDestination.RADIO,
                    NavDestination.LIBRARY
                ).forEach { destination ->
                    val isSelected = currentDestination == destination

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    AppleRed.copy(alpha = 0.35f),
                                                    AppleRed.copy(alpha = 0.15f)
                                                )
                                            )
                                        )
                                        .border(1.dp, AppleRed.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onDestinationSelected(destination) }
                            .testTag("nav_tab_${destination.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                tint = if (isSelected) AppleRed else Color(0xFFC4C4C6),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = destination.label,
                                color = if (isSelected) AppleRed else Color(0xFFA1A1AA),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Right Detached Glowing Search Orb
        val isSearchSelected = currentDestination == NavDestination.SEARCH

        Box(
            modifier = Modifier
                .size(58.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = AppleRed.copy(alpha = 0.6f),
                    spotColor = AppleRed
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF3C5F),
                            AppleRed,
                            Color(0xFF8B001D)
                        )
                    )
                )
                .border(
                    1.dp,
                    if (isSearchSelected) Color.White else Color.White.copy(alpha = 0.35f),
                    CircleShape
                )
                .clickable { onDestinationSelected(NavDestination.SEARCH) }
                .testTag("nav_search_orb"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
