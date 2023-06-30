package hr.ferit.lifequalityapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.SignInState
import hr.ferit.lifequalityapp.ui.components.GoogleButton
import hr.ferit.lifequalityapp.ui.components.Title

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Box(modifier = Modifier
            .fillMaxSize()
            .paint(
            painterResource(id = R.drawable.login_background),
            contentScale = ContentScale.FillBounds
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Title()
            GoogleButton(onSignInClick)
        }
    }
}