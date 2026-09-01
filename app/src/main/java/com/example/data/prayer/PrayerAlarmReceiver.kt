package com.example.data.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.NoorNotificationHelper

/**
 * Handles triggered prayer notification alarms and shows gentle notifications.
 */
class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME) ?: "Salat"
        val timeFormatted = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_TIME_FORMATTED) ?: ""
        val offsetMinutes = intent.getIntExtra(PrayerAlarmScheduler.EXTRA_OFFSET_MINUTES, 0)

        val isPreAlert = offsetMinutes < 0
        NoorNotificationHelper.showPrayerAlert(
            context = context,
            prayerName = prayerName,
            timeFormatted = timeFormatted,
            isPreAlert = isPreAlert
        )
    }
}
