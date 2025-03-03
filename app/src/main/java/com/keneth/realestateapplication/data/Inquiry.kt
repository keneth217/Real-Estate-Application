package com.keneth.realestateapplication.data

data class Inquiry(
    val uuid: String = "", // Unique identifier for the inquiry
    val propertyId: String = "", // UUID of the property
    val userId: String = "", // UUID of the user making the inquiry
    val message: String = "", // Inquiry message
    val createdAt: Long = System.currentTimeMillis(), // Timestamp when the inquiry was created
    val status: String = "Open" // Inquiry status (e.g., Open, Resolved)
)
