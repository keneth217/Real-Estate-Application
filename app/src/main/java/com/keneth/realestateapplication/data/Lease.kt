package com.keneth.realestateapplication.data

data class Lease(
    val uuid: String = "", // Unique identifier for the lease
    val propertyId: String = "", // UUID of the property
    val landlordId: String = "", // UUID of the landlord (from the `User` class)
    val tenantId: String = "", // UUID of the tenant (from the `User` class)
    val startDate: Long = System.currentTimeMillis(), // Lease start date
    val endDate: Long = System.currentTimeMillis(), // Lease end date
    val rentAmount: Double = 0.0, // Monthly rent amount
    val currency: String = "USD", // Currency of the rent
    val securityDeposit: Double = 0.0, // Security deposit amount
    val status: String = "Active" // Lease status (e.g., Active, Expired, Terminated)
)
