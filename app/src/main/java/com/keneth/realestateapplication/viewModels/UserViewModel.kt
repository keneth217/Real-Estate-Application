package com.keneth.realestateapplication.viewModels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keneth.realestateapplication.data.User
import com.keneth.realestateapplication.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // Auth State
    private val _authState = mutableStateOf<AuthStatus>(AuthStatus.LoggedOut)
    val authState: State<AuthStatus> get() = _authState

    // User Profile
    private val _userProfile = mutableStateOf<User?>(null)
    val userProfile: State<User?> get() = _userProfile

    // User First Name
    private val _userFirstName = mutableStateOf<String?>("User")
    val userFirstName: State<String?> get() = _userFirstName

    // Loading State
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    init {
        fetchUserFirstName()
        fetchUserProfile()
    }

    // Fetch the user's first name
    private fun fetchUserFirstName() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null) {
                    val userData = repository.getUserById(userId)
                    _userFirstName.value = userData?.get("firstName")?.toString()?.uppercase()
                }
            } catch (e: Exception) {
                _userFirstName.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch the user's full profile
    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null) {
                    _userProfile.value = repository.getUserProfile(userId)
                }
            } catch (e: Exception) {
                _userProfile.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Sign up a new user
    fun signUp(email: String, password: String, userData: Map<String, Any>) {
        viewModelScope.launch {
            _authState.value = AuthStatus.Loading
            try {
                val success = repository.signUp(email, password, userData)
                _authState.value =
                    if (success) AuthStatus.Success else AuthStatus.Error("Sign-up failed")
            } catch (e: Exception) {
                _authState.value = AuthStatus.Error("Sign-up failed: ${e.message}")
            }
        }
    }

    // Log in an existing user
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthStatus.Loading
            try {
                val user = repository.login(email, password)
                if (user != null) {
                    _authState.value = AuthStatus.SuccessWithData(user)
                } else {
                    _authState.value = AuthStatus.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthStatus.Error("Login failed: ${e.message}")
            }
        }
    }

    // Log out the current user
    fun logout(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthStatus.Loading
            try {
                repository.logout(context)
                _authState.value = AuthStatus.LoggedOut
                _userProfile.value = null
                _userFirstName.value = null
            } catch (e: Exception) {
                _authState.value = AuthStatus.Error("Logout failed: ${e.message}")
            }
        }
    }
}

// Sealed class for authentication results
sealed class AuthStatus {
    object Loading : AuthStatus()
    object Success : AuthStatus()
    object LoggedOut : AuthStatus()
    data class SuccessWithData(val user: Map<String, Any>?) : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}