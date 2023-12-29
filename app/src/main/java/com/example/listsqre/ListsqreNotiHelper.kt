package com.example.listsqre

import java.util.Calendar
import android.content.Intent
import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(!isNotiDbEmpty(context)) {
            createNotificationChannel(context)
            createNotification(context, readNotiDb(context))
            scheduleAlarm(context, readNotiDb(context)) // recurring daily
        } else { /* do nothing */ }
    }
}

private fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        context.getString(R.string.channel_id),
        context.getString(R.string.channel_name),
        NotificationManager.IMPORTANCE_HIGH
    )
    channel.description = context.getString(R.string.channel_description)
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}

private fun createNotification(context: Context, data: ListsqreNotiData) {
    val permission = "android.permission.POST_NOTIFICATIONS"
    val permissionState = ContextCompat.checkSelfPermission(context, permission)
    if(permissionState == PackageManager.PERMISSION_GRANTED) {
        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.channel_id)).apply {
            setSmallIcon(R.drawable.opt_button)
            setContentTitle(data.t)
            setContentText(data.d)
            priority = NotificationCompat.PRIORITY_HIGH
        }
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(GlobalVar.notifId, builder.build())
    } else {
        // ActivityCompat.requestPermissions()
    }
}

fun scheduleAlarm(context: Context, data: ListsqreNotiData) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, data.h)
        set(Calendar.MINUTE, data.m)
        set(Calendar.SECOND, 0)
        if(timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DATE, 1)
        } else { /* do nothing */ }
    }
    val alarmIntent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    try {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    } catch (e: SecurityException) {
        // do nothing
    }
}

/* --- deprecated, replaced by checking empty Db ---
fun deleteNotification(context: Context, notificationId: Int) {
    val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    nManager.cancel(notificationId)
}
*/