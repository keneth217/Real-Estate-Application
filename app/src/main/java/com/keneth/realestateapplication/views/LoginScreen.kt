package com.keneth.realestateapplication.views

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.UserPreferences
import com.keneth.realestateapplication.viewModels.AuthStatus
import com.keneth.realestateapplication.viewModels.UserViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    val authState = viewModel.authState.value
    val profileFontFamily = FontFamily(
        Font(R.font.darkmode_regular_400, weight = FontWeight.Normal),
        Font(R.font.cluisher_brush, weight = FontWeight.Bold)
    )

    LaunchedEffect(authState) {
        println("AuthState changed: $authState")
        when (authState) {
            is AuthStatus.SuccessWithData -> {
                val token = authState.user?.get("token").toString()
                val role = authState.user?.get("userRole").toString()
                val cleanedRole = role.replace("[", "").replace("]", "")

                println("Token: $token, Role: $cleanedRole")

                if (token.isNotEmpty() && cleanedRole.isNotEmpty()) {
                    UserPreferences.deleteToken(context)
                    UserPreferences.storeToken(context, token)
                    UserPreferences.deleteUserRole(context)
                    UserPreferences.storeUserRole(context, cleanedRole)

                    message = "Login successful!, Redirecting ....."
                    isSuccess = true
                    showMessage = true

                    println("Login successful! Navigating to $cleanedRole dashboard...")
                    delay(2000)

                    when (cleanedRole) {
                        "ADMIN" -> navController.navigate(Screen.Dashboard.route)
                        "AGENT" -> navController.navigate(Screen.AgentDashboard.route)
                        "BUYER" -> navController.navigate(Screen.BuyerDashboard.route)
                        "SELLER" -> navController.navigate(Screen.SellerDashboard.route)
                        "LANDLORD" -> navController.navigate(Screen.LandLordDashboard.route)
                        "TENANT" -> navController.navigate(Screen.TenantDashboard.route)
                        "GUEST" -> navController.navigate(Screen.GuestDashboard.route)
                        else -> {
                            UserPreferences.deleteToken(context)
                            UserPreferences.deleteUserRole(context)
                            message = "Invalid login details. Please log in again."
                            isSuccess = false
                            showMessage = true
                            println("Invalid role: $cleanedRole")
                            navController.navigate(Screen.Login.route)
                        }
                    }
                } else {
                    UserPreferences.deleteToken(context)
                    UserPreferences.deleteUserRole(context)
                    message = "Invalid Login details. Please log in again."
                    isSuccess = false
                    showMessage = true
                    println("Token or role is empty")
                }
            }

            is AuthStatus.Error -> {
                message = authState.message
                isSuccess = false
                showMessage = true
                println("Login error: ${authState.message}")
            }

            else -> {
                println("AuthState: $authState")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            contentColor = Color.Black,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "WELCOME BACK",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontFamily = profileFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color.Green,
                        letterSpacing = 5.0.sp
                    )
                )
                Spacer(modifier = Modifier.height(50.dp))
                Image(
                    painter = painterResource(id = R.drawable.img10),
                    contentDescription = "Splash Image",
                    modifier = Modifier.size(250.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Display the message above the input fields
                message?.let {
                    Text(
                        text = it,
                        color = if (isSuccess) Color.Green else Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        println("Login button clicked")
                        viewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFCB35D5),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp,
                        hoveredElevation = 8.dp
                    ),
                    enabled = authState != AuthStatus.Loading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (authState == AuthStatus.Loading) {
                            CircularProgressIndicator(
                                color = Color.Green,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logging In...", color = Color.Green)
                        } else {
                            Text("Login")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Don't have an account? Sign up.",
                    modifier = Modifier.clickable {
                        println("Navigating to SignUp screen")
                        navController.navigate(Screen.SignUp.route)
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Show message card on top of the Scaffold
        if (showMessage) {
            LaunchedEffect(Unit) {
                delay(5000)
                showMessage = false
                println("Message dialog dismissed")
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccess) Color.Green else Color.Red
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (isSuccess) R.drawable.correct else R.drawable.error2
                            ),
                            contentDescription = if (isSuccess) "Success Icon" else "Failure Icon",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message ?: "",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}