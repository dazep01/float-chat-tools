// float-chat-tools/app/src/main/java/com/example/floattools/BootReceiver.kt

package com.example.floattools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            context.startService(Intent(context, FloatingButtonService::class.java))
        }
    }
}
