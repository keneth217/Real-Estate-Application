package com.keneth.realestateapplication.data

data class Appointment(
    val uuid: String = "", // Unique identifier for the appointment
    val propertyId: String = "", // UUID of the property
    val userId: String = "", // UUID of the user scheduling the appointment
    val agentId: String = "", // UUID of the agent (if applicable)
    val dateTime: Long = System.currentTimeMillis(), // Date and time of the appointment
    val status: String = "Scheduled" // Appointment status (e.g., Scheduled, Completed, Cancelled)
)
