package com.keneth.realestateapplication.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil3.compose.rememberAsyncImagePainter
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.data.Address
import com.keneth.realestateapplication.data.Amenities
import com.keneth.realestateapplication.data.ContactInfo
import com.keneth.realestateapplication.data.PropertyType
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import androidx.compose.material3.TabRowDefaults as TabRowDefaults1

@Composable
fun PropertyDetailsScreen(
    propertyId: String,
    navController: NavController,
    propertyViewModel: PropertyViewModel
) {
    // Fetch property details when the screen is launched
    LaunchedEffect(propertyId) {
        propertyViewModel.fetchPropertyById(propertyId)
    }

    // Observe the property details from the ViewModel
    val property = propertyViewModel.propertyDetails.value

    // State for selected tab index
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Description", "Amenities", "Contacts", "Address", "Property Type", "Meta")

    // State for tracking the selected image index in the gallery
    var selectedImageIndex by remember { mutableIntStateOf(-1) } // -1 means no image is selected

    // State for controlling the visibility of the image dialog
    var showImageDialog by remember { mutableStateOf(false) }

    // Show the image dialog if an image is selected
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

    Scaffold(
        topBar = {
            Screen.PropertyDetails.title?.let {
                AppTopBar(
                    title = it,
                    onMenuClick = { navController.popBackStack() }
                )
            }
        }
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

                // Horizontal Image Gallery
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

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    contentColor = MaterialTheme.colorScheme.primary
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
                ) {
                    when (tabs[selectedTabIndex]) {
                        "Description" -> DescriptionTab(property.description)
                        "Amenities" -> AmenitiesTab(property.amenities)
                        "Contacts" -> ContactsTab(property.contactInfo)
                        "Address" -> AddressTab(property.address)
                        "Property Type" -> PropertyTypeTab(property.propertyType)
                    }
                }
            }
        } else {
            // Show loading or error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ImageDialog(
    images: List<String>,
    selectedImageIndex: Int,
    onDismiss: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                SubcomposeAsyncImage(
                    model = images[selectedImageIndex],
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(400.dp)
                        .height(300.dp)
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

                // Navigation buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    IconButton(onClick = onNext) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}

@Composable
fun ContactsTab(contactInfo: ContactInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Name: ${contactInfo.name}", style = MaterialTheme.typography.bodyMedium)
        Text("Phone: ${contactInfo.phone}", style = MaterialTheme.typography.bodyMedium)
        Text("Email: ${contactInfo.email}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun AddressTab(address: Address) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Street: ${address.street}", style = MaterialTheme.typography.bodyMedium)
        Text("City: ${address.city}", style = MaterialTheme.typography.bodyMedium)
        Text("State: ${address.state}", style = MaterialTheme.typography.bodyMedium)
        Text("Postal Code: ${address.postalCode}", style = MaterialTheme.typography.bodyMedium)
        Text("Country: ${address.country}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PropertyTypeTab(propertyType: PropertyType) {
    Text(
        text = propertyType.name,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun DescriptionTab(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun AmenitiesTab(amenities: Amenities) {

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Pool: ${amenities.pool}", style = MaterialTheme.typography.bodyMedium)
        Text("Parking: ${amenities.parking}", style = MaterialTheme.typography.bodyMedium)
        Text("Gym: ${amenities.gym}", style = MaterialTheme.typography.bodyMedium)
    }
}