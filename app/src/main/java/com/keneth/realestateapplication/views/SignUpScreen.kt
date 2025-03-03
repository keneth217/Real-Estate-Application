package com.keneth.realestateapplication.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keneth.realestateapplication.viewModels.AuthStatus
import com.keneth.realestateapplication.viewModels.UserViewModel

@Composable
fun SignUpScreen(navController: NavController, viewModelUser: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val authState = viewModelUser.authState.value
    //  var errorMessage by remember { mutableStateOf<String?>(null) }
    val errorMessage = (authState as? AuthStatus.Error)?.message

    var showErrorSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthStatus.Success, is AuthStatus.SuccessWithData -> {
                navController.navigate(Screen.Login.route)
            }
            is AuthStatus.Error -> {
                showErrorSnackbar = true
            }
            else -> {} // Handle other cases if necessary
        }
    }
    LaunchedEffect(authState) {
        if (authState is AuthStatus.Error) {
            showErrorSnackbar = true
        }
    }


    if (showErrorSnackbar) {
        Snackbar(
            modifier = Modifier.padding(8.dp),
            containerColor = Color.Red, // Red background for errors
            action = {
                Button(
                    onClick = { showErrorSnackbar = false },
                    colors = ButtonDefaults.buttonColors(contentColor = Color.White)
                ) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(errorMessage ?: "", color = Color.White)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Button(
            onClick = {
                val userData = mapOf(
                    "email" to email,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "password" to password
                )
                viewModelUser.signUp(email, password, userData)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(4.dp), // Rounded corners
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCE3EDE), // Green background
                contentColor = Color.White // Text color
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp,
                hoveredElevation = 8.dp
            )
        ) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Already have an account? Sign in.",
            modifier = Modifier.clickable {
                navController.navigate(Screen.Login.route)
            },
            color = MaterialTheme.colorScheme.scrim,
            style = MaterialTheme.typography.bodyLarge
        )
    }



}