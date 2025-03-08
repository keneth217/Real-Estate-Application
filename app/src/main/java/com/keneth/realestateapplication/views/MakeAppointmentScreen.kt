package com.keneth.realestateapplication.views

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keneth.realestateapplication.data.Appointment
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeAppointmentScreen(
    propertyId: String,
    navController: NavController,
    viewModel: PropertyViewModel,
    userId: String
) {
    val property = viewModel.propertyDetails.value
    val context = LocalContext.current

    var dateTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var status by remember { mutableStateOf("Scheduled") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var warningMessage by remember { mutableStateOf<String?>(null) }

    val existingAppointments = viewModel.appointments.value

    LaunchedEffect(propertyId) {
        viewModel.fetchPropertyById(propertyId)
        viewModel.fetchAppointmentsForUser(userId)
    }

    // Show Success Dialog
    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { successMessage = null },
            title = { Text("Success") },
            text = { Text(successMessage!!) },
            confirmButton = {
                Button(
                    onClick = {
                        successMessage = null
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Show Error Dialog
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }

    // Show Warning Dialog
    if (warningMessage != null) {
        AlertDialog(
            onDismissRequest = { warningMessage = null },
            title = { Text("Warning") },
            text = { Text(warningMessage!!) },
            confirmButton = {
                Button(onClick = { warningMessage = null }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Make Appointment",
                onMenuClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            property?.let {
                Text(
                    text = it.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            val formattedDate = remember(dateTime) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateTime))
            }
            val formattedTime = remember(dateTime) {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(dateTime))
            }

            // Date Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = formattedDate,
                    onValueChange = {},
                    label = { Text("Select Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Time Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { showTimePicker = true }
            ) {
                OutlinedTextField(
                    value = formattedTime,
                    onValueChange = {},
                    label = { Text("Select Time") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Status Input
            OutlinedTextField(
                value = status,
                onValueChange = { status = it },
                label = { Text("Status") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // Submit Button
            Button(
                onClick = {
                    val hasExistingAppointment = existingAppointments.any { it.propertyId == propertyId }

                    if (hasExistingAppointment) {
                        warningMessage = "You have already scheduled an appointment for this property."
                        return@Button
                    }

                    val appointment = Appointment(
                        propertyId = propertyId,
                        userId = userId,
                        dateTime = dateTime,
                        status = status
                    )

                    viewModel.makeAppointment(propertyId, appointment) { success, message ->
                        if (success) {
                            successMessage = "Your appointment has been successfully scheduled!"
                        } else {
                            errorMessage = message ?: "Failed to schedule appointment. Please try again."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Schedule Appointment")
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = selectedDate

                            // Preserve the current time while updating the date
                            val timeCalendar = Calendar.getInstance().apply {
                                timeInMillis = dateTime
                            }
                            calendar.set(
                                Calendar.HOUR_OF_DAY,
                                timeCalendar.get(Calendar.HOUR_OF_DAY)
                            )
                            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))

                            dateTime = calendar.timeInMillis
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val calendar = Calendar.getInstance().apply { timeInMillis = dateTime }
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                dateTime = calendar.timeInMillis
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}
