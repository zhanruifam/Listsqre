package com.example.listsqre

import android.os.Build
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
        createNotificationChannel(context)
        createNotification(context, readNotiTitle(context), readNotiDescr(context))
        clearNotiDb(context)
        // scheduleAlarm again for recurring notifications
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            context.getString(R.string.channel_id),
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = context.getString(R.string.channel_description)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

private fun createNotification(context: Context, title: String, descr: String) {
    if(ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
        == PackageManager.PERMISSION_GRANTED) {
        val builder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.opt_button)
            .setContentTitle(title)
            .setContentText(descr)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(0, builder.build())
    } else {
        // ActivityCompat.requestPermissions()
    }
}

fun scheduleAlarm(context: Context, hour: Int, min: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
        if(timeInMillis < System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        } else {
            // do nothing
        }
    }
    val alarmIntent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}