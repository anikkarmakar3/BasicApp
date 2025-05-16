package com.example.pdfrendercompose

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Created by Rajdeep Sarkar on 14-05-2025.
 */
object PreferenceUtils {
    private const val PREFS_FILE_NAME = "shared_prefs"
    private lateinit var application: Context

    fun init(context: Context) {
        application = context.applicationContext
    }

    private fun getSharedPreferences(): SharedPreferences {
        val masterKeyAlias =
            MasterKey.Builder(application).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(
            application,
            PREFS_FILE_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun save(key: String, value: Any) {
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Long -> editor.putLong(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
        }
        editor.apply()
    }


    fun <T> get(key: String, defaultValue: T): T {
        val sharedPreferences = getSharedPreferences()
        val value = when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue)
            is Long -> sharedPreferences.getLong(key, defaultValue)
            is Int -> sharedPreferences.getInt(key, defaultValue)
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue)
            is Float -> sharedPreferences.getFloat(key, defaultValue)
            else -> throw IllegalArgumentException("Unsupported preference value")
        }

        return value as T
    }

}