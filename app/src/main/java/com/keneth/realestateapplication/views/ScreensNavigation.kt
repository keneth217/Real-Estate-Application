package com.keneth.realestateapplication.views

sealed class Screen(
    val route: String,
    val title: String? = null,
    open val icon: Int? = null
) {
    // Authentication Screens
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object SignUp : Screen("signUp", "Sign Up")
    object ForgotPassword : Screen("forgotPassword", "Forgot Password")

    // Main App Screens
    object Dashboard : Screen("dashboard", "Dashboard")
    object PropertyListing : Screen("propertyListing", "Property Listings")
    object PropertyDetails : Screen("propertyDetails/{propertyId}","Property Details") {
        fun createRoute(propertyId: String) = "propertyDetails/$propertyId"
    }


    object AddProperty : Screen("addProperty", "Add Property")
    object Favorites : Screen("favorites", "Favorites")
    object Notifications : Screen("notifications", "Notifications")
    object Profile : Screen("profile", "Profile")
    object Settings : Screen("settings", "Settings")

    // Property Management Screens
    object PropertyCategories : Screen("propertyCategories", "Property Categories")
    object PropertyCategoryListing :
        Screen("propertyCategoryListing/{category}", "Category Listings") {
        fun createRoute(category: String) = "propertyCategoryListing/$category"
    }

    object MyProperties : Screen("myProperties", "My Properties")
    object PropertyStatus : Screen("propertyStatus", "Property Status")
    object PropertyTypes : Screen("propertyTypes", "Property Types")

    // Transaction and Inquiry Screens
    object Transactions : Screen("transactions", "Transactions")
    object Inquiries : Screen("inquiries", "Inquiries")
    object Appointments : Screen("appointments", "Appointments")

    // Reports and Analytics
    object Reports : Screen("reports", "Reports")
    object Analytics : Screen("analytics", "Analytics")

    // Miscellaneous Screens
    object HelpAndSupport : Screen("helpAndSupport", "Help & Support")
    object AboutUs : Screen("aboutUs", "About Us")
    object PrivacyPolicy : Screen("privacyPolicy", "Privacy Policy")
    object TermsAndConditions : Screen("termsAndConditions", "Terms & Conditions")
}