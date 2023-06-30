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
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.lifequalityapp.ui.theme.RaleWay
import hr.ferit.lifequalityapp.ui.viewmodels.RadioButtonViewModel

@Composable
fun NoiseRadioButton(
    label: String,
    index: Int,
    radioButtonViewModel: RadioButtonViewModel
){
    val selectedButton = radioButtonViewModel.selectedButton
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        RadioButton(
            selected = index==selectedButton,
            onClick = { radioButtonViewModel.selectButton(index)}
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