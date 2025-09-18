// float-chat-tools/app/src/main/java/com/example/floattools/FloatingButtonService.kt

package com.example.floattools

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout

class FloatingButtonService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: ImageView
    private var params: WindowManager.LayoutParams? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        floatingButton = LayoutInflater.from(this)
            .inflate(R.layout.floating_button_layout, null) as ImageView

        params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
        }

        // Load saved position
        val pos = PositionManager.loadPosition(this)
        params?.x = pos.first
        params?.y = pos.second

        params?.gravity = Gravity.TOP or Gravity.START

        floatingButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params?.x ?: 0
                    initialY = params?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    params?.x = initialX + (event.rawX - initialTouchX).toInt()
                    params?.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingButton, params)
                    false
                }

                MotionEvent.ACTION_UP -> {
                    // Save position
                    PositionManager.savePosition(this, params?.x ?: 0, params?.y ?: 0)
                    showToolsMenu()
                    true
                }

                else -> false
            }
        }

        startForeground(1, NotificationHelper.createNotification(this))
        windowManager.addView(floatingButton, params)
    }

    private fun showToolsMenu() {
        val menuView = LayoutInflater.from(this).inflate(R.layout.tools_menu_layout, null) as LinearLayout

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = params?.x ?: 0
            y = (params?.y ?: 0) + 80
        }

        menuView.findViewById<ImageView>(R.id.btnWhatsapp).setOnClickListener {
            launchApp("com.whatsapp")
            windowManager.removeView(menuView)
        }
        menuView.findViewById<ImageView>(R.id.btnTelegram).setOnClickListener {
            launchApp("org.telegram.messenger")
            windowManager.removeView(menuView)
        }
        menuView.findViewById<ImageView>(R.id.btnInstagram).setOnClickListener {
            launchApp("com.instagram.android")
            windowManager.removeView(menuView)
        }
        menuView.findViewById<ImageView>(R.id.btnMessenger).setOnClickListener {
            launchApp("com.facebook.orca")
            windowManager.removeView(menuView)
        }

        menuView.findViewById<ImageView>(R.id.btnVolume).setOnClickListener {
            ToolManager.showVolumeSlider(this)
            windowManager.removeView(menuView)
        }

        menuView.findViewById<ImageView>(R.id.btnFlashlight).setOnClickListener {
            ToolManager.toggleFlashlight(this)
            windowManager.removeView(menuView)
        }

        menuView.findViewById<ImageView>(R.id.btnCloseMenu).setOnClickListener {
            windowManager.removeView(menuView)
        }

        windowManager.addView(menuView, layoutParams)
    }

    private fun launchApp(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingButton.isInitialized) {
            windowManager.removeView(floatingButton)
        }
    }
}
