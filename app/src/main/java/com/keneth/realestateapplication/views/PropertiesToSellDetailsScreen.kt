package com.keneth.realestateapplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.keneth.realestateapplication.viewModels.PropertyViewModel

@Composable
fun PropertiesToSellDetailsScreen(
    navController: NavController, propertyViewModel: PropertyViewModel,
    propertyId: String,
) {

    var successMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(propertyId) {
        propertyViewModel.fetchPropertyById(propertyId)
    }
    val property = propertyViewModel.propertyDetails.value
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Basic Details",
        "Description",
        "Amenities",
        "Contacts",
        "Address",
        "Property Type"
    )
    var selectedImageIndex by remember { mutableIntStateOf(-1) }
    var showImageDialog by remember { mutableStateOf(false) }
    if (showImageDialog && selectedImageIndex != -1) {
        ImageDialog(
            images = property?.images ?: emptyList(),
            selectedImageIndex = selectedImageIndex,
            onDismiss = { showImageDialog = false },
            onNext = {
                selectedImageIndex = (selectedImageIndex + 1) % (property?.images?.size ?: 1)
            },
            onBack = {
                selectedImageIndex =
                    if (selectedImageIndex - 1 < 0) (property?.images?.size
                        ?: 1) - 1 else selectedImageIndex - 1
            }
        )
    }

    // Show success dialog if there's a success message
    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { successMessage = null }, // Reset the success message
            title = { Text("Success") },
            text = { Text(successMessage!!) },
            confirmButton = {
                Button(
                    onClick = { successMessage = null }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Screen.PropertyToSellDetails.title?.let {
                AppTopBar(
                    title = it,
                    onMenuClick = { navController.popBackStack() }
                )
            }
        }, containerColor = Color.White, contentColor = Color.Black
    ) { paddingValues ->
        if (property != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Property Title
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(property.images.size) { index ->
                        Card(
                            modifier = Modifier
                                .width(300.dp)
                                .height(250.dp)
                                .clickable {
                                    selectedImageIndex = index
                                    showImageDialog = true
                                }
                        ) {
                            SubcomposeAsyncImage(
                                model = property.images[index],
                                contentDescription = "Property Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(250.dp)
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
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (!property.isSold) {
                        Button(
                            onClick = {

                                navController.navigate(
                                    Screen.SellPropertyScreen.createRoute(
                                        propertyId
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFCB35D5),
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 2.dp,
                                hoveredElevation = 8.dp
                            ),
                        ) {
                            Text("Buy Property")
                        }
                    }
                }

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    contentColor = Color.Magenta

                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = title) }
                        )
                    }
                }

                // Tab Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp)
                        .background(Color.White)
                ) {
                    when (tabs[selectedTabIndex]) {
                        "Basic Details" -> BasicDetailsTab(property)
                        "Description" -> DescriptionTab(property.description)
                        "Amenities" -> AmenitiesTab(property.amenities)
                        "Contacts" -> ContactsTab(property.contactInfo)
                        "Address" -> AddressTab(property.address)
                        "Property Type" -> PropertyTypeTab(property.propertyType)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
