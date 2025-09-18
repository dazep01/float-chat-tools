// float-chat-tools/app/src/main/java/com/example/floattools/BubbleChatManager.kt

package com.example.floattools

import android.app.PendingIntent
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.RemoteInput

object BubbleChatManager {

    private val bubbles = mutableMapOf<String, View>()
    private lateinit var windowManager: WindowManager

    fun showBubble(
        context: Context,
        packageName: String,
        title: String,
        message: String,
        remoteInput: RemoteInput,
        actions: Array<android.app.Notification.Action>?,
        key: String
    ) {
        if (bubbles.containsKey(key)) return // avoid duplicate

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val bubbleView = LayoutInflater.from(context).inflate(R.layout.bubble_chat_layout, null)
        val tvTitle = bubbleView.findViewById<TextView>(R.id.tvBubbleTitle)
        val tvMessage = bubbleView.findViewById<TextView>(R.id.tvBubbleMessage)
        val etReply = bubbleView.findViewById<EditText>(R.id.etReply)
        val btnSend = bubbleView.findViewById<ImageView>(R.id.btnSend)
        val btnClose = bubbleView.findViewById<ImageView>(R.id.btnClose)

        tvTitle.text = title
        tvMessage.text = message

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 50
            y = 150 + (bubbles.size * 110)
        }

        btnSend.setOnClickListener {
            val replyText = etReply.text.toString()
            if (replyText.isNotEmpty()) {
                actions?.forEach { action ->
                    val intent = action.actionIntent
                    val pi = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent.fillIn(context, Intent().apply {
                            putExtra("key", key)
                        }),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )

                    val results = Bundle().apply {
                        putCharSequence(remoteInput.resultKey, replyText)
                    }

                    RemoteInput.addResultsToIntent(arrayOf(remoteInput), pi.intent, results)
                    try {
                        pi.send()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                windowManager.removeView(bubbleView)
                bubbles.remove(key)
            }
        }

        btnClose.setOnClickListener {
            windowManager.removeView(bubbleView)
            bubbles.remove(key)
        }

        bubbles[key] = bubbleView
        windowManager.addView(bubbleView, layoutParams)
    }

    fun dismissBubble(key: String) {
        bubbles[key]?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bubbles.remove(key)
        }
    }
}
