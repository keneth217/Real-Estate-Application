package com.keneth.realestateapplication.views

import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun AppBottomBar(
    items: List<Screen>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit,
    backgroundColor: Color = Color(0xFF12F11B) // Default background color (green)
) {
    BottomNavigation(
        modifier = Modifier.height(56.dp), // Set a fixed height for the bottom navigation
        backgroundColor = backgroundColor // Set the background color of the bottom navigation
    ) {
        items.forEachIndexed { index, screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = getIconForRoute(screen.route),
                        contentDescription = screen.route
                    )
                },
                label = { Text(text = screen.route) },
                selected = selectedItem == index,
                onClick = { onItemClick(index) },
                selectedContentColor = Color.White, // Color for selected item
                unselectedContentColor = Color.Gray // Color for unselected items
            )
        }
    }
}

// Helper function to get icons for bottom navigation items
fun getIconForRoute(route: String): ImageVector {
    return when (route) {
        Screen.MyProperties.route -> Icons.Default.Home
        Screen.Dashboard.route -> Icons.Default.Menu
        Screen.Reports.route -> Icons.Default.Star
        Screen.Settings.route -> Icons.Default.Settings
        else -> Icons.Default.Home
    }
}
