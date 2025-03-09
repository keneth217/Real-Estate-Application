package com.keneth.realestateapplication.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.data.User
import com.keneth.realestateapplication.viewModels.UserViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModelUser: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val userDetails = viewModelUser.userProfile.value
    var confirmLogout by remember { mutableStateOf(false) }

    // Default user if userDetails is null
    val user = userDetails ?: User("", "", "", "", "", "")
    val profilePicture by viewModelUser.userProfile
    val profileImage = viewModelUser.userProfile.value?.profileImage ?: ""
    // Fetch user profile when the screen is launched
    LaunchedEffect(Unit) {
        viewModelUser.fetchUserProfile()
    }

    Scaffold(
        topBar = {
            Screen.Profile.title?.let {
                AppTopBar(
                    title = it,
                    onMenuClick = { navController.popBackStack() }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Gradient Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF03F603), Color(0xFF7AEE80))
                        ),
                        shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 50.dp) // Move down by 50dp
                ) {
                    // Green Clip Background
                    Box(
                        modifier = Modifier
                            .size(100.dp) // Same size as the image
                            .clip(CircleShape)
                            .background(Color.Green) // Green background
                    )


                    println("user profile image in profile page :${user.profileImage}")
                    ProfilePicture(
                        profilePicture = profileImage,
                        profileImage = profileImage.toString(),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        navController = navController
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // User Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.firstName.uppercase() + " " + user.lastName.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.phone,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Logout Button
                Button(
                    onClick = { confirmLogout = true },
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "LOG OUT",
                        style = TextStyle(
                            color = Color.White
                        )
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (confirmLogout) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent overlay
                .clickable { confirmLogout = false }, // Dismiss when clicking outside
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Are you sure you want to log out? \nThis will clear your session.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(

                            onClick = { confirmLogout = false },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red, // Text color
                            ),
                            border = ButtonDefaults.outlinedBorder,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                confirmLogout = false // Hide confirmation dialog
                                viewModelUser.logout(context) // Call logout function
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}