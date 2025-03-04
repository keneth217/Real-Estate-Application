package com.keneth.realestateapplication.views

import MultiStepFormViewModel
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.keneth.realestateapplication.R
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
    val isLoading = multiStepFormViewModel.isLoading
    val isSuccess = multiStepFormViewModel.isSuccess
    val errorMessage = multiStepFormViewModel.errorMessage

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
            // Show an image only on the first step
            if (currentStep == RegistrationStep.EMAIL_PASSWORD) {
                Image(
                    painter = painterResource(id = R.drawable.img2),
                    contentDescription = "Registration Header Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 20.dp), // Adjust spacing
                    contentScale = ContentScale.Crop
                )
            }

            // Display the current step title
            Text(
                text = when (currentStep) {
                    RegistrationStep.EMAIL_PASSWORD -> "Step 1: Email & Password"
                    RegistrationStep.PERSONAL_DETAILS -> "Step 2: Personal Details"
                    RegistrationStep.ADDRESS -> "Step 3: Address (Optional)"
                    RegistrationStep.PROFILE_PICTURE -> "Step 4: Profile Picture (Optional)"
                    RegistrationStep.REVIEW -> "Step 5: Review & Submit"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )

            // Show registration progress
            RegistrationProgress(currentStep)

            Spacer(modifier = Modifier.height(20.dp))

            // Display the current step content
            when (currentStep) {
                RegistrationStep.EMAIL_PASSWORD -> EmailPasswordStep(multiStepFormViewModel)
                RegistrationStep.PERSONAL_DETAILS -> PersonalDetailsStep(multiStepFormViewModel)
                RegistrationStep.ADDRESS -> AddressStep(multiStepFormViewModel)
                RegistrationStep.PROFILE_PICTURE -> ProfilePictureStep(multiStepFormViewModel)
                RegistrationStep.REVIEW -> ReviewStep(
                    multiStepFormViewModel,
                    navController = navController
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep != RegistrationStep.EMAIL_PASSWORD) {
                    Button(
                        onClick = { multiStepFormViewModel.previousStep() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
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
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3FC918),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        ),
                        enabled = !isLoading // Disable button while loading
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
                    // Disable "Next" button if required fields are not filled
                    val isNextButtonEnabled = when (currentStep) {
                        RegistrationStep.EMAIL_PASSWORD -> multiStepFormViewModel.isEmailPasswordStepValid()
                        RegistrationStep.PERSONAL_DETAILS -> multiStepFormViewModel.isPersonalDetailsStepValid()
                        else -> true // Enable for optional steps
                    }

                    Button(
                        onClick = { multiStepFormViewModel.nextStep() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
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
                // Login Link
                Text(
                    "Already have an account? Login.",
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Login.route)
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Skip button for optional steps
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
        RegistrationStep.ADDRESS -> 0.6f
        RegistrationStep.PROFILE_PICTURE -> 0.8f
        RegistrationStep.REVIEW -> 1.0f
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
fun EmailPasswordStep(viewModel: MultiStepFormViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
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

fun ReviewStep(viewModel: MultiStepFormViewModel, navController: NavController) {
    val isSuccess = viewModel.isSuccess
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // Show success message and navigate to login
    if (isSuccess) {
        LaunchedEffect(Unit) {
            // Delay to show the success message
            delay(2000) // 2 seconds
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.AddProperty.route) { inclusive = true } // Clear back stack
            }
        }

        AlertDialog(
            onDismissRequest = { /* Do nothing */ },
            title = { Text("Success") },
            text = { Text("Registration successful!") },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.AddProperty.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Display user details for review
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Email: ${viewModel.email}")
        Text("First Name: ${viewModel.firstName}")
        Text("Last Name: ${viewModel.lastName}")
        Text("Phone: ${viewModel.phone}")
        Text("Address: ${viewModel.address.street}, ${viewModel.address.city}, ${viewModel.address.state}, ${viewModel.address.postalCode}, ${viewModel.address.country}")

        // Submit button
        Button(
            onClick = {
                viewModel.submitForm(
                    onSuccess = {
                        // This block will be executed when the registration is successful
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

