package com.keneth.realestateapplication.viewModels

import android.content.Context
import android.net.Uri
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



    private val _authState = mutableStateOf<AuthStatus>(AuthStatus.LoggedOut)
    val authState: State<AuthStatus> get() = _authState

    private val _userProfile = mutableStateOf<User?>(null)
    val userProfile: State<User?> get() = _userProfile


    private val _userFirstName = mutableStateOf<String?>("User")
    val userFirstName: State<String?> get() = _userFirstName


    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    init {
        fetchUserProfile()
    }





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




    fun signUp(email: String, password: String, userData: Map<String, Any>, imageUri: Uri) {
        viewModelScope.launch {
            _authState.value = AuthStatus.Loading
            try {
                val success = repository.signUp(email, password, userData, imageUri)
                if (success) {
                    _authState.value = AuthStatus.Success
                } else {
                    _authState.value = AuthStatus.Error("Sign-up failed. Please try again.")
                }
            } catch (e: Exception) {
                // Set the exact error message
                _authState.value = AuthStatus.Error("Sign-up failed: ${e.message ?: "Unknown error"}")
            }
        }
    }
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

    fun updateUserProfile(user: User, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null) {
                    repository.updateUserProfile(userId, user.toMap())
                    onComplete()
                }
            } catch (e: Exception) {
                println("Failed to update user profile: ${e.message}")
            }
        }
    }
    fun uploadProfilePicture(imageUri: Uri, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadImage(imageUri)
                if (imageUrl != null) {
                    onComplete(imageUrl)
                } else {
                    println("Failed to upload profile picture")
                }
            } catch (e: Exception) {
                println("Failed to upload profile picture: ${e.message}")
            }
        }
    }
}
sealed class AuthStatus {
    object Loading : AuthStatus()
    object Success : AuthStatus()
    object LoggedOut : AuthStatus()
    data class SuccessWithData(val user: Map<String, Any>?) : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}