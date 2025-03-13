package com.keneth.realestateapplication.views

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.data.UserAddress
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
    val user = userDetails ?: User(
        uuid = "",
        firstName = "",
        lastName = "",
        phone = "",
        email = "",
        password = "",
        userRole = emptyList(),
        profileImage = "",

        )
    val profilePicture by viewModelUser.userProfile
    val profileImage = viewModelUser.userProfile.value?.profileImage ?: ""
    val profileFontFamily = FontFamily(
        Font(R.font.darkmode_regular_400, weight = FontWeight.Normal),
        Font(R.font.cluisher_brush, weight = FontWeight.Bold)
    )
    println("user details: $user")
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
        }, containerColor = Color.White, contentColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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
                        .offset(y = 50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.firstName.uppercase() + " " + user.lastName.uppercase(),
                    fontFamily = profileFontFamily,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold, letterSpacing = 2.0.sp
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
                Row {
                    Text(
                        text = "P.0 Box -",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = formatAddress(user.address),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        val userId = user.uuid
                        if (userId != null) {
                            navController.navigate("${Screen.UpdateProfileScreen.route}/$userId")
                        } else {
                            println("Error: User ID is null")
                        }
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Icon",
                            tint = Color.White
                        )
                        Text(
                            text = "Update Details",
                            style = TextStyle(
                                color = Color.White
                            )
                        )
                    }
                }

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

    // Logout Confirmation Dialo
    if (confirmLogout) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { confirmLogout = false },
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
                        text = "Are you sure you want to log out?" +
                                " \nThis will clear your session.",
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


fun formatAddress(address: UserAddress?): String {
    return "${address?.postalCode ?: "N/A"}, " +
            "${address?.street ?: "Unknown Street"}, " +
            "${address?.city ?: "Unknown City"}-" +
            "${address?.country ?: "Unknown Country"}"

}
