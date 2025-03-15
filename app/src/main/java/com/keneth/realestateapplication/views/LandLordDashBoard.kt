package com.keneth.realestateapplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
fun LandLordDashboardScreen(
    navController: NavHostController, viewModelUser: UserViewModel
) {
    @Composable
    fun LandLordDashboardScreen(
        navController: NavHostController, viewModelUser: UserViewModel
    ) {
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

        var displayName by remember { mutableStateOf("User") }
        val userDetails = viewModelUser.userProfile.value
        val profileImage = viewModelUser.userProfile.value?.profileImage ?: ""

        if (userDetails != null) {
            displayName = userDetails.firstName.uppercase()
        }

        val currentHour = LocalTime.now().hour
        val greeting = when (currentHour) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            else -> "Good Night"
        }

        LaunchedEffect(Unit) {
            viewModelUser.fetchUserProfile()
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    AppDrawer(
                        drawerState = drawerState,
                        scope = scope,
                        onItemClick = { route ->
                            navController.navigate(route) { launchSingleTop = true }
                            scope.launch { drawerState.close() }
                        },
                        onBackClick = { navController.popBackStack() },
                        currentRoute = currentRoute,
                        drawerItems = listOf(
                            Screen.TenantDashboard,
                            Screen.SellerDashboard,
                            Screen.BuyerDashboard,
                            Screen.GuestDashboard,
                            Screen.LandLordDashboard,
                            Screen.AgentDashboard
                        )
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "Landlord Dashboard",
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
                        .padding(16.dp)
                ) {
                    // Greeting and Profile Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "$greeting,",
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

                    Text("Total Collections")
                    Text("2,000,000.0 Kshs")

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dashboard Cards in Two Columns
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DashboardCard(
                                title = "Total Properties",
                                value = "10",
                                modifier = Modifier.weight(1f)
                            )
                            DashboardCard(
                                title = "Total Tenants",
                                value = "25",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DashboardCard(
                                title = "Pending Rent Payments",
                                value = "$1,200",
                                modifier = Modifier.weight(1f)
                            )
                            DashboardCard(
                                title = "Maintenance Requests",
                                value = "3",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DashboardCard(
                                title = "Upcoming Lease Expirations",
                                value = "2",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        }
    }
}


