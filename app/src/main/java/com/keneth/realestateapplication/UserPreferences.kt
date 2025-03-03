package com.keneth.realestateapplication

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREFS_NAME = "MyFarmPrefs"
    private const val KEY_TOKEN = "token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun storeToken(context: Context, token: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_TOKEN, token)
        editor.apply()
        println("Token stored: $token") // Print the stored token
    }

    fun getToken(context: Context): String? {
        val token = getSharedPreferences(context).getString(KEY_TOKEN, null)
        println("Retrieved token: $token") // Print the retrieved token
        return token
    }

    fun deleteToken(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(KEY_TOKEN)
        editor.apply()
        println("Token deleted") // Print confirmation of token deletion
    }
}