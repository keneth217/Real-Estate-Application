package com.keneth.realestateapplication.views

import com.keneth.realestateapplication.viewModels.MultiStepFormViewModel
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.data.RealEstateUserRoles
import com.keneth.realestateapplication.enum.RegistrationStep
import com.keneth.realestateapplication.viewModels.AuthStatus
import com.keneth.realestateapplication.viewModels.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun MultiStepForm(
    navController: NavController,
    userViewModel: UserViewModel,
    multiStepFormViewModel: MultiStepFormViewModel
) {
    val currentStep = multiStepFormViewModel.currentStep
    val isLoading = userViewModel.isLoading.value
    val isSuccess = multiStepFormViewModel.isSuccess
    val errorMessage = multiStepFormViewModel.errorMessage
    val authStatus= userViewModel.authState.value

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentStep == RegistrationStep.EMAIL_PASSWORD) {
                Image(
                    painter = painterResource(id = R.drawable.img2),
                    contentDescription = "Registration Header Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = when (currentStep) {
                    RegistrationStep.EMAIL_PASSWORD -> "Step 1: Email & Password"
                    RegistrationStep.PERSONAL_DETAILS -> "Step 2: Personal Details"
                    RegistrationStep.USER_ROLES -> "Step 3: Select Roles"
                    RegistrationStep.ADDRESS -> "Step 4: Address (Optional)"
                    RegistrationStep.PROFILE_PICTURE -> "Step 5: Profile Picture (Optional)"
                    RegistrationStep.REVIEW -> "Step 6: Review & Submit"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )

            RegistrationProgress(currentStep)
            Spacer(modifier = Modifier.height(20.dp))

            when (currentStep) {
                RegistrationStep.EMAIL_PASSWORD -> EmailPasswordStep(multiStepFormViewModel)
                RegistrationStep.PERSONAL_DETAILS -> PersonalDetailsStep(multiStepFormViewModel)
                RegistrationStep.USER_ROLES -> RoleStep(multiStepFormViewModel)
                RegistrationStep.ADDRESS -> AddressStep(multiStepFormViewModel)
                RegistrationStep.PROFILE_PICTURE -> ProfilePictureStep(multiStepFormViewModel)
                RegistrationStep.REVIEW -> ReviewStep(multiStepFormViewModel, navController,userViewModel)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep != RegistrationStep.EMAIL_PASSWORD) {
                    Button(
                        onClick = { multiStepFormViewModel.previousStep() },
                        modifier = Modifier.padding(vertical = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCB35D5),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Text("Previous")
                    }
                }

                if (currentStep == RegistrationStep.REVIEW) {
                    Button(
                        onClick = {
                            multiStepFormViewModel.submitForm(
                                onSuccess = {
                                    multiStepFormViewModel.isSuccess = true
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
                            pressedElevation = 2.dp
                        ),
                        enabled = !isLoading
                    ) {
                        if (multiStepFormViewModel.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Submit")
                        }
                    }
                } else {
                    val isNextButtonEnabled = when (currentStep) {
                        RegistrationStep.EMAIL_PASSWORD -> multiStepFormViewModel.isEmailPasswordStepValid()
                        RegistrationStep.PERSONAL_DETAILS -> multiStepFormViewModel.isPersonalDetailsStepValid()
                        else -> true
                    }
                    Button(
                        onClick = { multiStepFormViewModel.nextStep() },
                        modifier = Modifier.padding(vertical = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCB35D5),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        ),
                        enabled = isNextButtonEnabled
                    ) {
                        Text("Next")
                    }
                }
            }

            if (currentStep == RegistrationStep.EMAIL_PASSWORD) {
                Text(
                    "Already have an account? Login.",
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Login.route)
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (currentStep == RegistrationStep.ADDRESS || currentStep == RegistrationStep.PROFILE_PICTURE) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { multiStepFormViewModel.skipStep() }) {
                    Text("SKIP")
                }
            }
        }
    }
}

