package com.keneth.realestateapplication.views

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppBottomBar(
    items: List<Screen>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit
) {
    BottomNavigation {
        items.forEachIndexed { index, screen ->
            BottomNavigationItem(
                icon = { Icon(imageVector = getIconForRoute(screen.route), contentDescription = screen.route) },
                label = { Text(text = screen.route) },
                selected = selectedItem == index,
                onClick = { onItemClick(index) }
            )
        }
    }
}

// Helper function to get icons for bottom navigation items
fun getIconForRoute(route: String): ImageVector {
    return when (route) {
        Screen.Dashboard.route -> Icons.Default.List
        Screen.Reports.route -> Icons.Default.Star
        Screen.Settings.route -> Icons.Default.Settings
        else -> Icons.Default.Home
    }
}