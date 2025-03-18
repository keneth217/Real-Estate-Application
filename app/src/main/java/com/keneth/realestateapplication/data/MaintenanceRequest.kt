package com.keneth.realestateapplication.data

import java.util.Date

data class MaintenanceRequest(
    val id: String = "", // Auto-generated Firestore ID
    val propertyId: String, // ID of the property
    val tenantId: String, // ID of the tenant submitting the request
    val title: String, // Short title/description of the issue
    val description: String, // Detailed description of the issue
    val dateSubmitted: Date = Date(), // Date the request was submitted
    val status: MaintenanceStatus = MaintenanceStatus.PENDING, // Status of the request
    val priority: MaintenancePriority = MaintenancePriority.MEDIUM // Priority of the request
)

// Enum for maintenance request status
enum class MaintenanceStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}

// Enum for maintenance request priority
enum class MaintenancePriority {
    LOW, MEDIUM, HIGH, URGENT
}
