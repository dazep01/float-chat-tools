// float-chat-tools/app/src/main/java/com/example/floattools/NotificationListener.kt

package com.example.floattools

import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "NotifListener"
        val TARGET_PACKAGES = setOf(
            "com.whatsapp",
            "org.telegram.messenger",
            "com.instagram.android",
            "com.facebook.orca"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (packageName !in TARGET_PACKAGES) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val title = extras.getString("android.title") ?: "Unknown"
        val text = extras.getString("android.text") ?: "No message"
        val groupKey = sbn.groupKey

        // Cek apakah notif bisa dibalas
        val remoteInput = getRemoteInput(notification)
        if (remoteInput != null) {
            BubbleChatManager.showBubble(
                this,
                packageName,
                title,
                text,
                remoteInput,
                notification.actions,
                sbn.key
            )
        }
    }

    private fun getRemoteInput(notification: android.app.Notification): android.app.RemoteInput? {
        notification.actions?.forEach { action ->
            action.remoteInputs?.forEach { remoteInput ->
                return remoteInput
            }
        }
        return null
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        BubbleChatManager.dismissBubble(sbn.key)
    }
}
