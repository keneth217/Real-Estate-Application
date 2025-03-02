package com.keneth.realestateapplication.views

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.ripple
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.data.User

import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: PropertyViewModel,
    viewModelUser: UserViewModel
) {


    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    var selectedItem by remember { mutableIntStateOf(0) }
    val propertyLists by viewModel.propertyLists
    val listedProperties by viewModel.listedProperties
    val soldProperties by viewModel.soldProperties
    val totalProperties by viewModel.totalProperties
    val totalListedProperties by viewModel.totalListedProperties
    val totalSoldProperties by viewModel.totalSoldProperties
    val propertyCategoryLists by viewModel.propertyCategoryLists
    val interactionSource = remember { MutableInteractionSource() }
    val topBarTitle by remember { mutableStateOf("Dashboard") }
    val userDetails = viewModelUser.userProfile.value
    val firstName = viewModelUser.userFirstName.value
    val displayName by remember { mutableStateOf("User") }
    val currentHour = LocalTime.now().hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning"
        in 12..15 -> "Good Afternoon"
        in 16..20 -> "Good Evening"
        else -> "Good Night"
    }

    println("total $totalProperties")

    val bottomNavItems = listOf(
        Screen.PropertyListing, Screen.Reports, Screen.Settings
    )

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            AppDrawer(
                drawerState = drawerState, scope = scope, onItemClick = { route ->
                    navController.navigate(route)
                    scope.launch { drawerState.close() }
                }, currentRoute = currentRoute
            )
        }
    }) {
        Scaffold(topBar = {
            AppTopBar(title = topBarTitle, onMenuClick = { scope.launch { drawerState.open() } })
        }, bottomBar = {
            AppBottomBar(items = bottomNavItems,
                selectedItem = selectedItem,
                onItemClick = { index ->
                    selectedItem = index
                    navController.navigate(bottomNavItems[index].route)
                })
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Green header with user greeting
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF03F603), Color(0xFF7AF198))
                            ), shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "$greeting ,", style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.padding(top = 10.dp))
                                if (userDetails != null) {
                                    if (firstName != null) {
                                        Text(
                                            text = firstName.uppercase(), style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                            Image(
                                painter = painterResource(id = R.drawable.person),

                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                                    .clickable {
                                        navController.navigate(Screen.Profile.route)
                                    },
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }

                    // Dashboard metrics
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 50.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),

                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DashboardCard("Total ", totalProperties)
                            DashboardCard("Listed ", totalListedProperties)
                            DashboardCard("Sold ", totalSoldProperties)
                        }
                    }
                }

                // Property Categories
                Column(modifier = Modifier.padding(top = 30.dp, start = 20.dp)) {
                    Text("Property Categories", fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    if (propertyCategoryLists.isEmpty()) {
                        Text(
                            "No categories found. Add a new category!",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyVerticalGrid(
                            GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(propertyCategoryLists) { category ->
                                CategoryCard(category.category) {
                                    navController.navigate("${Screen.PropertyCategories.route}/${category.uuid}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, value: Int) {
    Card(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .size(100.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    value.toString(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CategoryCard(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

