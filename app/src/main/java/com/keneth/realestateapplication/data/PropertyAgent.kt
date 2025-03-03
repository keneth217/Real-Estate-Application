package com.keneth.realestateapplication.data

data class PropertyAgent(
    val uuid: String = "", // Unique identifier for the agent
    val userId: String = "", // Reference to the user (from the `User` class)
    val licenseNumber: String = "", // Agent's license number
    val agencyName: String = "", // Name of the agency the agent works for
    val yearsOfExperience: Int = 0, // Years of experience
    val rating: Double = 0.0, // Average rating from reviews
    val propertiesListed: List<String> = emptyList() // List of property UUIDs listed by the agent
)
