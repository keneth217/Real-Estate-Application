package com.keneth.realestateapplication.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.keneth.realestateapplication.data.RealEstateUserRoles
import com.keneth.realestateapplication.data.UserAddress
import com.keneth.realestateapplication.viewModels.UserViewModel

@Composable
fun UpdateProfileScreen(
    navController: NavController,
    userId: String,
    viewModelUser: UserViewModel = viewModel()
) {
    val user by viewModelUser.userProfile
    var isLoading by remember { mutableStateOf(false) }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }

    // Fetch user profile when the screen is launched
    LaunchedEffect(userId) {
        viewModelUser.fetchUserProfile()
    }

    // Show loading state while fetching data
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Prefill form fields with user data
    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var address by remember { mutableStateOf(user?.address ?: UserAddress()) }

    // Track selected roles
    val selectedRoles = remember { mutableStateListOf<RealEstateUserRoles>() }

    // Initialize selected roles with the user's current roles
    LaunchedEffect(user) {
        user?.userRole?.let { roles ->
            selectedRoles.addAll(roles)
        }
    }

    // Image Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> profilePictureUri = uri }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Update Profile") })
    }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture Section
                val imageUri = profilePictureUri ?: user?.profileImage?.let { Uri.parse(it) }
                Box(contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { launcher.launch("image/*") }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { launcher.launch("image/*") }
                        )
                    }
                }

                // First Name Field
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Last Name Field
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Phone Field
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = email,
                    onValueChange = { },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                // Address Fields
                OutlinedTextField(
                    value = address.street,
                    onValueChange = { address = address.copy(street = it) },
                    label = { Text("Street") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address.city,
                    onValueChange = { address = address.copy(city = it) },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address.postalCode,
                    onValueChange = { address = address.copy(postalCode = it) },
                    label = { Text("Postal Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address.country,
                    onValueChange = { address = address.copy(country = it) },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Roles Section
                Text(
                    text = "Roles",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Show only roles that the user has selected
                user?.userRole?.forEach { role ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedRoles.contains(role)) {
                                    selectedRoles.remove(role)
                                } else {
                                    selectedRoles.add(role)
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = selectedRoles.contains(role),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedRoles.add(role)
                                } else {
                                    selectedRoles.remove(role)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = role.name, style = MaterialTheme.typography.body1)
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        isLoading = true
                        if (profilePictureUri != null) {
                            viewModelUser.uploadProfilePicture(profilePictureUri!!) { imageUrl ->
                                val updatedUser = user?.copy(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phone = phone,
                                    email = email,
                                    address = address,
                                    profileImage = imageUrl,
                                    userRole = selectedRoles
                                )
                                if (updatedUser != null) {
                                    viewModelUser.updateUserProfile(updatedUser) {
                                        isLoading = false
                                        navController.popBackStack()
                                    }
                                }
                            }
                        } else {
                            val updatedUser = user?.copy(
                                firstName = firstName,
                                lastName = lastName,
                                phone = phone,
                                email = email,
                                address = address,
                                userRole = selectedRoles
                            )
                            if (updatedUser != null) {
                                viewModelUser.updateUserProfile(updatedUser) {
                                    isLoading = false
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Updating Profile...")
                        }
                    } else {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

