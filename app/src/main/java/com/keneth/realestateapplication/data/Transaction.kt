package com.keneth.realestateapplication.data

data class Transaction(
    val uuid: String = "", // Unique identifier for the transaction
    val propertyId: String = "", // UUID of the property
    val buyerId: String = "", // UUID of the buyer (from the `User` class)
    val sellerId: String = "", // UUID of the seller (from the `User` class)
    val agentId: String = "", // UUID of the agent (from the `Agent` class)
    val transactionType: String = "", // Type of transaction (e.g., Sale, Rent)
    val amount: Double = 0.0, // Transaction amount
    val currency: String = "USD", // Currency of the transaction
    val transactionDate: Long = System.currentTimeMillis(), // Date of the transaction
    val status: String = "Pending" // Status of the transaction (e.g., Pending, Completed, Cancelled)


)
