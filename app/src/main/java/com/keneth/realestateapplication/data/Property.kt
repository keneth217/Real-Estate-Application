package com.keneth.realestateapplication.data

import android.net.Uri

data class Property(
    val uuid: String = "", // Unique identifier for the property
    val title: String = "", // Title of the property listing
    val description: String = "", // Detailed description of the property
    val price: Double = 0.0, // Price of the property
    val currency: String = "USD", // Currency (e.g., USD, EUR, GBP)
    val propertyType: PropertyType = PropertyType(), // Type of property (e.g., Apartment, House, Villa,one bedroom,tow,three,bedroom)
    val bedrooms: Int = 0, // Number of bedrooms
    val bathrooms: Int = 0, // Number of bathrooms
    val area: Double = 0.0, // Area of the property in square meters/feet
    val areaUnit: String = "sqm", // Unit of area (e.g., sqm, sqft)
    val address: Address = Address(), // Address of the property
    val images: List<String> = emptyList(), // List of image URLs
    val isFeatured: Boolean = false, // Whether the property is featured
    val isListed: Boolean = false,
    val isSold: Boolean = false, // Whether the property is sold
    val listingDate: Long = 0L, // Listing date in milliseconds (epoch time)
    val updatedDate: Long = 0L, // Last updated date in milliseconds
    val amenities: Amenities = Amenities(), // List of amenities (e.g., Pool, Gym, Parking)
    val contactInfo: ContactInfo = ContactInfo() ,// Contact information for the property
    val appointments: List<Appointment> = emptyList() // List of appointments
)

data class Amenities(
    val pool: Boolean = false,
    val parking: Boolean = false,
    val gym: Boolean = false,

    )

// Address data class for property location
data class Address(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = ""
)

// Contact information for the property
data class ContactInfo(
    val name: String = "",
    val phone: String = "",
    val email: String = ""
)

data class PropertyType(
    val uuid: String = "",
    var name: String = "",

    )
