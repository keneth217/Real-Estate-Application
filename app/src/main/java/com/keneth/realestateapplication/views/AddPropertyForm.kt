package com.keneth.realestateapplication.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import coil.compose.rememberAsyncImagePainter
import com.keneth.realestateapplication.data.Amenities
import com.keneth.realestateapplication.data.PROPERTY_PURPOSE
import com.keneth.realestateapplication.data.RealEstateUserRoles
import com.keneth.realestateapplication.enum.AddPropertyStep
import com.keneth.realestateapplication.enum.RegistrationStep
import com.keneth.realestateapplication.viewModels.AddPropertyViewModel
import com.keneth.realestateapplication.viewModels.MultiStepFormViewModel
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddPropertyForm(
    navController: NavController,
    propertyViewModel: PropertyViewModel,
    multiStepFormPropertyViewModel: AddPropertyViewModel
) {

    val currentStep = multiStepFormPropertyViewModel.currentStep
    val isLoading = propertyViewModel.isLoading.value
    val isSuccess = multiStepFormPropertyViewModel.isSuccess
    val errorMessage = multiStepFormPropertyViewModel.errorMessage

    Scaffold(
        topBar = {
            Screen.AddProperty.title?.let {
                AppTopBar(
                    title = it,
                    onMenuClick = { navController.popBackStack() }
                )
            }
        }, containerColor = Color.White, contentColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (currentStep) {
                    AddPropertyStep.BASIC_DETAILS -> "Step 1: Basic Details"
                    AddPropertyStep.CONTACT_INFO -> "Step 2: Contact Information"
                    AddPropertyStep.ADDRESS -> "Step 3: Address"
                    AddPropertyStep.PURPOSE -> "Step4 : Choose property purpose"
                    AddPropertyStep.PROPERTY_TYPE -> "Step 4: Property Type"
                    AddPropertyStep.IMAGES -> "Step 5: Images"
                    AddPropertyStep.AMENITIES -> "House amenities"
                    AddPropertyStep.REVIEW -> "Step 6: Review & Submit"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )
            PropertyProgress(currentStep)
            Spacer(modifier = Modifier.height(20.dp))
            when (currentStep) {
                AddPropertyStep.BASIC_DETAILS -> BasicDetailsStep(multiStepFormPropertyViewModel)
                AddPropertyStep.CONTACT_INFO -> ContactInfoStep(multiStepFormPropertyViewModel)
                AddPropertyStep.PURPOSE -> PropertyPurposeStep(multiStepFormPropertyViewModel)
                AddPropertyStep.ADDRESS -> AddressStep(multiStepFormPropertyViewModel)
                AddPropertyStep.PROPERTY_TYPE -> PropertyTypeStep(
                    propertyViewModel = propertyViewModel,
                    addPropertyViewModel = multiStepFormPropertyViewModel
                )

                AddPropertyStep.IMAGES -> ImagesStep(multiStepFormPropertyViewModel)
                AddPropertyStep.AMENITIES -> AmenitiesStep(multiStepFormPropertyViewModel)
                AddPropertyStep.REVIEW -> ReviewStep(
                    viewModel = multiStepFormPropertyViewModel,
                    navController = navController
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep != AddPropertyStep.BASIC_DETAILS) {
                    Button(
                        onClick = { multiStepFormPropertyViewModel.previousStep() },
                        modifier = Modifier.padding(vertical = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCB35D5),
                            contentColor = Color.White
                        ),
                    ) {
                        Text("Previous")
                    }
                }

                if (currentStep == AddPropertyStep.REVIEW) {
                    Button(
                        onClick = {
                            multiStepFormPropertyViewModel.submitForm(
                                onSuccess = {
                                    multiStepFormPropertyViewModel.isSuccess = true
                                }
                            )
                        },
                        modifier = Modifier.padding(vertical = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3FC918),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                            hoveredElevation = 8.dp
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Submit")
                        }
                    }
                } else {
                    Button(
                        onClick = { multiStepFormPropertyViewModel.nextStep() },
                        modifier = Modifier.padding(vertical = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCB35D5),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                            hoveredElevation = 8.dp
                        )
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { multiStepFormPropertyViewModel.errorMessage = null },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { multiStepFormPropertyViewModel.errorMessage = null }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun PropertyProgress(currentStep: AddPropertyStep) {
    val progress = when (currentStep) {
        AddPropertyStep.BASIC_DETAILS -> 0.0f
        AddPropertyStep.CONTACT_INFO -> 0.2f
        AddPropertyStep.PURPOSE ->0.3f
        AddPropertyStep.ADDRESS -> 0.4f
        AddPropertyStep.PROPERTY_TYPE -> 0.5f
        AddPropertyStep.AMENITIES -> 0.6f
        AddPropertyStep.IMAGES -> 0.8f
        AddPropertyStep.REVIEW -> 1.0f
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = Color.Green,
            trackColor = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Progress: ${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BasicDetailsStep(viewModel: AddPropertyViewModel) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.price.toString(),
                onValueChange = { viewModel.price = it.toDoubleOrNull() ?: 0.0 },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.currency,
                onValueChange = { viewModel.currency = it },
                label = { Text("Currency") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.bedrooms.toString(),
                onValueChange = { viewModel.bedrooms = it.toIntOrNull() ?: 0 },
                label = { Text("Bedrooms") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.bathrooms.toString(),
                onValueChange = { viewModel.bathrooms = it.toIntOrNull() ?: 0 },
                label = { Text("Bathrooms") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.area.toString(),
                onValueChange = { viewModel.area = it.toDoubleOrNull() ?: 0.0 },
                label = { Text("Area") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.areaUnit,
                onValueChange = { viewModel.areaUnit = it },
                label = { Text("Area Unit") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }


}

@Composable
fun ContactInfoStep(viewModel: AddPropertyViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.contactInfo.name,
            onValueChange = {
                viewModel.contactInfo = viewModel.contactInfo.copy(name = it)
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.contactInfo.phone,
            onValueChange = {
                viewModel.contactInfo = viewModel.contactInfo.copy(phone = it)
            },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.contactInfo.email,
            onValueChange = {
                viewModel.contactInfo = viewModel.contactInfo.copy(email = it)
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AmenitiesStep(viewModel: AddPropertyViewModel) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = viewModel.amenities.gym,
                onCheckedChange = { viewModel.amenities = viewModel.amenities.copy(gym = it) }
            )
            Text("Gym")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = viewModel.amenities.pool,
                onCheckedChange = { viewModel.amenities = viewModel.amenities.copy(pool = it) }
            )
            Text("Pool")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = viewModel.amenities.parking,
                onCheckedChange = { viewModel.amenities = viewModel.amenities.copy(parking = it) }
            )
            Text("Parking")
        }
    }
}

@Composable
fun AddressStep(viewModel: AddPropertyViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.address.street,
            onValueChange = { viewModel.address = viewModel.address.copy(street = it) },
            label = { Text("Street") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.address.city,
            onValueChange = { viewModel.address = viewModel.address.copy(city = it) },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.address.state,
            onValueChange = { viewModel.address = viewModel.address.copy(state = it) },
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.address.postalCode,
            onValueChange = { viewModel.address = viewModel.address.copy(postalCode = it) },
            label = { Text("Postal Code") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.address.country,
            onValueChange = { viewModel.address = viewModel.address.copy(country = it) },
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PropertyTypeStep(
    propertyViewModel: PropertyViewModel,
    addPropertyViewModel: AddPropertyViewModel
) {
    val propertyTypes = propertyViewModel.propertyTypes.value
    var showDialog by remember { mutableStateOf(false) }
    val selectedTypes = remember { mutableStateListOf<String>() }

    Column {
        Button(
            onClick = {
                propertyViewModel.fetchPropertyType()
                showDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCB35D5),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text("Select Property Type")
        }

        if (showDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Select Property Type",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp) // Limit height if list is long
                        ) {
                            items(propertyTypes) { type ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (selectedTypes.contains(type.name)) {
                                                selectedTypes.remove(type.name)
                                            } else {
                                                selectedTypes.add(type.name)
                                            }
                                        }
                                        .padding(8.dp)
                                ) {
                                    Checkbox(
                                        checked = selectedTypes.contains(type.name),
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) {
                                                selectedTypes.add(type.name)
                                            } else {
                                                selectedTypes.remove(type.name)
                                            }
                                        }
                                    )
                                    Text(
                                        text = type.name,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    addPropertyViewModel.propertyType.name =
                                        selectedTypes.toList().toString()
                                    showDialog = false
                                },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3FC918),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Confirm")
                            }
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFCB35D5),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyPurposeStep(viewModel: AddPropertyViewModel) {
    val selectedPurpose = viewModel.selectedPurpose

    Column(modifier = Modifier.fillMaxWidth()) {
        PROPERTY_PURPOSE.entries.forEach { purpose ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectPurpose(purpose) }
                    .padding(8.dp)
            ) {
                Checkbox(
                    checked = selectedPurpose == purpose,
                    onCheckedChange = { viewModel.selectPurpose(purpose) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = purpose.name, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
@Composable
fun ImagesStep(viewModel: AddPropertyViewModel) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        // Add selected URIs to the ViewModel
        viewModel.images.addAll(uris)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Upload Images Button
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Images")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Selected Images
        if (viewModel.images.isEmpty()) {
            // Show a placeholder if no images are selected
            Text(
                text = "No images selected",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        } else {
            LazyRow {
                items(viewModel.images) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri), // Load image from URI
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewStep(
    viewModel: AddPropertyViewModel,
    navController: NavController
) {
    val isSuccess = viewModel.isSuccess
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // Show success message and navigate to dashboard
    if (isSuccess) {
        LaunchedEffect(Unit) {
            // Delay to show the success message
            delay(2000) // 2 seconds
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.AddProperty.route) { inclusive = true } // Clear back stack
            }
        }

        AlertDialog(
            onDismissRequest = { /* Do nothing */ },
            title = { Text("Success") },
            text = { Text("Property added successfully!") },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.AddProperty.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Title: ${viewModel.title}")
        Text("Description: ${viewModel.description}")
        Text("Price: ${viewModel.price} ${viewModel.currency}")
        Text("Bedrooms: ${viewModel.bedrooms}")
        Text("Bathrooms: ${viewModel.bathrooms}")
        Text("Area: ${viewModel.area} ${viewModel.areaUnit}")
        Text("Contact Name: ${viewModel.contactInfo.name}")
        Text("Contact Phone: ${viewModel.contactInfo.phone}")
        Text("Contact Email: ${viewModel.contactInfo.email}")
        Text("Address: ${viewModel.address.street}, ${viewModel.address.city}, ${viewModel.address.state}, ${viewModel.address.postalCode}, ${viewModel.address.country}")
        Text("Property Type: ${viewModel.propertyType.name}")
        Text("Property Purpose:${viewModel.propertyPurpose.name}")
        Text("Images: ${viewModel.images.size} uploaded")

        // Submit button
        Button(
            onClick = {
                viewModel.submitForm(
                    onSuccess = {
                        // This block will be executed when the property is successfully added
                        viewModel.isSuccess = true // Set success state
                    }
                )
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Submit")
            }
        }

        // Show error message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}