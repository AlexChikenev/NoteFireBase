package com.example.notefirebase.utils

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.VibrationEffect
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.notefirebase.R
import android.os.Vibrator
import android.text.StaticLayout
import android.util.TypedValue
import androidx.core.graphics.withTranslation

@Suppress("DEPRECATION")
class Helper(
    private val activity: FragmentActivity
) {
    // Navigate into app
    fun navigate(fragment: Fragment) {
        activity.supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment)
            .commit()
    }

    // Create custom toast
    fun customToast(context: Context, content: Int) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_layout, null)
        val toast = Toast(context)
        val text = layout.findViewById<TextView>(R.id.textView)
        text.setText(content)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    // Disable UI controls
    fun uiControls(decorView: View) {
        val uiOptions: Int =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.setSystemUiVisibility(uiOptions)
    }

    // Vibration
    fun vibrateDevice() {
        val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}