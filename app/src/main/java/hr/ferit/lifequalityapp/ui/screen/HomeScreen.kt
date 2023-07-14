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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.ActivityCompat.startForegroundService
import androidx.navigation.NavController
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.UserData
import hr.ferit.lifequalityapp.ui.components.HomeScreenBody
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.components.PermissionDialog
import hr.ferit.lifequalityapp.ui.components.StatusBar
import hr.ferit.lifequalityapp.ui.measurements.automatic.AutomaticMeasurementService
import hr.ferit.lifequalityapp.ui.navigation.Screen
import hr.ferit.lifequalityapp.ui.permissions.FineLocationPermissionTextProvider
import hr.ferit.lifequalityapp.ui.permissions.MicrophonePermissionTextProvider
import hr.ferit.lifequalityapp.ui.permissions.hasLocationAndRecordAudioPermission
import hr.ferit.lifequalityapp.ui.viewmodels.PermissionViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.ServiceToggleViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("BatteryLife")
@Composable
@TargetApi(29)
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    tokensViewModel: TokensViewModel = koinViewModel(),
    serviceToggleViewModel: ServiceToggleViewModel = koinViewModel(),
    permissionViewModel: PermissionViewModel = koinViewModel(),
) {
    val context = LocalContext.current
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
                    if (!NetworkChecker.isNetworkAvailable(context) && !serviceToggleViewModel.isServiceRunning) {
                        Toast.makeText(
                            context,
                            R.string.network_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else if (context.hasLocationAndRecordAudioPermission() && !serviceToggleViewModel.isServiceRunning) {
                        val locationManager =
                            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGpsEnabled =
                            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnabled =
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        if (!isGpsEnabled && !isNetworkEnabled) {
                            Toast.makeText(
                                context,
                                R.string.turn_on_location_service,
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            Intent(context, AutomaticMeasurementService::class.java).also {
                                it.action = AutomaticMeasurementService.Actions.START.toString()
                                it.putExtra("user_id", userData?.userId)
                                startForegroundService(context, it)
                                serviceToggleViewModel.toggleService()
                            }
                        }
                    } else if (!serviceToggleViewModel.isServiceRunning) {
                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                    } else {
                        Intent(context, AutomaticMeasurementService::class.java).also {
                            it.action = AutomaticMeasurementService.Actions.STOP.toString()
                            startForegroundService(context, it)
                            serviceToggleViewModel.toggleService()
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
