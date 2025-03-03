package com.keneth.realestateapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.keneth.realestateapplication.modelProvider.PropertyViewModelFactory
import com.keneth.realestateapplication.modelProvider.UserViewModelFactory
import com.keneth.realestateapplication.repository.PropertyRepository
import com.keneth.realestateapplication.repository.UserRepository
import com.keneth.realestateapplication.ui.theme.RealEstateApplicationTheme
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel
import com.keneth.realestateapplication.views.NavigationGraph
import com.keneth.realestateapplication.views.SplashScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()
        val auth = FirebaseAuth.getInstance()

        // Create repository
        val userRepository = UserRepository(firestore, storage, auth)
        val propertyRepository = PropertyRepository(firestore, storage, auth)

        // Create ViewModels using ViewModelProvider
        val userViewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }
        val propertyViewModel: PropertyViewModel by viewModels {
            PropertyViewModelFactory(
                propertyRepository
            )
        }

        enableEdgeToEdge()
        setContent {
            RealEstateApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationGraph(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = propertyViewModel,
                        viewModelUser = userViewModel,
                    )
                }
            }
        }
    }
}




