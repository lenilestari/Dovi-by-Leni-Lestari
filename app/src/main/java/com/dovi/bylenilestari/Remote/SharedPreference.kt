package com.importa.ami.Remote

import android.content.Context

object SharedPreferenceLogin {
    private const val PREF_NAME = "MyPrefs"
    private const val KEY_ACCOUNT_EMAIL = "email"

    fun saveAccountInfo(context: Context, email: String) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(KEY_ACCOUNT_EMAIL, email)
        editor.apply()
    }

    fun getEmail(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACCOUNT_EMAIL, null)
    }

    fun clearAccountInfo(context: Context) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.remove(KEY_ACCOUNT_EMAIL)
        editor.apply()
    }
}


