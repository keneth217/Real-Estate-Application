package com.keneth.realestateapplication.data

data class Review(
    val uuid: String = "", // Unique identifier for the review
    val reviewerId: String = "", // UUID of the user who wrote the review
    val reviewedEntityId: String = "", // UUID of the entity being reviewed (property, agent, etc.)
    val rating: Int = 0, // Rating (e.g., 1 to 5 stars)
    val comment: String = "", // Review comment
    val createdAt: Long = System.currentTimeMillis() // Timestamp when the review was created
 )
