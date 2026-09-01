package com.example.data.prayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.model.PrayerTime
import java.util.Calendar

/**
 * Manages reliable AlarmManager alarms for Adhan and pre-salat reminders.
 */
object PrayerAlarmScheduler {

    const val ACTION_PRAYER_ALARM = "com.example.action.PRAYER_ALARM"
    const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    const val EXTRA_TIME_FORMATTED = "extra_time_formatted"
    const val EXTRA_OFFSET_MINUTES = "extra_offset_minutes"

    private val PRAYER_REQUEST_CODES = mapOf(
        "Fajr" to 101,
        "Sunrise" to 102,
        "Dhuhr" to 103,
        "Asr" to 104,
        "Maghrib" to 105,
        "Isha" to 106
    )

    fun scheduleAll(
        context: Context,
        prayers: List<PrayerTime>,
        timersMap: Map<String, Int>,
        enabledMap: Map<String, Boolean>
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        for (prayer in prayers) {
            val isEnabled = enabledMap[prayer.name] ?: (prayer.name != "Sunrise")
            val offsetMin = timersMap[prayer.name] ?: 0
            val requestCode = PRAYER_REQUEST_CODES[prayer.name] ?: (200 + prayer.name.hashCode() % 50)

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ALARM
                putExtra(EXTRA_PRAYER_NAME, prayer.name)
                putExtra(EXTRA_TIME_FORMATTED, prayer.timeString)
                putExtra(EXTRA_OFFSET_MINUTES, offsetMin)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!isEnabled) {
                alarmManager.cancel(pendingIntent)
                continue
            }

            // Calculate trigger time in epoch millis
            val now = Calendar.getInstance()
            val alarmCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, prayer.hour)
                set(Calendar.MINUTE, prayer.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MINUTE, offsetMin)
            }

            // If time has already passed today, advance to tomorrow
            if (alarmCal.before(now)) {
                alarmCal.add(Calendar.DAY_OF_YEAR, 1)
            }

            val triggerMillis = alarmCal.timeInMillis

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerMillis,
                            pendingIntent
                        )
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        PRAYER_REQUEST_CODES.forEach { (prayerName, requestCode) ->
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ALARM
                putExtra(EXTRA_PRAYER_NAME, prayerName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