@Composable
fun RegistrationProgress(currentStep: RegistrationStep) {
    val progress = when (currentStep) {
        RegistrationStep.EMAIL_PASSWORD -> 0.0f
        RegistrationStep.PERSONAL_DETAILS -> 0.5f
        RegistrationStep.USER_ROLES -> 0.6F
        RegistrationStep.ADDRESS -> 0.7f
        RegistrationStep.PROFILE_PICTURE -> 0.8f
        RegistrationStep.REVIEW -> 1.0f
    }



    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = Color.Green,
            trackColor = Color.Gray,
            strokeCap = StrokeCap.Round
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
fun EmailPasswordStep(viewModel: MultiStepFormViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        PasswordField(
            password = viewModel.password,
            onPasswordChange = { viewModel.password = it }
        )
    }
}

@Composable
fun RoleStep(viewModel: MultiStepFormViewModel) {
    val selectedRoles = viewModel.selectedRoles

    Column(modifier = Modifier.fillMaxWidth()) {
        RealEstateUserRoles.entries.forEach { role ->
            if (role != RealEstateUserRoles.ADMIN) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.toggleRoleSelection(role)
                        }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = selectedRoles.contains(role),
                        onCheckedChange = { viewModel.toggleRoleSelection(role) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = role.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun PersonalDetailsStep(viewModel: MultiStepFormViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.firstName,
            onValueChange = { viewModel.firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.lastName,
            onValueChange = { viewModel.lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.phone,
            onValueChange = { viewModel.phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddressStep(viewModel: MultiStepFormViewModel) {
    Column(
    ) {
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
fun ProfilePictureStep(viewModel: MultiStepFormViewModel) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent() // Single selection
    ) { uri ->
        if (uri != null) {
            viewModel.profilePicture = uri // Update ViewModel with selected image
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Upload Image Button
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Selected Image
        viewModel.profilePicture?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } ?: Text(
            text = "No image selected",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Composable
fun ReviewStep(viewModel: MultiStepFormViewModel, navController: NavController, userViewModel: UserViewModel) {
    val authState = userViewModel.authState.value
    val isLoading = viewModel.isLoading

    // State to control the visibility of the success/error dialog
    var showDialog by remember { mutableStateOf(false) }

    // Show dialog when authState changes
    LaunchedEffect(authState) {
        if (authState is AuthStatus.Success || authState is AuthStatus.Error) {
            showDialog = true
        }
    }

    // Navigate to login page after successful registration
    LaunchedEffect(authState) {
        if (authState is AuthStatus.Success) {
            delay(2000) // Wait for 2 seconds before navigating
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.AddProperty.route) { inclusive = true }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Display user details for review
        Text("Email: ${viewModel.email}")
        Text("First Name: ${viewModel.firstName}")
        Text("Last Name: ${viewModel.lastName}")
        Text("Phone: ${viewModel.phone}")
        Text("Roles selected: ${viewModel.selectedRoles}")
        Text("Address: ${viewModel.address.street}, ${viewModel.address.city}, ${viewModel.address.state}, ${viewModel.address.postalCode}, ${viewModel.address.country}")

        // Display the selected profile picture
        viewModel.profilePicture?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }

        // Submit button
        Button(
            onClick = {
                viewModel.submitForm(
                    onSuccess = {
                        // Handle success (navigation is handled in LaunchedEffect)
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                Text("Signing Up...") // Change text when loading
            } else {
                Text("Submit")
            }
        }

        // Success or Error Dialog
        if (showDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent background
                    .clickable { showDialog = false }, // Dismiss dialog on click outside
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (authState) {
                            is AuthStatus.Success -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = Color.Green,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Registration Successful!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green
                                )
                                Text(
                                    text = "You will be redirected to the login page shortly.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            is AuthStatus.Error -> {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Error",
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Registration Failed",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Text(
                                    text = (authState as AuthStatus.Error).message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            else -> {}
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFCB35D5),
                                contentColor = Color.White
                            )
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
