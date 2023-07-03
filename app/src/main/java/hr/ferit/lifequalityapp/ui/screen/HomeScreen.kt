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
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.UserData
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import hr.ferit.lifequalityapp.ui.components.CoarseLocationPermissionTextProvider
import hr.ferit.lifequalityapp.ui.components.HomeScreenBody
import hr.ferit.lifequalityapp.ui.components.FineLocationPermissionTextProvider
import hr.ferit.lifequalityapp.ui.components.MicrophonePermissionTextProvider
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.components.PermissionDialog
import hr.ferit.lifequalityapp.ui.components.StatusBar
import hr.ferit.lifequalityapp.ui.navigation.Screen
import hr.ferit.lifequalityapp.ui.viewmodels.PermissionViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.ServiceToggleViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    tokensViewModel : TokensViewModel = koinViewModel(),
    serviceToggleViewModel : ServiceToggleViewModel = koinViewModel(),
    permissionViewModel: PermissionViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )

    //permission logic
    val dialogQueue = permissionViewModel.visiblePermissionDialogQueue
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                permissionViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
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
            HomeScreenBody(
                onManualInputClick = {
                    navController.navigate(Screen.ManualInputScreen.route)
                },
                onToggleService = {
                    if(!NetworkChecker.isNetworkAvailable(context)){
                        Toast.makeText(
                            context,
                            R.string.network_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        if((ContextCompat.checkSelfPermission(context, permissionsToRequest[0])
                            == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, permissionsToRequest[1])
                            == PackageManager.PERMISSION_GRANTED) &&
                            ContextCompat.checkSelfPermission(context, permissionsToRequest[2])
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            serviceToggleViewModel.toggleService()
                        } else{
                            multiplePermissionResultLauncher.launch(permissionsToRequest)
                        }
                    }
                },
                serviceToggleViewModel.isServiceRunning
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
                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        CoarseLocationPermissionTextProvider(context)
                    }
                    Manifest.permission.RECORD_AUDIO -> {
                        MicrophonePermissionTextProvider(context)
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
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
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

