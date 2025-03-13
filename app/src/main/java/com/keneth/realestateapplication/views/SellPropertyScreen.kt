package com.keneth.realestateapplication.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keneth.realestateapplication.viewModels.PropertyViewModel

@Composable
fun SellPropertyScreen(
    propertyId: String,
    navController: NavController,

    viewModel: PropertyViewModel
) {
    // Collect property details
    val property = viewModel.propertyDetails.value

    LaunchedEffect(propertyId) {
        viewModel.fetchPropertyById(propertyId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mark as Sold",
                onMenuClick = { navController.popBackStack() }
            )
        }, containerColor = Color.White, contentColor = Color.Black,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Form to mark the property as sold
            // ...
        }
    }
}