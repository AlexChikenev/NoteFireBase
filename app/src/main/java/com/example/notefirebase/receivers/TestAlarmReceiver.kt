package com.example.notefirebase.receivers

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notefirebase.R

@Suppress("DEPRECATION")
class TestAlarmReceiver : BroadcastReceiver(){
    private var mediaPlayer: MediaPlayer? = null
    private var ringtone: Ringtone? = null
     var isAlarmPlaying = false

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        var ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri)

        // If alarm sound doesn't set, use phone ringtone
        if (ringtone == null || ringtone!!.streamType == RingtoneManager.TYPE_RINGTONE) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                0
            )
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = true

            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_RING)
            mediaPlayer?.setDataSource(context, ringtoneUri)
            mediaPlayer?.prepare()
            isAlarmPlaying = true
            mediaPlayer?.start()
        } else {
            isAlarmPlaying = true
            ringtone!!.play()
        }

        showNotification(context, intent)
    }

    private fun showNotification(context: Context, intent: Intent) {
        // Set up notification channel
        val notificationAlarmId = 1
        val name = "Alarm"
        val descriptionText = "Alarm notification"
        val channelId = "alarm notification"
        val importance = NotificationManager.IMPORTANCE_LOW

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val alarmNotification = "Задача по по времени"
        val taskName = intent.getStringExtra("taskName")

        val dismissIntent = Intent(context, TestDismissReceiver::class.java)
        dismissIntent.action = "stop_action"
        TestDismissReceiver.setAlarmReceiver(this) // Передача экземпляра TestAlarmReceiver
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.toast_img)
            .setContentTitle(alarmNotification)
            .setContentText("Будильник для задачи: $taskName сработал")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.toast_img, "Stop", dismissPendingIntent)
        builder.setDeleteIntent(dismissPendingIntent)



        // Show notification
        with(NotificationManagerCompat.from(context)) {
            // Check permission for notifications
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationAlarmId, builder.build())
            }
        }
    }

    fun stopAlarm() {
        ringtone?.stop()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        ringtone = null
        isAlarmPlaying = false
    }
}
