package com.keneth.realestateapplication.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel
import java.text.NumberFormat
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PropertyListingScreen(
    navController: NavController,
    propertyViewModel: PropertyViewModel,

    ) {
    //val propertyLists =propertyViewModel.propertyLists.value
    val listedProperties = propertyViewModel.listedProperties.value
    val totalListed = propertyViewModel.totalListedProperties.value
    val isLoading = propertyViewModel.isLoadingListedProperties.value
    //val totalSales=propertyViewModel.totalSales.value
    val topBarTitle = "Property Listing ($totalListed)"


    // Fetch listed properties when the screen is launched
    LaunchedEffect(Unit) {
        propertyViewModel.fetchListedProperties()
    }
    // println("all properties $propertyLists")
    println("Listing screen")
    println("listed properties $listedProperties")
    println("total listed properties $totalListed")
    // println("total sales properties $totalSales")
    Scaffold(
        topBar = {
            AppTopBar(
                title = topBarTitle,
                onMenuClick = { navController.popBackStack() }
            )
        },
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
            Column(modifier = Modifier.padding(4.dp)) {

                if (isLoading) {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                           // modifier = Modifier.size(30.dp),
                        )
                    }


                } else if (listedProperties.isEmpty()) {
                    Text(
                        "No properties listed for Sale !",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(listedProperties) { property ->
                            ListedPropertyCard(property, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListedPropertyCard(
    property: Property,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .padding(2.dp)
            .clickable {
                val route = Screen.PropertyDetails.createRoute(property.uuid)
                navController.navigate(route)

                println("Item route we navigating to $route")
                println("property Id ${property.uuid}")

            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            SubcomposeAsyncImage(
                model = property.images.firstOrNull() ?: R.drawable.img_default, // Get first image or default
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
                        CircularProgressIndicator(
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

