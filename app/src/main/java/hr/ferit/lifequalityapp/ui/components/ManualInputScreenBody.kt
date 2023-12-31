package hr.ferit.lifequalityapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.theme.RaleWay

@Composable
fun ManualInputScreenBody(
    selectedButton: Int,
    onRadioButtonClick: (index: Int) -> Unit,
    onSendAnswerClick: () -> Unit,
) {
    val labels = listOf(
        stringResource(R.string.low),
        stringResource(R.string.medium),
        stringResource(R.string.high),
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.choose_noise_level),
            textAlign = TextAlign.Center,
            fontFamily = RaleWay,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 25.sp,
        )
        Spacer(Modifier.height(20.dp))
        labels.forEach {
                label ->
            if (label == labels[0]) {
                NoiseRadioButton(label, labels.indexOf(label), selectedButton, onRadioButtonClick)
            } else {
                NoiseRadioButton(label, labels.indexOf(label), selectedButton, onRadioButtonClick)
            }
        }
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = {
                onSendAnswerClick.invoke()
            },
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .size(
                    height = 70.dp,
                    width = 200.dp,
                )
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(40.dp),
                ),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = colorResource(
                    R.color.green,
                ),
            ),
        ) {
            Text(
                stringResource(R.string.send),
                textAlign = TextAlign.Center,
                fontFamily = RaleWay,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
    }
}
