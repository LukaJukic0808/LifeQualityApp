package hr.ferit.lifequalityapp.ui.measurements.automatic

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.location.LocationManager
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.measurements.manual.saveManualInput
import hr.ferit.lifequalityapp.ui.permissions.hasLocationPermission
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

@TargetApi(30)
@SuppressLint("MissingPermission")
class AutomaticMeasurementWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val userId = inputData.getString("user_id")
    override suspend fun doWork(): Result {
        // startForegroundService()
        if (context.hasLocationPermission()) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                return Result.failure()
            } else {
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
                        saveManualInput(
                            context = context,
                            userId = userId!!,
                            location = location,
                            noiseLevel = 0,
                            doesThermometerExist = false,
                            doesBarometerExist = false,
                            doesHumiditySensorExist = false,
                            temperature = 0.0f,
                            pressure = 0.0f,
                            relativeHumidity = 0.0f,
                            currentTokenBalance = 0,
                        )
                        return Result.success()
                    }
                    return Result.failure()
                } catch (e: Exception) {
                    return Result.failure()
                }
            }
        }
        return Result.failure()
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "measurement_channel")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("Dohvaćanje lokacije, razine buke i senzorskih podataka...")
                    .setContentTitle("Prikupljanje podataka u tijeku")
                    .build(),
                FOREGROUND_SERVICE_TYPE_LOCATION or FOREGROUND_SERVICE_TYPE_MICROPHONE,
            ),
        )
    }
}
