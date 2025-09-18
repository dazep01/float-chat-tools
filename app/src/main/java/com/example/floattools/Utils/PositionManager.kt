// float-chat-tools/app/src/main/java/com/example/floattools/Utils/PositionManager.kt

package com.example.floattools.Utils

import android.content.Context

object PositionManager {

    private const val PREFS_NAME = "float_prefs"
    private const val KEY_X = "pos_x"
    private const val KEY_Y = "pos_y"

    fun savePosition(context: Context, x: Int, y: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_X, x).putInt(KEY_Y, y).apply()
    }

    fun loadPosition(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val x = prefs.getInt(KEY_X, 100)
        val y = prefs.getInt(KEY_Y, 300)
        return Pair(x, y)
    }
}
