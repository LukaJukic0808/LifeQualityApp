package hr.ferit.lifequalityapp.ui.screen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.GoogleAuthUiClient
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.measurements.automatic.AutomaticMeasurementService
import hr.ferit.lifequalityapp.ui.navigation.Screen
import hr.ferit.lifequalityapp.ui.permissions.isMeasurementRunning
import hr.ferit.lifequalityapp.ui.viewmodels.SignInViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val applicationContext = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
        )
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SignInScreen.route) {
        composable(Screen.SignInScreen.route) {
            val signInViewModel: SignInViewModel = koinViewModel()
            val state by signInViewModel.state.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(key1 = Unit) {
                if (googleAuthUiClient.getSignedInUser() != null) {
                    navController.navigate(Screen.HomeScreen.route)
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        coroutineScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch,
                            )
                            signInViewModel.onSignInResult(signInResult)
                        }
                    }
                },
            )

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        R.string.sign_in_success,
                        Toast.LENGTH_SHORT,
                    ).show()
                    navController.navigate(Screen.HomeScreen.route)
                    signInViewModel.resetState()
                }
            }

            SignInScreen(
                state = state,
                onSignInClick = {
                    if (!NetworkChecker.isNetworkAvailable(applicationContext)) {
                        Toast.makeText(
                            applicationContext,
                            R.string.network_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch,
                                ).build(),
                            )
                        }
                    }
                },
            )
        }

        composable(Screen.HomeScreen.route) {
            val coroutineScope = rememberCoroutineScope()
            HomeScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    coroutineScope.launch {
                        googleAuthUiClient.signOut()
                        Toast.makeText(
                            applicationContext,
                            R.string.sign_out_success,
                            Toast.LENGTH_SHORT,
                        ).show()
                        if (applicationContext.isMeasurementRunning()) {
                            Intent(applicationContext, AutomaticMeasurementService::class.java).also {
                                it.action = AutomaticMeasurementService.Actions.STOP.toString()
                                ActivityCompat.startForegroundService(applicationContext, it)
                            }
                        }
                        navController.popBackStack()
                    }
                },
                navController = navController,
            )
        }

        composable(Screen.ManualInputScreen.route) {
            val coroutineScope = rememberCoroutineScope()
            ManualInputScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    coroutineScope.launch {
                        googleAuthUiClient.signOut()
                        Toast.makeText(
                            applicationContext,
                            R.string.sign_out_success,
                            Toast.LENGTH_SHORT,
                        ).show()
                        if (applicationContext.isMeasurementRunning()) {
                            Intent(applicationContext, AutomaticMeasurementService::class.java).also {
                                it.action = AutomaticMeasurementService.Actions.STOP.toString()
                                ActivityCompat.startForegroundService(applicationContext, it)
                            }
                        }
                        navController.navigate(Screen.SignInScreen.route) {
                            popUpTo(Screen.SignInScreen.route)
                        }
                    }
                },
                navController = navController,
            )
        }
    }
}
