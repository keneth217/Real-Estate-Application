package com.keneth.realestateapplication.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.keneth.realestateapplication.data.Appointment
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppointmentsScreen(
    navController: NavHostController,
    viewModel: PropertyViewModel,
    propertyId: String? = null, // Make propertyId nullable
    userId: String? = null // Make userId nullable
) {
    val allAppointments = viewModel.allAppointments.value
    val userAppointments = viewModel.userAppointments.value
    val propertyAppointments = viewModel.propertyAppointments.value

    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value

    // Fetch data when the screen is launched
    LaunchedEffect(propertyId, userId) {
        if (userId != null) {
            viewModel.fetchAppointmentsForUser(userId)
        } else if (propertyId != null) {
            viewModel.fetchAppointmentsForProperty(propertyId)
        } else {
            viewModel.fetchAllAppointments()
        }
    }

    // Show loading indicator
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Show error message
    if (errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    // Display appointments
    val appointmentsToShow = when {
        userId != null -> userAppointments
        propertyId != null -> propertyAppointments
        else -> allAppointments
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(appointmentsToShow) { appointment ->
            AppointmentItem(appointment = appointment)
        }
    }
}

@Composable
fun AppointmentItem(appointment: Appointment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Appointment ID: ${appointment.uuid}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Property ID: ${appointment.propertyId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "User ID: ${appointment.userId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Date: ${
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                        Date(appointment.dateTime)
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Status: ${appointment.status}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}