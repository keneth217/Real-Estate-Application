package com.keneth.realestateapplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.viewModels.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun AgentDashboardScreen(
    navController: NavHostController,viewModelUser: UserViewModel
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    var displayName by remember { mutableStateOf("User") }

    val userDetails = viewModelUser.userProfile.value
    val firstName = viewModelUser.userFirstName.value
    val welcome = viewModelUser.userProfile.value
    //  val profileImage: String = viewModelUser.userProfile.value?.profileImage ?: ""
    if (welcome != null) {
        displayName = welcome.firstName.uppercase()
    }
    val currentHour = LocalTime.now().hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning"
        in 12..15 -> "Good Afternoon"
        in 16..20 -> "Good Evening"
        else -> "Good Night"
    }
    val profilePicture by viewModelUser.userProfile
    val profileImage = viewModelUser.userProfile.value?.profileImage ?: ""
    LaunchedEffect(Unit) {
        viewModelUser.fetchUserProfile()
    }
    val profileFontFamily = FontFamily(
        Font(R.font.darkmode_regular_400, weight = FontWeight.Normal),
        Font(R.font.cluisher_brush, weight = FontWeight.Bold)
    )





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
                    title = ("Tenant"+ " " + Screen.TenantDashboard.title) ?: "Dashboard",
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
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "$greeting ,",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black
                                    )
                                )
                                Spacer(modifier = Modifier.padding(top = 4.dp))

                                Text(
                                    text = displayName,
                                    style = TextStyle(
                                        fontSize = 30.sp,
                                        fontFamily = profileFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black,
                                        letterSpacing = 2.0.sp
                                    )
                                )
                            }
                            ProfilePicture(
                                profilePicture = profileImage,
                                profileImage = profileImage.toString(),
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

}