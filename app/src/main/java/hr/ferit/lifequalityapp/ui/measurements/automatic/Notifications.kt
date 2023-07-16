package hr.ferit.lifequalityapp.ui.measurements.automatic

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.MainActivity

fun createMainNotification(context: Context, earnedTokens: Int): Notification {
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    return NotificationCompat.Builder(context, "measurement_channel")
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentText(context.resources.getString(R.string.collecting_your_data, earnedTokens))
        .setContentTitle(context.resources.getString(R.string.collecting_data))
        .setContentIntent(pendingIntent)
        .build()
}

fun showNoInternetNotification(context: Context, notificationManager: NotificationManager) {
    val notification = NotificationCompat.Builder(context, "measurement_channel")
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(context.resources.getString(R.string.measurement_failure))
        .setContentText(context.resources.getString(R.string.no_internet))
        .build()
    notificationManager.notify(2, notification)
}

fun showNoGpsNotification(context: Context, notificationManager: NotificationManager) {
    val notification = NotificationCompat.Builder(context, "measurement_channel")
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(context.resources.getString(R.string.measurement_failure))
        .setContentText(context.resources.getString(R.string.no_gps))
        .build()
    notificationManager.notify(2, notification)
}

fun showMeasuringNotification(context: Context, notificationManager: NotificationManager) {
    val notification = NotificationCompat.Builder(context, "measurement_channel")
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(context.resources.getString(R.string.measurement_running))
        .setProgress(0, 0, true)
        .build()
    notificationManager.notify(2, notification)
}

fun showMeasuringSuccessNotification(context: Context, notificationManager: NotificationManager, measurementTokens: Int) {
    val notification = NotificationCompat.Builder(context, "measurement_channel")
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(context.resources.getString(R.string.measurement_success))
        .setContentText(context.resources.getString(R.string.added_tokens, measurementTokens))
        .setProgress(0, 0, false)
        .build()
    notificationManager.notify(2, notification)
}

fun showMeasuringFailureNotification(context: Context, notificationManager: NotificationManager) {
    val notification = NotificationCompat.Builder(context, "measurement_channel")
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(context.resources.getString(R.string.measurement_failure))
        .setContentText(context.resources.getString(R.string.location_unreachable))
        .build()
    notificationManager.notify(2, notification)
}

fun isMainNotificationActive(notificationManager: NotificationManager): Boolean {
    val notifications = notificationManager.activeNotifications
    notifications.forEach { notification ->
        if (notification.id == 1) {
            return true
        }
    }
    return false
}
