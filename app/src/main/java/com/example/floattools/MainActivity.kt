// float-chat-tools/app/src/main/java/com/example/floattools/MainActivity.kt

package com.example.floattools

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStartService)
        val btnDraw = findViewById<Button>(R.id.btnOpenDrawPermission)
        val btnNotif = findViewById<Button>(R.id.btnOpenNotifAccess)

        btnStart.setOnClickListener {
            startService(Intent(this, FloatingButtonService::class.java))
            Toast.makeText(this, "✅ Floating service running!", Toast.LENGTH_SHORT).show()
        }

        btnDraw.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }

        btnNotif.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }
}
