package com.keneth.realestateapplication.views

import android.content.Context
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.keneth.realestateapplication.R

import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: PropertyViewModel,
    viewModelUser: UserViewModel,
    context: Context
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
    val welcome = viewModelUser.userProfile.value
    var displayName by remember { mutableStateOf("User") }
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

    val profileFontFamily = FontFamily(
        Font(R.font.darkmode_regular_400, weight = FontWeight.Normal),
        Font(R.font.cluisher_brush, weight = FontWeight.Bold)
    )
    // Fetch data from ViewModel
    val totalSales by viewModel.totalSales
    val userProfile by viewModelUser.userProfile

    val profilePicture by viewModelUser.userProfile
    val profileImage = viewModelUser.userProfile.value?.profileImage ?: ""
    LaunchedEffect(Unit) {
        viewModel.fetchTotalSales()
        viewModel.fetchSoldProperties()
        viewModel.fetchAllProperties()
        viewModel.fetchListedProperties()
        viewModelUser.fetchUserProfile()
    }
    println("user profile image: $profileImage")
    println("Total Sales  $totalSales")
    println("total $totalProperties")
    println("total listed $totalListedProperties")
    println("Username $firstName")
    if (welcome != null) {
        println("display ${welcome.lastName}")
    }

    val bottomNavItems = listOf(
        Screen.MyProperties, Screen.PropertyListing, Screen.Reports, Screen.Settings
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Green header with user greeting
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF03F603), Color(0xFF3ACF5F))
                                ),
                                shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
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
                                            fontSize = 24.sp,
                                          // fontFamily = FontFamily.Cursive,
                                            fontWeight = FontWeight.Normal,
                                            color = Color.White
                                            //letterSpacing = 5.0.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.padding(top = 4.dp))

                                    Text(
                                        text = displayName, style = TextStyle(
                                            fontSize = 30.sp,
                                            fontFamily = profileFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            letterSpacing = 2.0.sp
                                        )
                                    )
                                }

                                ProfilePicture(
                                    profilePicture = profileImage,
                                    profileImage = profileImage.toString(),
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape),
                                    navController = navController
                                )

                            }

                            // Card for total sales
                            Spacer(modifier = Modifier.padding(top = 16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(color = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color = Color.White),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "Sales: $totalSales",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                // Properties Statistics
                item {
                    Text(
                        text = "Properties Statistics",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                }

                // Bar chart for totals
                item {
                    TotalsPieChart(
                        totalProperties = totalProperties,
                        totalListedProperties = totalListedProperties,
                        totalSoldProperties = totalSoldProperties,
                        context = context
                    )
                }

                // Property Categories
                item {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Property Categories",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )

                        if (propertyCategoryLists.isEmpty()) {
                            Text(
                                text = "No categories found. Add a new category!",
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
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
