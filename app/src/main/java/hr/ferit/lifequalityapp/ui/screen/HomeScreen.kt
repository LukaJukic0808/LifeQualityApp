package hr.ferit.lifequalityapp.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.UserData
import hr.ferit.lifequalityapp.ui.components.HomeScreenBody
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.components.PermissionDialog
import hr.ferit.lifequalityapp.ui.components.StatusBar
import hr.ferit.lifequalityapp.ui.measurements.automatic.AutomaticMeasurementWorker
import hr.ferit.lifequalityapp.ui.navigation.Screen
import hr.ferit.lifequalityapp.ui.permissions.FineLocationPermissionTextProvider
import hr.ferit.lifequalityapp.ui.permissions.MicrophonePermissionTextProvider
import hr.ferit.lifequalityapp.ui.permissions.hasBackgroundLocationPermission
import hr.ferit.lifequalityapp.ui.permissions.hasLocationAndRecordAudioPermission
import hr.ferit.lifequalityapp.ui.permissions.isIgnoringBatteryOptimizations
import hr.ferit.lifequalityapp.ui.viewmodels.PermissionViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.ServiceToggleViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.Duration

@SuppressLint("BatteryLife")
@Composable
@TargetApi(29)
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    workManager: WorkManager,
    tokensViewModel: TokensViewModel = koinViewModel(),
    serviceToggleViewModel: ServiceToggleViewModel = koinViewModel(),
    permissionViewModel: PermissionViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val measurementRequest =
        PeriodicWorkRequestBuilder<AutomaticMeasurementWorker>(Duration.ofMinutes(15))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED,
                    )
                    .build(),
            )
            .setInputData(
                Data.Builder().putString("user_id", userData?.userId).build(),
            )
            .setInitialDelay(Duration.ofSeconds(10))
            .build()

    val workInfo = workManager
        .getWorkInfosForUniqueWorkLiveData(stringResource(R.string.uniqueWorkName))
        .observeAsState()
        .value

    serviceToggleViewModel.isMyServiceRunning(workInfo)

    val permissionsToRequest: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    } else {
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    // permission logic
    val dialogQueue = permissionViewModel.visiblePermissionDialogQueue
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            perms.keys.forEach { permission ->
                permissionViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true,
                )
            }
        },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_blue)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            StatusBar(userData, onSignOut, tokensViewModel.tokens)
            HomeScreenBody(
                onManualInputClick = {
                    navController.navigate(Screen.ManualInputScreen.route)
                },
                onToggleService = {
                    if (!NetworkChecker.isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            R.string.network_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        if (context.hasLocationAndRecordAudioPermission()) {
                            if (context.hasBackgroundLocationPermission()) {
                                if (context.isIgnoringBatteryOptimizations(context.packageName)) {
                                    val locationManager =
                                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                    val isGpsEnabled =
                                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                    val isNetworkEnabled =
                                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                                    if (!serviceToggleViewModel.isServiceRunning && !isGpsEnabled && !isNetworkEnabled) {
                                        Toast.makeText(
                                            context,
                                            R.string.turn_on_location_service,
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    } else if (!serviceToggleViewModel.isServiceRunning) {
                                        workManager
                                            .enqueueUniquePeriodicWork(
                                                context.resources.getString(R.string.uniqueWorkName),
                                                ExistingPeriodicWorkPolicy.KEEP,
                                                measurementRequest,
                                            )
                                    } else {
                                        workManager.cancelAllWork()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.battery,
                                        Toast.LENGTH_LONG,
                                    ).show()
                                    val intent = Intent(
                                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                        Uri.fromParts("package", context.packageName, null),
                                    )
                                    context.startActivity(intent)
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.background_location_access_needed,
                                    Toast.LENGTH_LONG,
                                ).show()
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null),
                                )
                                context.startActivity(intent)
                            }
                        } else {
                            multiplePermissionResultLauncher.launch(permissionsToRequest)
                        }
                    }
                },
                serviceToggleViewModel.isServiceRunning,
            )
        }
    }

    // showing permission dialogs
    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        FineLocationPermissionTextProvider(context)
                    }

                    Manifest.permission.RECORD_AUDIO -> {
                        MicrophonePermissionTextProvider(context)
                    }

                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission,
                ),
                onDismiss = permissionViewModel::dismissDialog,
                onOkClick = {
                    permissionViewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission),
                    )
                },
                onGoToAppSettingsClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null),
                    )
                    context.startActivity(intent)
                },
            )
        }
}
