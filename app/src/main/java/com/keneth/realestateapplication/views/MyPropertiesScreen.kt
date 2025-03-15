import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.views.AppTopBar
import com.keneth.realestateapplication.views.ListedPropertyCard
import com.keneth.realestateapplication.views.Screen

@Composable
fun MyPropertiesScreen(navController: NavHostController, viewModel: PropertyViewModel) {
    // Fetch listed properties and other data
    val listedProperties = viewModel.listedProperties.value
    val totalListed = viewModel.totalListedProperties.value
    val allProperties=viewModel.propertyLists.value
    val soldProperties=viewModel.soldProperties.value
    val isLoading = viewModel.isLoadingListedProperties.value
    val totalSales = viewModel.fetchTotalSales()
    val totalProperties= viewModel.totalProperties.value

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // State for selected tab
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Listed", "Sold")

    // Fetch listed properties when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.fetchListedProperties()
        viewModel.fetchAllProperties()
        viewModel.fetchSoldProperties()
    }

    // Filter properties based on search query and selected tab
    val filteredProperties = when (selectedTabIndex) {
        0 -> allProperties // Show all properties
        1 -> listedProperties
        2 -> soldProperties// Show sold properties
        else -> allProperties
    }.filter { property ->
        property.title.contains(searchQuery, ignoreCase = true) ||
                property.description.contains(searchQuery, ignoreCase = true) ||
                property.address.street.contains(searchQuery, ignoreCase = true) ||
                property.address.city.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Screen.MyProperties.title?.let {
                AppTopBar(
                    title = "$it ($totalProperties)",
                    onMenuClick = { navController.popBackStack() }
                )
            }
        }, containerColor = Color.White,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(4.dp), // Rounded corners
                onClick = { navController.navigate(Screen.AddProperty.route) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Property") },
                text = { Text("Add Property") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs for All, Listed, Sold
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
               containerColor = Color.White,
                contentColor = Color.Black,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search for property") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Display properties or loading/error states
            Column(modifier = Modifier.padding(4.dp)) {
                if (isLoading) {
                    // Show loading indicator
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (filteredProperties.isEmpty()) {
                    // Show message if no properties are found
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            when (selectedTabIndex) {
                                0 -> "No properties found!"
                                1 -> "No listed properties found!"
                                2 -> "No sold properties found!"
                                else -> "No properties found!"
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    // Display filtered properties
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredProperties) { property ->
                            ListedPropertyCard(property, navController)
                        }
                    }
                }
            }
        }
    }
}