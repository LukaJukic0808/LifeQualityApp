package hr.ferit.lifequalityapp.ui.measurements.automatic

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.sensing.models.BarometerModel
import hr.ferit.lifequalityapp.sensing.models.HumiditySensorModel
import hr.ferit.lifequalityapp.sensing.models.ThermometerModel
import hr.ferit.lifequalityapp.ui.MainActivity
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.permissions.hasLocationAndRecordAudioPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

@TargetApi(31)
@SuppressLint("MissingPermission")
class AutomaticMeasurementService : Service(), KoinComponent {
    private val handler = Handler(Looper.getMainLooper())
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var initialNotificationDelay = true
    private var userId: String? = null
    private var earnedTokens = 0 // update notification for user to see this
    private lateinit var barometerModel: BarometerModel
    private lateinit var thermometerModel: ThermometerModel
    private lateinit var humiditySensorModel: HumiditySensorModel
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var outputFile: File
    private lateinit var intent: Intent
    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaRecorder = MediaRecorder(this)
        outputFile = File(this.filesDir, "recording")
        locationClient = get()
        barometerModel = get()
        thermometerModel = get()
        humiditySensorModel = get()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        intent = Intent(this, MainActivity::class.java)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("user_id")
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = createMainNotification()
        startForeground(1, notification)
        collectData()
    }

    private fun collectData() {
        coroutineScope.launch {
            if (initialNotificationDelay) {
                delay(10000L)
                initialNotificationDelay = false
            }
            if (!NetworkChecker.isNetworkAvailable(applicationContext)) {
                Log.d("No Internet", "No Internet connection")
            } else {
                if (applicationContext.hasLocationAndRecordAudioPermission()) {
                    val locationManager =
                        applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val isGpsEnabled =
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    val isNetworkEnabled =
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    if (!isGpsEnabled && !isNetworkEnabled) {
                        Log.d("GPS is off", "Turn on GPS")
                    } else {
                        withContext(Dispatchers.Main) {
                            showMeasuringNotification()
                        }
                        withContext(Dispatchers.IO) {
                            try {
                                val location = locationClient.getCurrentLocation(
                                    Priority.PRIORITY_HIGH_ACCURACY,
                                    object : CancellationToken() {
                                        override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                                            CancellationTokenSource().token

                                        override fun isCancellationRequested() = false
                                    },
                                ).await()
                                if (location != null) {
                                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                    mediaRecorder.setOutputFile(outputFile.absolutePath)
                                    mediaRecorder.prepare()
                                    mediaRecorder.start()
                                    var averageAmplitude = mediaRecorder.maxAmplitude
                                    for (i in 1..50) {
                                        delay(100L)
                                        averageAmplitude += mediaRecorder.maxAmplitude
                                    }
                                    mediaRecorder.stop()
                                    earnedTokens += saveAutomaticMeasurement(
                                        userId = userId,
                                        location = location,
                                        noiseAmplitude = averageAmplitude / 50,
                                        doesThermometerExist = thermometerModel.doesSensorExist,
                                        doesBarometerExist = barometerModel.doesSensorExist,
                                        doesHumiditySensorExist = humiditySensorModel.doesSensorExist,
                                        temperature = thermometerModel.temperature.value,
                                        pressure = barometerModel.pressure.value,
                                        relativeHumidity = humiditySensorModel.relativeHumidity.value,
                                    )
                                } else {
                                    Log.d("Location", "Location is null")
                                }
                            } catch (e: IOException) {
                                Log.d("IOException", "Caught exception: $e")
                            }
                        }
                        withContext(Dispatchers.Main) {
                            if (isMainNotificationActive()) {
                                notificationManager.notify(1, createMainNotification())
                            }
                            notificationManager.cancel(2)
                        }
                    }
                } else {
                    Log.d("Missing permissions", "Grant all permissions")
                }
            }
            handler.postDelayed(::collectData, TimeUnit.MINUTES.toMillis(1))
        }
    }

    private fun stop() {
        mediaRecorder.release()
        coroutineScope.cancel()
        handler.removeCallbacksAndMessages(null)
        stopSelf()
    }

    private fun createMainNotification(): Notification {
        return NotificationCompat.Builder(this, "measurement_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText(applicationContext.resources.getString(R.string.collecting_your_data, earnedTokens))
            .setContentTitle(applicationContext.resources.getString(R.string.collecting_data))
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun showMeasuringNotification() {
        val notification = NotificationCompat.Builder(this, "measurement_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(applicationContext.resources.getString(R.string.measurement_running))
            .setProgress(0, 0, true)
            .build()
        notificationManager.notify(2, notification)
    }

    private fun isMainNotificationActive(): Boolean {
        val notifications = notificationManager.activeNotifications
        notifications.forEach { notification ->
            if (notification.id == 1) {
                return true
            }
        }
        return false
    }

    enum class Actions {
        START, STOP
    }
}
