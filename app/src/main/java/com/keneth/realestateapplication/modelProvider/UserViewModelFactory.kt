package com.keneth.realestateapplication.modelProvider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.keneth.realestateapplication.repository.UserRepository
import com.keneth.realestateapplication.viewModels.UserViewModel

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
