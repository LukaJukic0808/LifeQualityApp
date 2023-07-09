package hr.ferit.lifequalityapp.ui.measurements.automatic

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.location.LocationManager
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.permissions.hasBackgroundLocationPermission
import hr.ferit.lifequalityapp.ui.permissions.hasLocationAndRecordAudioPermission
import hr.ferit.lifequalityapp.ui.permissions.isIgnoringBatteryOptimizations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File
import java.io.IOException
import kotlin.random.Random

@TargetApi(31)
@SuppressLint("MissingPermission")
class AutomaticMeasurementWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams), KoinComponent {
    // private val userId = inputData.getString("user_id")
    override suspend fun doWork(): Result {
        val locationClient: FusedLocationProviderClient = get()
        if (context.isIgnoringBatteryOptimizations(context.packageName)) {
            startForegroundService()
            val mediaRecorder = MediaRecorder(context)
            val outputFile = File(context.filesDir, "test")
            if (context.hasLocationAndRecordAudioPermission() && context.hasBackgroundLocationPermission()) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (!isGpsEnabled && !isNetworkEnabled) {
                    return Result.failure()
                } else {
                    return withContext(Dispatchers.IO) {
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
                                Log.d("success", "${location.latitude}")
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                mediaRecorder.setOutputFile(outputFile.absolutePath)
                                mediaRecorder.prepare()
                                mediaRecorder.start()
                                var maxAmplitude = mediaRecorder.maxAmplitude
                                delay(5000L)
                                maxAmplitude = mediaRecorder.maxAmplitude
                                mediaRecorder.stop()
                                mediaRecorder.release()
                                Log.d("success", "$maxAmplitude")
                            }
                        } catch (e: IOException) {
                            return@withContext Result.failure()
                        }
                        Result.success()
                    }
                }
            }
            return Result.failure()
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
            ),
        )
    }
}
