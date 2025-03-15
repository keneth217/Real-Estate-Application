package com.keneth.realestateapplication.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.keneth.realestateapplication.viewModels.PropertyViewModel

@Composable
fun ReportsScreen(navController: NavController, viewModel: PropertyViewModel) {
    Scaffold(
        topBar = {
            Screen.Reports.title?.let {
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
            Text(
                text = "Reports Page"
            )

        }
    }
}