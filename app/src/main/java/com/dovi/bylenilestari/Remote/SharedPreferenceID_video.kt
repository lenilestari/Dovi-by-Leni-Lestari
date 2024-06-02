package com.importa.ami.Remote

import android.content.Context

object SharedPreferenceID_video {
    private const val PREF_NAME = "MyPrefs"
    private const val KEY_ID = "video_id"

    fun saveVideoID(context: Context, id: Int?) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        if (id != null) {
            editor.putInt(KEY_ID, id)
        } else {
            editor.remove(KEY_ID) // Hapus data jika id null
        }
        editor.apply()
    }

    fun getVideoID(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_ID, 0)
    }

    fun clearVideoID(context: Context) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.remove(KEY_ID)
        editor.apply()
    }
}




