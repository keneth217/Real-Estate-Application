package com.keneth.realestateapplication.views

import com.keneth.realestateapplication.viewModels.MultiStepFormViewModel
import MyPropertiesScreen
import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keneth.realestateapplication.viewModels.AddPropertyViewModel
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    viewModel: PropertyViewModel,
    viewModelUser: UserViewModel,
    context: Context
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // Splash Screen (outside Scaffold)
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController, context)
        }

        // Authentication Screens
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModelUser)
        }
//        composable(Screen.SignUp.route) {
//            SignUpScreen(navController = navController, viewModelUser)
//        }

        composable(Screen.SignUp.route) {

            val multiStepFormViewModel: MultiStepFormViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MultiStepFormViewModel(viewModelUser) as T
                    }
                }
            )
            MultiStepForm(
                navController = navController,
                userViewModel = viewModelUser,
                multiStepFormViewModel = multiStepFormViewModel
            )
        }

        composable(Screen.AddProperty.route) {
            //val propertyViewModel: PropertyViewModel = viewModel()
            val multiStepPropertyFormViewModel: AddPropertyViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AddPropertyViewModel(viewModel) as T
                    }
                }
            )

            AddPropertyForm(
                navController = navController,
                propertyViewModel = viewModel,
                multiStepFormPropertyViewModel = multiStepPropertyFormViewModel
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController, viewModelUser)
        }

        composable(Screen.PropertyTypes.route) {
            PropertyTypeScreen(navController = navController, viewModel)
        }

        // Main App Screens (inside Scaffold)
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController, viewModel, viewModelUser, context)
        }
        composable(Screen.PropertyListing.route) {
            PropertyListingScreen(
                navController = navController, viewModel,
            )
        }
        composable(
            route = Screen.PropertyDetails.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailsScreen(
                propertyId = propertyId,
                navController = navController,
                viewModel,
            )
        }

        composable(
            route = Screen.UserDetails.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailsScreen(
                userId = userId,
                navController = navController,
                userViewModel = viewModelUser,
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController, viewModel)
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, viewModelUser)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        // Property Management Screens
        composable(Screen.PropertyCategories.route) {
            PropertyCategoriesScreen(navController = navController, viewModel)
        }
        composable(
            route = Screen.PropertyCategoryListing.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            PropertyCategoryListingScreen(
                category = category,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(Screen.MyProperties.route) {
            MyPropertiesScreen(navController = navController, viewModel)
        }
        composable(Screen.PropertyStatus.route) {
            PropertyStatusScreen(navController = navController, viewModel)
        }

        // Transaction and Inquiry Screens
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController = navController, viewModel)
        }
        composable(Screen.Inquiries.route) {
            InquiriesScreen(navController = navController, viewModel)
        }

        composable(Screen.UsersManagementScreen.route) {
            UsersManagementScreen(navController = navController, viewModelUser)
        }
        composable(Screen.Appointments.route) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            val userId = backStackEntry.arguments?.getString("userId")
            AppointmentsScreen(
                navController = navController,
                viewModel = viewModel,
                propertyId = propertyId,
                userId = userId
            )
        }

        // Reports and Analytics
        composable(Screen.Reports.route) {
            ReportsScreen(navController = navController, viewModel)
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(navController = navController, viewModel)
        }

        // Miscellaneous Screens
        composable(Screen.HelpAndSupport.route) {
            HelpAndSupportScreen(navController = navController)
        }
        composable(Screen.AboutUs.route) {
            AboutUsScreen(navController = navController)
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }
        composable(Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(navController = navController)
        }

        composable(
            route = Screen.MakeAppointmentScreen.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            viewModelUser.userProfile.value?.let {
                MakeAppointmentScreen(
                    propertyId = propertyId,
                    navController = navController,
                    viewModel,
                    userId = it.uuid,
                )
            }
        }
        composable(
            route = Screen.SellPropertyScreen.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            SellPropertyScreen(
                propertyId = propertyId,
                navController = navController,
                viewModel,
            )
        }
        composable(
            route = Screen.ListPropertyScreen.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            ListPropertyScreen(
                propertyId = propertyId,
                navController = navController,
                viewModel,
            )
        }

        composable(
            "${Screen.UpdateProfileScreen.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UpdateProfileScreen(
                    navController, userId,
                    viewModelUser
                )
            } else {
                // Handle case where userId is null (e.g., show an error message)
                Text("Error: User ID is missing")
            }
        }
        composable(Screen.BuyerDashboard.route) {
            BuyerDashboardScreen(navController = navController)
        }
        composable(Screen.SellerDashboard.route) {
            SelleDashboardScreen(navController = navController)
        }
        composable(Screen.AgentDashboard.route) {
            AgentDashboardScreen(navController = navController, viewModelUser)
        }
        composable(Screen.LandLordDashboard.route) {
            LandLordDashboardScreen(navController = navController,viewModelUser)
        }
        composable(Screen.GuestDashboard.route) {
            GuestDashboardScreen(navController = navController)
        }

        composable(Screen.TenantDashboard.route) {
            TenantDashboardScreen(navController = navController, viewModelUser)
        }
    }
}







































