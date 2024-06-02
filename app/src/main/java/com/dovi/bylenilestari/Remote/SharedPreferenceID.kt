package com.importa.ami.Remote

import android.content.Context

object SharedPreferenceID {
    private const val PREF_NAME = "MyPrefs"
    private const val KEY_ID = "id"

    fun saveAccountInfo(context: Context, id: Int?) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        if (id != null) {
            editor.putString(KEY_ID, id.toString())
        } else {
            editor.remove(KEY_ID) // Hapus data jika id null
        }
        editor.apply()
    }

    fun getID(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ID, null)?.toIntOrNull() ?: 0
    }

    fun clearAccountInfo(context: Context) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.remove(KEY_ID)
        editor.apply()
    }
}



