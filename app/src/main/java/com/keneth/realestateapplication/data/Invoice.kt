package com.keneth.realestateapplication.data

import java.util.Date

data class Invoice(
    val id: String = "", // Auto-generated Firestore ID
    val propertyId: String, // ID of the property
    val tenantId: String, // ID of the tenant
    val landlordId: String, // ID of the landlord
    val amount: Double, // Total amount due
    val dueDate: Date, // Due date for the invoice
    val dateIssued: Date = Date(), // Date the invoice was issued
    val status: InvoiceStatus = InvoiceStatus.PENDING, // Status of the invoice
    val description: String = "Monthly Rent" // Description of the invoice
)

// Enum for invoice status
enum class InvoiceStatus {
    PENDING, PAID, OVERDUE, CANCELLED
}