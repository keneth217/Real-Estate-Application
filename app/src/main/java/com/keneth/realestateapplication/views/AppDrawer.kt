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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch

// Define Drawer Item Data Class
data class DrawerItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    onItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    backgroundColor: Color=Color.White ,
    currentRoute: String?,
    drawerItems: List<Screen> // Corrected Type
) {

    val currentScreenTitle = drawerItems.find { it.title == currentRoute }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
           .background(backgroundColor)
    ) {
        item {
            // Menu Header
            Column(
                modifier = Modifier
                    .fillMaxSize()
                  //  .background(MaterialTheme.colorScheme.primary)
            ) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Menu",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black
                    )

                    if (currentScreenTitle != null) {
                        if (currentScreenTitle.title?.contains("DashBoard", ignoreCase = true) == true) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = LocalIndication.current
                                    ) {
                                        scope.launch { drawerState.open() }
                                    }
                                    .padding(end = 16.dp)
                                    .align(Alignment.CenterVertically),
                                tint = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = LocalIndication.current
                                    ) {
                                        onBackClick()
                                    }
                                    .padding(end = 16.dp)
                                    .align(Alignment.CenterVertically),
                                tint = Color.White
                            )
                        }
                    }
                }
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
            }
        }
        items(drawerItems) { item ->
            NavigationDrawerItem(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                selected = currentRoute == item.route,
                label = {
                    item.title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (currentRoute == item.route) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                },
                onClick = {
                    scope.launch { drawerState.close() }
                    onItemClick(item.route)
                },
                icon = {
                    item.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.title,
                            tint = if (currentRoute == item.route) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
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
