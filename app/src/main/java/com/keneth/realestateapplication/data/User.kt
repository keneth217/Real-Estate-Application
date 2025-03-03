package com.keneth.realestateapplication.data

data class User(
    val uuid: String = "", // Unique identifier for the user
    val firstName: String = "", // User's first name
    val lastName: String = "", // User's last name
    val phone: String = "", // User's phone number
    val email: String = "", // User's email address
    val password: String = "", // User's password (should be hashed in production)
    val userRole: RealEstateUserRoles = RealEstateUserRoles.GUEST, // User's role (default: GUEST)
    val profileImage: String = "", // URL to the user's profile image
    val address: USerAddress = USerAddress(), // User's address
    val isEmailVerified: Boolean = false, // Whether the user's email is verified
    val isPhoneVerified: Boolean = false, // Whether the user's phone is verified
    val createdAt: Long = System.currentTimeMillis(), // Timestamp when the user was created
    val updatedAt: Long = System.currentTimeMillis(), // Timestamp when the user was last updated
    val preferences: UserPreferences = UserPreferences(), // User preferences
    val favorites: List<String> = emptyList(),// List of favorite property UUIDs
    val languagePreference: LanguagePreference = LanguagePreference.ENGLISH
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uuid" to uuid,
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "email" to email,
            "password" to password,
            "userRole" to userRole.name,
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
    ENGLISH, KISWAHILI,FRENCH
}

// Address data class for user location
data class USerAddress(
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
