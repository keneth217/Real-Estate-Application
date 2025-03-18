package com.keneth.realestateapplication.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PropertiesToSellScreen( navController: NavController,propertyViewModel: PropertyViewModel){
    val propertiesToSell= propertyViewModel.propertiesToSell.value
    val isLoading =propertyViewModel.isLoading.value
    val errorMessage= propertyViewModel.errorMessage.value


    LaunchedEffect(Unit) {
        propertyViewModel.fetchPropertiesToSell()
    }

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // Fetch listed properties when the screen is launched
    LaunchedEffect(Unit) {
        propertyViewModel.fetchListedProperties()
    }

    val filteredProperties = propertiesToSell.filter { property ->
        property.title.contains(searchQuery, ignoreCase = true) ||
                property.description.contains(searchQuery, ignoreCase = true) ||
                property.address.street.contains(searchQuery, ignoreCase = true) ||
                property.address.city.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Screen.PropertiesToRentScreen.title?.let {
                AppTopBar(
                    title = it ,
                    onMenuClick = { navController.popBackStack() }
                )
            }
        }, containerColor = Color.White, contentColor = Color.Black,

        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                        androidx.compose.material.CircularProgressIndicator()
                    }
                } else if (propertiesToSell.isEmpty()) {
                    // Show message if no properties are listed
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No properties listed for sale!",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                } else if (searchQuery.isNotEmpty() && filteredProperties.isEmpty()) {
                    // Show message if no properties match the search query
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No properties found for : '$searchQuery'!",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                } else {
                    // Display filtered properties
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredProperties) { property ->
                            ListedPropertyToSellCard(property, navController)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ListedPropertyToSellCard(
    property: Property,
    navController: NavController
) {
    androidx.compose.material.Card(
        modifier = Modifier
            .padding(2.dp)
            .clickable {
                val route = Screen.PropertyToSellDetails.createRoute(property.uuid)
                navController.navigate(route)

                println("Item route we navigating to $route")
                println("property Id ${property.uuid}")

            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray), backgroundColor = Color.Gray
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            SubcomposeAsyncImage(
                model = property.images.firstOrNull()
                    ?: R.drawable.img_default, // Get first image or default
                contentDescription = "Property Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(2.dp)),
                loading = {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

            )

            println("images ${property.images}")

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = property.title,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                )
                Row(

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = property.address.city,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        ),
                    )
                }
                Text(
                    text = property.description,
                    style = TextStyle(fontSize = 14.sp, color = Color.Black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.currency.uppercase() + ":",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = NumberFormat.getNumberInstance(Locale.US).format(property.price),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { /* Handle Call */ }) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { /* Handle Message */ }) {
                            Icon(
                                imageVector = Icons.Default.MailOutline,
                                contentDescription = "Message",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}


