package com.keneth.realestateapplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.keneth.realestateapplication.viewModels.PropertyViewModel
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

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
import coil.compose.SubcomposeAsyncImage

@Composable
fun ListPropertyScreen(
    propertyId: String,
    navController: NavController,
    viewModel: PropertyViewModel
) {
    // Collect property details
    val property = viewModel.propertyDetails.value

    LaunchedEffect(propertyId) {
        viewModel.fetchPropertyById(propertyId)
    }

    // State for success message
    var successMessage by remember { mutableStateOf<String?>(null) }

    // State for tracking the selected image index in the gallery
    var selectedImageIndex by remember { mutableIntStateOf(-1) } // -1 means no image is selected

    // State for controlling the visibility of the image dialog
    var showImageDialog by remember { mutableStateOf(false) }

    // State for showing sold property message
    var showSoldMessage by remember { mutableStateOf(false) }

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
            AppTopBar(
                title = "List Property",
                onMenuClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Property Title
            if (property != null) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically

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

                // Display property status above the button
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

                // List Property Button (only show if the property is not listed or sold)
                if (!property.isListed && !property.isSold) {
                    Button(
                        onClick = {
                            viewModel.listProperty(propertyId)
                            successMessage = "Property listed successfully!"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("List this property")
                    }
                } else if (property.isSold) {
                    // Show sold message when button is clicked for a sold property
                    if (showSoldMessage) {
                        Text(
                            text = "This property is sold and cannot be listed.",
                            color = Color.Red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Button(
                        onClick = { showSoldMessage = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("List this property")
                    }
                } else if (property.isListed) {
                    successMessage = "Property is already listed!"
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
}