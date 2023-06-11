package com.example.notefirebase.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TestDismissReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val alarmReceiver = getAlarmReceiver()
        val action = intent.action
        Log.d("action", action!!)
        if (action == "stop_action") {
            alarmReceiver?.stopAlarm()
        }

    }

    companion object {
        private var alarmReceiver: TestAlarmReceiver? = null

        fun setAlarmReceiver(receiver: TestAlarmReceiver) {
            alarmReceiver = receiver
        }

        fun getAlarmReceiver(): TestAlarmReceiver? {
            return alarmReceiver
        }
    }
}
