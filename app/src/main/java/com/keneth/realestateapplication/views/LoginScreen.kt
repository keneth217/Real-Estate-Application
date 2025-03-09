package com.keneth.realestateapplication.views

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.keneth.realestateapplication.viewModels.AuthStatus
import com.keneth.realestateapplication.viewModels.UserViewModel
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keneth.realestateapplication.R
import com.keneth.realestateapplication.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authState = viewModel.authState.value
    val isLoading = viewModel.isLoading.value


    val profileFontFamily = FontFamily(
        Font(R.font.darkmode_regular_400, weight = FontWeight.Normal),
        Font(R.font.cluisher_brush, weight = FontWeight.Bold)
    )
    LaunchedEffect(authState) {
        when (authState) {
            is AuthStatus.SuccessWithData -> {
                val token = authState.user?.get("token").toString()
                println("Token from authState: $token")
                if (token.isNotEmpty()) {
                    // Store token
                    UserPreferences.deleteToken(context)
                    UserPreferences.storeToken(context, token)

                    showSuccessSnackbar = true
                    delay(2000) // Show success message before navigating
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                } else {
                    println("Token is empty, not navigating to dashboard")
                }
            }

            is AuthStatus.Error -> {
                errorMessage = authState.message
                showErrorSnackbar = true
            }

            else -> {}
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() }) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = if (showErrorSnackbar) Color.Red else Color.Green
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            //    .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "WELCOME BACK",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontFamily = profileFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color.White, letterSpacing = 5.0.sp
                    )

                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.img10),

                    contentDescription = "Splash Image",
                    modifier = Modifier.size(250.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordField(
                    password = password,
                    onPasswordChange = { password = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.login(email, password) },
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
                    )
                ) {
                    if (isLoading) {
                        Text("Logging In...")
                    }
                    Text("Login")
                }
                // Login Button
                Button(
                    onClick = { viewModel.login(email, password) },
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
                    )
                ) {

                    Text("Login with Google")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign Up Link
                Text(
                    "Don't have an account? Sign up.",
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.SignUp.route)
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Show Snackbars
    if (showErrorSnackbar) {
        LaunchedEffect(showErrorSnackbar) {
            delay(3000)
            showErrorSnackbar = false
        }
        Snackbar(
            modifier = Modifier.padding(8.dp),
            containerColor = Color.Red
        ) {
            Text(errorMessage ?: "Invalid credentials", color = Color.White)
        }
    }

    if (showSuccessSnackbar) {
        LaunchedEffect(showSuccessSnackbar) {
            delay(2000)
            showSuccessSnackbar = false
        }
        Snackbar(
            modifier = Modifier.padding(8.dp),
            containerColor = Color.Green
        ) {
            Text("Login successful!", color = Color.White)
        }
    }
}

