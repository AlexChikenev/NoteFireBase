package com.example.notefirebase.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmReceiver = getAlarmReceiver()
        val action = intent.action
        Log.d("action", action!!)
        if (action == "stop_action") {
            alarmReceiver?.stopAlarm()
        }
    }

    companion object {
        private var alarmReceiver: AlarmReceiver? = null

        fun setAlarmReceiver(receiver: AlarmReceiver) {
            alarmReceiver = receiver
        }

        fun getAlarmReceiver(): AlarmReceiver? {
            return alarmReceiver
        }
    }
}
