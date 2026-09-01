package com.example.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NoorNotificationHelper {

    const val CHANNEL_PRAYER_ALERTS = "noor_prayer_alerts"
    const val CHANNEL_ATHKAR_REMINDERS = "noor_athkar_reminders"
    const val CHANNEL_KHATMA_REMINDERS = "noor_khatma_reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_ALERTS,
                "Prayer Times & Adhan Pre-alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gentle alerts before and during prayer times."
                enableVibration(true)
            }

            val athkarChannel = NotificationChannel(
                CHANNEL_ATHKAR_REMINDERS,
                "Daily Athkar & Sunnah Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Morning and Evening Athkar routines."
                enableVibration(true)
            }

            val khatmaChannel = NotificationChannel(
                CHANNEL_KHATMA_REMINDERS,
                "Khatma Reading Goals",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle daily Quran reading milestones and target reminders."
            }

            notificationManager.createNotificationChannels(listOf(prayerChannel, athkarChannel, khatmaChannel))
        }
    }

    fun showPrayerAlert(context: Context, prayerName: String, timeFormatted: String, isPreAlert: Boolean = false) {
        val title = if (isPreAlert) "Approaching Salat: $prayerName" else "Time for Salat: $prayerName"
        val content = if (isPreAlert) "$prayerName will start in 15 minutes ($timeFormatted). Prepare your heart and wudu 🌿"
        else "Hayya 'ala as-Salah. $prayerName is now at $timeFormatted 🕌"

        showNotification(
            context = context,
            channelId = CHANNEL_PRAYER_ALERTS,
            notificationId = 1001 + prayerName.hashCode() % 100,
            title = title,
            content = content
        )
    }

    fun showAthkarReminder(context: Context, type: String = "Morning") {
        val title = if (type.equals("Morning", ignoreCase = true)) "Morning Athkar • أذكار الصباح" else "Evening Athkar • أذكار المساء"
        val content = "Begin with the remembrance of Allah: 'Verily in the remembrance of Allah do hearts find rest' (13:28) 🕊️"

        showNotification(
            context = context,
            channelId = CHANNEL_ATHKAR_REMINDERS,
            notificationId = 2001,
            title = title,
            content = content
        )
    }

    fun showKhatmaReminder(context: Context, dailyAyahTarget: Int, surahName: String) {
        val title = "Daily Quran Goal • Khatma"
        val content = "Today's goal: $dailyAyahTarget Ayahs starting in $surahName. Keep your spiritual light shining ✨"

        showNotification(
            context = context,
            channelId = CHANNEL_KHATMA_REMINDERS,
            notificationId = 3001,
            title = title,
            content = content
        )
    }

    private fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        content: String
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Notification permission might not be granted yet on Android 13+
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
