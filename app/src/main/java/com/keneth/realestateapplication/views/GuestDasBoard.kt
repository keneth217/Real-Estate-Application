package com.keneth.realestateapplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun GuestDashboardScreen(
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    val drawerItems = listOf(
        Screen.TenantDashboard,
        Screen.SellerDashboard,
        Screen.BuyerDashboard,
        Screen.GuestDashboard,
        Screen.LandLordDashboard,
        Screen.AgentDashboard
    )


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawer(
                    drawerState = drawerState,
                    scope = scope,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    onBackClick = { navController.popBackStack() },
                    currentRoute = currentRoute,
                    drawerItems = drawerItems
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = Screen.TenantDashboard.title ?: "Dashboard",
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            containerColor = Color.White,
            contentColor = Color.Black
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Text("Tenant Dashboard")
                }
            }
        }
    }

}