package hr.ferit.lifequalityapp.ui.screen


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.UserData
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import hr.ferit.lifequalityapp.ui.components.HomeScreenBody
import hr.ferit.lifequalityapp.ui.components.NetworkChecker
import hr.ferit.lifequalityapp.ui.components.StatusBar
import hr.ferit.lifequalityapp.ui.navigation.Screen
import hr.ferit.lifequalityapp.ui.viewmodels.ServiceToggleViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    tokensViewModel : TokensViewModel = koinViewModel(),
    serviceToggleViewModel : ServiceToggleViewModel = koinViewModel()
) {
    val context = LocalContext.current

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
                        serviceToggleViewModel.toggleService()
                    }
                },
                serviceToggleViewModel.isServiceRunning
            )
        }
    }
}

