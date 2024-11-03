package Notifications

import Activities.MainActivity
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.collegetracker.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val permissionManager = PermissionManager(context)
        if (permissionManager.hasNotificationPermission()) {
            showNotification(context)
        } else {
            Toast.makeText(context, "Notification Permission Denied", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context) {
        val channelId = "daily_notification_channel"
        val notificationId = 1

        // Create an intent to open MainActivity when the notification is clicked
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create a pending intent
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.book_icon) // Replace with your app's icon
            .setContentTitle("Attendance Reminder")
            .setContentText("Hey, Update your Attendance to track Efficiently")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Automatically remove the notification when clicked


        with(NotificationManagerCompat.from(context)) {
            // Show the notification
            notify(notificationId, builder.build())
        }
    }
}