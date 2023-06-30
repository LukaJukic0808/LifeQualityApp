package hr.ferit.lifequalityapp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.theme.RaleWay

@Composable
fun Title(){
    Text(
        text = stringResource(R.string.app_name),
        textAlign = TextAlign.Center,
        style = TextStyle(
            color = Color.White,
            fontFamily = RaleWay,
            fontStyle = FontStyle.Italic,
            fontSize = 55.sp,
            fontWeight = FontWeight.SemiBold,
        )
    )
}