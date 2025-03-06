package com.keneth.realestateapplication.views

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    onItemClick: (String) -> Unit,

    currentRoute: String?, // Pass the current route
) {

    val interactionSource = remember { MutableInteractionSource() }
    // List of screens to display in the drawer
    val drawerScreens = listOf(
        Screen.Dashboard,
        Screen.AddProperty,
        Screen.PropertyTypes,
        Screen.Settings,
        Screen.Reports,
        Screen.MyProperties,


    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            // Menu Header with Green Background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    // Text with weight to push the Icon to the end
                    Text(
                        text = "Menu",
                        modifier = Modifier
                            .padding(bottom = 16.dp, start = 16.dp)
                            .align(Alignment.CenterVertically)
                            .weight(1f), // Push the Icon to the end
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White // Set text color to white
                    )
                    // Icon with click event, larger size, and white tint
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(50.dp) // Make the icon bigger
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() }, // Interaction source for ripple
                                indication = LocalIndication.current // Use the default ripple effect
                            ) {
                                scope.launch { drawerState.close() } // Close the drawer
                            }
                            .padding(end = 16.dp) // Add padding to the end
                            .align(Alignment.CenterVertically),
                        tint = Color.White // Set icon tint to white
                    )
                }
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.5f), // Add a divider for separation
                    thickness = 1.dp
                )
            }
        }
        items(drawerScreens) { screen ->
            NavigationDrawerItem(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                selected = currentRoute == screen.route,
                label = {
                    Text(
                        text = screen.title!!,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    scope.launch { drawerState.close() } // Close the drawer
                    onItemClick(screen.route) // Navigate to the selected screen
                },
                icon = {
                    // Add an icon for each drawer item (optional)
                    val icon = when (screen) {
                        Screen.Settings -> Icons.Default.Settings
                        Screen.Reports -> Icons.Default.List
                        else -> Icons.Default.Info
                    }
                    Icon(imageVector = icon, contentDescription = screen.title)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.Transparent,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}