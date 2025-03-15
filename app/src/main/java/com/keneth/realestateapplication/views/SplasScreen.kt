package com.keneth.realestateapplication.views

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.UserPreferences

@Composable
fun SplashScreen(navController: NavController, context: Context) {
    val scope = rememberCoroutineScope()
    var isCheckingToken by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(pageCount = { 4 }) // 4 pages

    val pages = listOf(
        SplashPage(
            R.drawable.img2, // Replace with your real estate-themed image
            "Welcome to RealEstatePro!",
            "Welcome to RealEstatePro! Your ultimate platform for buying, selling, and managing properties. Discover your dream home, list properties, and stay ahead in the real estate market with ease."
        ),
        SplashPage(
            R.drawable.img3, // Replace with your real estate-themed image
            "Find Your Dream Property",
            "Explore a wide range of properties tailored to your needs. Whether you're looking for a cozy apartment, a luxurious villa, or a commercial space, we’ve got you covered. Start your journey today!"
        ),
        SplashPage(
            R.drawable.img10, // Replace with your real estate-themed image
            "List and Manage Properties Effortlessly",
            "Sell or rent your properties with ease. Our platform allows you to list properties, manage inquiries, and track performance—all in one place. Maximize your property’s potential with RealEstatePro."
        ),
        SplashPage(
            R.drawable.img12, // Replace with your real estate-themed image
            "Stay Ahead in the Real Estate Market",
            "Get real-time market insights, trends, and analytics to make informed decisions. Whether you're a buyer, seller, or investor, RealEstatePro helps you stay ahead in the competitive real estate market."
        )
    )

    // Auto-scroll pages every 5 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // Check authentication token and user role
    LaunchedEffect(Unit) {
        scope.launch {
            val token = UserPreferences.getToken(context)
            val userRole = UserPreferences.getUserRole(context) // Retrieve user role
            println("Retrieved token from preferences: $token")
            println("Retrieved user role from preferences: $userRole")

            if (token != null) {
                val isValid = isTokenValidFirebase(token)
                println("Token validation result: $isValid")

                if (isValid && userRole != null) {
                    println("Token is valid, navigating to role-specific screen")
                    navigateToRoleScreen(navController, userRole) // Navigate based on role
                } else {
                    println("Token is invalid or role is missing, showing onboarding")
                    isCheckingToken = false
                }
            } else {
                println("No token found, showing onboarding")
                isCheckingToken = false
            }
        }
    }

    if (isCheckingToken) return // Wait for authentication check

    // UI Layout
    Scaffold(
        containerColor = Color.White, contentColor = Color.Black,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (pagerState.currentPage < pages.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            text = "SKIP",
                            fontSize = 16.sp,
                            color = Color.Blue,
                            modifier = Modifier.clickable {
                                println("Skip clicked, navigating to Login")
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page -> SplashPageUI(splashPage = pages[page]) }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(pages.size) { index ->
                        Indicator(isSelected = pagerState.currentPage == index)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (pagerState.currentPage == pages.lastIndex) {
                    Button(
                        onClick = {
                            println("Get Started clicked, navigating to Login")
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(bottom = 32.dp),
                        shape = RoundedCornerShape(4.dp), // Rounded corners
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5CEC57), // Green background
                            contentColor = Color.White // Text color
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                            hoveredElevation = 8.dp
                        )
                    ) {
                        Text("Get Started")
                    }
                }
            }
        }
    }
}

// Navigate to the appropriate screen based on the user's role
private fun navigateToRoleScreen(navController: NavController, userRole: String) {
    println("Retrieved user role from preferences: $userRole") // Log the raw role

    val cleanedRole = userRole.replace("[", "").replace("]", "").uppercase()
    println("Cleaned user role: $cleanedRole") // Log the cleaned role
    println("Navigating to role-specific screen for role: $cleanedRole")

    when (cleanedRole) { // Use cleanedRole instead of userRole
        "ADMIN" -> navController.navigate(Screen.Dashboard.route)
        "AGENT" -> navController.navigate(Screen.AgentDashboard.route)
        "BUYER" -> navController.navigate(Screen.BuyerDashboard.route)
        "SELLER" -> navController.navigate(Screen.SellerDashboard.route)
        "LANDLORD" -> navController.navigate(Screen.LandLordDashboard.route)
        "TENANT" -> navController.navigate(Screen.TenantDashboard.route)
        "GUEST" -> navController.navigate(Screen.GuestDashboard.route)
        else -> {
            println("Unknown role: $cleanedRole, navigating to Login")
            navController.navigate(Screen.Login.route) // Default fallback
        }
    }
}


suspend fun isTokenValidFirebase(token: String): Boolean {
    return try {
        val idTokenResult = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()
        val isValid = idTokenResult?.token != null
        println("Token validation result: $isValid")
        isValid
    } catch (e: Exception) {
        println("Failed to validate token: ${e.message}")
        false
    }
}

@Composable
fun SplashPageUI(splashPage: SplashPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Image(
            painter = painterResource(id = splashPage.imageRes),
            contentDescription = "Splash Image",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = splashPage.title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            color = Color.Green,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = splashPage.desc,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(if (isSelected) 12.dp else 8.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color.Green else Color.Gray)
    )
}

data class SplashPage(val imageRes: Int, val title: String, val desc: String)