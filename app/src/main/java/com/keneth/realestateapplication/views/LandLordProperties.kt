package com.keneth.realestateapplication.views

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel

@Composable
fun LandLordPropertiesScreen(
    landLordId: String,
    navController: NavController,
    viewModel: PropertyViewModel,
    userViewModel: UserViewModel
) {
    // Collect state from ViewModel
    val landLordProperties = viewModel.propertyLists.value
    val userProfile = userViewModel.userProfile.value
    val isLoading = viewModel.isLoading.value

    // Local state for image dialog
    var selectedImageIndex by remember { mutableStateOf(0) }
    var showImageDialog by remember { mutableStateOf(false) }

    // Fetch data when the screen is launched or landLordId changes
    LaunchedEffect(landLordId) {
        if (landLordId != null) {
            viewModel.fetchPropertiesByUserId(landLordId)
        }
        userViewModel.fetchUserProfile()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "List Property",
                onMenuClick = { navController.popBackStack() }
            )
        },
        containerColor = Color.White,
        contentColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (landLordProperties.isEmpty()) {
                // Show message if no properties are found
                Text(
                    text = "No properties found for this landlord.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                // Display each property
                landLordProperties.forEach { property ->
                    PropertyItem(
                        property = property,
                        navController = navController,
                        onListProperty = { propertyId ->
                            viewModel.listProperty(propertyId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyItem(
    property: Property,
    navController: NavController,
    onListProperty: (String) -> Unit
) {
    var showSoldMessage by remember { mutableStateOf(false) }
    // State for tracking the selected image index in the gallery
    var selectedImageIndex by remember { mutableIntStateOf(-1) } // -1 means no image is selected

    // State for controlling the visibility of the image dialog
    var showImageDialog by remember { mutableStateOf(false) }
    // Show the image dialog if an image is selected
    if (showImageDialog && selectedImageIndex != -1) {
        ImageDialog(
            images = property?.images ?: emptyList(),
            selectedImageIndex = selectedImageIndex,
            onDismiss = {
                showImageDialog = false
                selectedImageIndex = -1 // Reset the selected image index
            },
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Property Title
        Text(
            text = property.title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Property Status
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sold: ${property.isSold}")
            Text("Listed: ${property.isListed}")
        }

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

        // Display property status
        when {
            property.isSold -> {
                Text(
                    text = "This property is already sold.",
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            property.isListed -> {
                Text(
                    text = "This property is already listed.",
                    color = Color.Green,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // List Property Button
        if (!property.isListed && !property.isSold) {
            Button(
                onClick = { onListProperty(property.uuid) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("List this property")
            }
        } else if (property.isSold && showSoldMessage) {
            Text(
                text = "This property is sold and cannot be listed.",
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}



