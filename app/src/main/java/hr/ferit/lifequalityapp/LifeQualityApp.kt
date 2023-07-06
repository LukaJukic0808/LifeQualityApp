package hr.ferit.lifequalityapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import hr.ferit.lifequalityapp.di.locationClientModule
import hr.ferit.lifequalityapp.di.sensorsModule
import hr.ferit.lifequalityapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LifeQualityApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LifeQualityApp)
            modules(viewModelModule, sensorsModule, locationClientModule)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "measurement_channel",
                "Measurement",
                NotificationManager.IMPORTANCE_HIGH,
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
