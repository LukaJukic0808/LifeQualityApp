package hr.ferit.lifequalityapp.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.UserData
import hr.ferit.lifequalityapp.ui.authentication.UserToken
import hr.ferit.lifequalityapp.ui.components.FineLocationPermissionTextProvider
import hr.ferit.lifequalityapp.ui.components.ManualInputScreenBody
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.components.PermissionDialog
import hr.ferit.lifequalityapp.ui.components.StatusBar
import hr.ferit.lifequalityapp.ui.measurements.ManualInput
import hr.ferit.lifequalityapp.ui.viewmodels.BarometerViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.HumiditySensorViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.PermissionViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.RadioButtonViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.ThermometerViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManualInputScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    tokensViewModel : TokensViewModel = koinViewModel(),
    radioButtonViewModel: RadioButtonViewModel = koinViewModel(),
    thermometerViewModel: ThermometerViewModel = koinViewModel(),
    humiditySensorViewModel: HumiditySensorViewModel = koinViewModel(),
    barometerViewModel: BarometerViewModel = koinViewModel(),
    permissionViewModel: PermissionViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val temperature by thermometerViewModel.temperature.collectAsStateWithLifecycle()
    val pressure by barometerViewModel.pressure.collectAsStateWithLifecycle()
    val relativeHumidity by humiditySensorViewModel.relativeHumidity.collectAsStateWithLifecycle()
    val permissionToRequest = Manifest.permission.ACCESS_FINE_LOCATION

    //permission logic
    val dialogQueue = permissionViewModel.visiblePermissionDialogQueue
    val locationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionViewModel.onPermissionResult(
                permission = permissionToRequest,
                isGranted = isGranted
            )
        }
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
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if(ContextCompat.checkSelfPermission(context, permissionToRequest)
                            == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            val manualInput = ManualInput()
                            var addedTokens = 0

                            manualInput.noiseLevel = radioButtonViewModel.selectedButton
                            addedTokens += 10

                            if (thermometerViewModel.doesSensorExist) {
                                manualInput.temperature = temperature
                                addedTokens += 5
                            }

                            if (barometerViewModel.doesSensorExist) {
                                manualInput.pressure = pressure
                                addedTokens += 5
                            }

                            if (humiditySensorViewModel.doesSensorExist) {
                                manualInput.relativeHumidity = relativeHumidity
                                addedTokens += 5
                            }

                            db.collection("manualInputs").add(manualInput)
                                .addOnSuccessListener {
                                    db.collection("users")
                                        .document(userData?.userId!!)
                                        .set(UserToken(tokensViewModel.tokens + addedTokens))
                                    Toast.makeText(
                                        context,
                                        String.format(
                                            context.resources.getString(R.string.answer_stored),
                                            addedTokens
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        R.string.answer_not_stored,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            navController.popBackStack()
                        } else{
                            locationPermissionResultLauncher.launch(permissionToRequest)
                        }
                    }
                }
            )
        }
    }

    //showing permission dialogs
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
                    permission
                ),
                onDismiss = permissionViewModel::dismissDialog,
                onOkClick = {
                    permissionViewModel.dismissDialog()
                    locationPermissionResultLauncher.launch(
                        permission
                    )
                },
                onGoToAppSettingsClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }
            )
        }
}
