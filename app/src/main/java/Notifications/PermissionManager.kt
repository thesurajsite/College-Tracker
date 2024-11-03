package Notifications

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    private val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"

    fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestNotificationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(NOTIFICATION_PERMISSION), REQUEST_CODE)
    }

    companion object {
        const val REQUEST_CODE = 1001
    }
}
