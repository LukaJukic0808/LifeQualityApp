package hr.ferit.lifequalityapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import hr.ferit.lifequalityapp.ui.theme.RaleWay

@Composable
fun NoiseRadioButton(
    label: String,
    index: Int,
    selectedButton : Int,
    onRadioButtonClick: (index: Int) -> Unit,
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        RadioButton(
            selected = index==selectedButton,
            onClick = { onRadioButtonClick.invoke(index) }
        )
        Text(
            text = label,
            textAlign = TextAlign.Center,
            fontFamily = RaleWay,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}