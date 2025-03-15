package com.keneth.realestateapplication

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREFS_NAME = "MyFarmPrefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ROLE = "user_role" // New key for user role

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Store token
    fun storeToken(context: Context, token: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_TOKEN, token)
        editor.apply()
        println("Token stored: $token")
    }

    // Retrieve token
    fun getToken(context: Context): String? {
        val token = getSharedPreferences(context).getString(KEY_TOKEN, null)
        println("Retrieved token: $token")
        return token
    }

    // Delete token
    fun deleteToken(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(KEY_TOKEN)
        editor.apply()
        println("Token deleted")
    }

    // Store user role
    fun storeUserRole(context: Context, role: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_USER_ROLE, role)
        editor.apply()
        println("User role stored: $role")
    }

    // Retrieve user role
    fun getUserRole(context: Context): String? {
        val role = getSharedPreferences(context).getString(KEY_USER_ROLE, null)
        println("Retrieved user role: $role")
        return role
    }

    // Delete user role
    fun deleteUserRole(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(KEY_USER_ROLE)
        editor.apply()
        println("User role deleted")
    }
}