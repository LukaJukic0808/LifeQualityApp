package hr.ferit.lifequalityapp.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.sensing.models.BarometerModel
import hr.ferit.lifequalityapp.sensing.models.HumiditySensorModel
import hr.ferit.lifequalityapp.sensing.models.ThermometerModel
import hr.ferit.lifequalityapp.ui.authentication.UserData
import hr.ferit.lifequalityapp.ui.components.ManualInputScreenBody
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.components.PermissionDialog
import hr.ferit.lifequalityapp.ui.components.StatusBar
import hr.ferit.lifequalityapp.ui.measurements.manual.saveManualInput
import hr.ferit.lifequalityapp.ui.permissions.FineLocationPermissionTextProvider
import hr.ferit.lifequalityapp.ui.permissions.hasLocationPermission
import hr.ferit.lifequalityapp.ui.viewmodels.PermissionViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.RadioButtonViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@SuppressLint("MissingPermission")
@Composable
fun ManualInputScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    tokensViewModel: TokensViewModel = koinViewModel(),
    radioButtonViewModel: RadioButtonViewModel = koinViewModel(),
    thermometerModel: ThermometerModel = get(),
    humiditySensorModel: HumiditySensorModel = get(),
    barometerModel: BarometerModel = get(),
    permissionViewModel: PermissionViewModel = koinViewModel(),
    locationClient: FusedLocationProviderClient = get(),
) {
    val context = LocalContext.current
    val temperature by thermometerModel.temperature.collectAsStateWithLifecycle()
    val pressure by barometerModel.pressure.collectAsStateWithLifecycle()
    val relativeHumidity by humiditySensorModel.relativeHumidity.collectAsStateWithLifecycle()
    val permissionToRequest = Manifest.permission.ACCESS_FINE_LOCATION

    // permission logic
    val dialogQueue = permissionViewModel.visiblePermissionDialogQueue
    val locationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionViewModel.onPermissionResult(
                permission = permissionToRequest,
                isGranted = isGranted,
            )
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
            ManualInputScreenBody(
                radioButtonViewModel.selectedButton,
                onRadioButtonClick = { index ->
                    radioButtonViewModel.selectButton(index)
                },
                onSendAnswerClick = {
                    if (!NetworkChecker.isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            R.string.network_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        if (context.hasLocationPermission()) {
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
                                )
                                    .show()
                            } else {
                                locationClient.getCurrentLocation(
                                    Priority.PRIORITY_HIGH_ACCURACY,
                                    object : CancellationToken() {
                                        override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                                            CancellationTokenSource().token

                                        override fun isCancellationRequested() = false
                                    },
                                )
                                    .addOnSuccessListener { location: Location? ->
                                        if (location == null) {
                                            Toast.makeText(
                                                context,
                                                R.string.location_unreachable,
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        } else {
                                            saveManualInput(
                                                context = context,
                                                userId = userData?.userId!!,
                                                location = location,
                                                noiseLevel = radioButtonViewModel.selectedButton,
                                                doesThermometerExist = thermometerModel.doesSensorExist,
                                                doesBarometerExist = barometerModel.doesSensorExist,
                                                doesHumiditySensorExist = humiditySensorModel.doesSensorExist,
                                                temperature = temperature,
                                                pressure = pressure,
                                                relativeHumidity = relativeHumidity,
                                                currentTokenBalance = tokensViewModel.tokens,
                                            )
                                            navController.popBackStack()
                                        }
                                    }
                            }
                        } else {
                            locationPermissionResultLauncher.launch(permissionToRequest)
                        }
                    }
                },
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
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission,
                ),
                onDismiss = permissionViewModel::dismissDialog,
                onOkClick = {
                    permissionViewModel.dismissDialog()
                    locationPermissionResultLauncher.launch(
                        permission,
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
