import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.keneth.realestateapplication.data.LanguagePreference
import com.keneth.realestateapplication.data.RealEstateUserRoles
import com.keneth.realestateapplication.data.USerAddress
import com.keneth.realestateapplication.data.User
import com.keneth.realestateapplication.data.UserPreferences
import com.keneth.realestateapplication.enum.RegistrationStep
import com.keneth.realestateapplication.viewModels.UserViewModel
import java.util.UUID

class MultiStepFormViewModel(
    private val userViewModel: UserViewModel
) : ViewModel() {
    var currentStep by mutableStateOf(RegistrationStep.EMAIL_PASSWORD)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf(USerAddress())
    var profilePicture by mutableStateOf<Uri?>(null)
    var selectedRoles by mutableStateOf(setOf<RealEstateUserRoles>())
    var isEmailVerified by mutableStateOf(false)
    var isPhoneVerified by mutableStateOf(false)
    var createdAt by mutableStateOf(System.currentTimeMillis())
    var updatedAt by mutableStateOf(System.currentTimeMillis())
    var preferences by mutableStateOf(UserPreferences())
    var favorites by mutableStateOf<List<String>>(emptyList())
    var languagePreference by mutableStateOf(LanguagePreference.ENGLISH)
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun toggleRoleSelection(role: RealEstateUserRoles) {
        selectedRoles = if (selectedRoles.contains(role)) {
            selectedRoles - role
        } else {
            selectedRoles + role
        }
    }

    fun isEmailPasswordStepValid(): Boolean {
        return email.isNotBlank() && password.isNotBlank()
    }

    fun isPersonalDetailsStepValid(): Boolean {
        return firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
    }

    fun nextStep() {
        when (currentStep) {
            RegistrationStep.EMAIL_PASSWORD -> {
                if (isEmailPasswordStepValid()) {
                    currentStep = RegistrationStep.PERSONAL_DETAILS
                }
            }

            RegistrationStep.PERSONAL_DETAILS -> {
                if (isPersonalDetailsStepValid()) {
                    currentStep = RegistrationStep.USER_ROLES
                }
            }

            RegistrationStep.USER_ROLES -> {
                if (selectedRoles.isNotEmpty()) {
                    currentStep = RegistrationStep.ADDRESS
                } else {
                    errorMessage = "Please select at least one role."
                }
            }

            RegistrationStep.ADDRESS -> currentStep = RegistrationStep.PROFILE_PICTURE
            RegistrationStep.PROFILE_PICTURE -> currentStep = RegistrationStep.REVIEW
            RegistrationStep.REVIEW -> {}
        }
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            RegistrationStep.EMAIL_PASSWORD -> RegistrationStep.EMAIL_PASSWORD
            RegistrationStep.PERSONAL_DETAILS -> RegistrationStep.EMAIL_PASSWORD
            RegistrationStep.USER_ROLES -> RegistrationStep.PERSONAL_DETAILS
            RegistrationStep.ADDRESS -> RegistrationStep.USER_ROLES
            RegistrationStep.PROFILE_PICTURE -> RegistrationStep.ADDRESS
            RegistrationStep.REVIEW -> RegistrationStep.PROFILE_PICTURE
        }
    }

    fun skipStep() {
        currentStep = when (currentStep) {
            RegistrationStep.ADDRESS -> RegistrationStep.PROFILE_PICTURE
            RegistrationStep.PROFILE_PICTURE -> RegistrationStep.REVIEW
            else -> currentStep
        }
    }

    fun submitForm(onSuccess: () -> Unit) {
        if (!validateForm()) {
            errorMessage = "Please fill out all required fields."
            return
        }

        if (selectedRoles.isEmpty()) {
            errorMessage = "Please select at least one role."
            return
        }

        isLoading = true
        errorMessage = null

        try {
            val user = User(
                uuid = UUID.randomUUID().toString(),
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                email = email,
                password = password,
                userRole = selectedRoles,
                profileImage = profilePicture?.toString() ?: "",
                address = address,
                isEmailVerified = isEmailVerified,
                isPhoneVerified = isPhoneVerified,
                createdAt = createdAt,
                updatedAt = updatedAt,
                preferences = preferences,
                favorites = favorites,
                languagePreference = languagePreference
            )

            userViewModel.signUp(email, password, user.toMap(), profilePicture!!)
            isSuccess = true
            println("Registration success: $user,$profilePicture",)
        } catch (e: Exception) {
            errorMessage = "Failed to register user: ${e.message}"
            isSuccess = false
        } finally {
            isLoading = false
        }
    }

    private fun validateForm(): Boolean {
        return email.isNotBlank() &&
                password.isNotBlank() &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                phone.isNotBlank() &&
                profilePicture != null
    }
}