// float-chat-tools/app/src/main/java/com/example/floattools/ToolManager.kt

package com.example.floattools

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView

object ToolManager {

    fun showVolumeSlider(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layout = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundColor(0xFF212121.toInt())
            setPadding(32, 32, 32, 32)
        }

        val tv = TextView(context).apply {
            text = "Volume Control"
            setTextColor(-0x1) // white
            textSize = 16f
        }

        val seekBar = SeekBar(context).apply {
            max = maxVol
            progress = currentVol
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        val closeBtn = android.widget.Button(context).apply {
            text = "Close"
            setOnClickListener {
                wm.removeView(layout)
            }
        }

        layout.addView(tv)
        layout.addView(seekBar)
        layout.addView(closeBtn)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = android.view.Gravity.CENTER
        }

        wm.addView(layout, params)
    }

    fun toggleFlashlight(context: Context) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return

            val state = cameraManager.getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true

            val torch = !isTorchOn
            isTorchOn = torch
            cameraManager.setTorchMode(cameraId, torch)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private var isTorchOn = false
}
