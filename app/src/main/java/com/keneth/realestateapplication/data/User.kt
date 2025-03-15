package com.keneth.realestateapplication.data

data class User(
    val uuid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val userRole: List<RealEstateUserRoles> = emptyList(),
    val profileImage: String = "",
    val address: UserAddress = UserAddress(),
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences(),
    val favorites: List<String> = emptyList(),
    val languagePreference: LanguagePreference = LanguagePreference.ENGLISH
) {
    // Secondary constructor with no arguments
    constructor() : this(
        uuid = "",
        firstName = "",
        lastName = "",
        phone = "",
        email = "",
        password = "",
        userRole = emptyList(),
        profileImage = "",
        address = UserAddress(),
        isEmailVerified = false,
        isPhoneVerified = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        preferences = UserPreferences(),
        favorites = emptyList(),
        languagePreference = LanguagePreference.ENGLISH
    )

    fun toMap(): Map<String, Any> {
        return mapOf(
            "uuid" to uuid,
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "email" to email,
            "password" to password,
            "userRole" to userRole.map { it.name },
            "profileImage" to profileImage,
            "address" to mapOf(
                "street" to address.street,
                "city" to address.city,
                "state" to address.state,
                "postalCode" to address.postalCode,
                "country" to address.country
            ),
            "isEmailVerified" to isEmailVerified,
            "isPhoneVerified" to isPhoneVerified,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "preferences" to mapOf(
                "notificationEnabled" to preferences.notificationEnabled,
                "preferredPropertyTypes" to preferences.preferredPropertyTypes,
                "budgetRange" to mapOf(
                    "min" to preferences.budgetRange.min,
                    "max" to preferences.budgetRange.max
                ),
                "preferredLocations" to preferences.preferredLocations
            ),
            "favorites" to favorites,
            "languagePreference" to languagePreference.name
        )
    }
}


enum class LanguagePreference {
    ENGLISH, KISWAHILI, FRENCH
}

// Address data class for user location
data class UserAddress(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = ""
)

// User preferences data class
data class UserPreferences(
    val notificationEnabled: Boolean = true, // Whether notifications are enabled
    val preferredPropertyTypes: List<String> = emptyList(), // Preferred property types (e.g., Apartment, House)
    val budgetRange: BudgetRange = BudgetRange(), // User's budget range
    val preferredLocations: List<String> = emptyList() // Preferred locations (e.g., City names)
)

// Budget range data class
data class BudgetRange(
    val min: Double = 0.0, // Minimum budget
    val max: Double = 0.0 // Maximum budget
)

// Enum for user roles
enum class RealEstateUserRoles {
    ADMIN,
    AGENT,
    BUYER,
    SELLER,
    LANDLORD,
    TENANT,
    INVESTOR,
    GUEST,
    MODERATOR,
    ANALYST
}
