package hr.ferit.lifequalityapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.theme.RaleWay

@Composable
fun GoogleButton(
    onClick: () -> Unit,
) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
            .height(IntrinsicSize.Max)
            .padding(1.dp),
        colors = ButtonDefaults.buttonColors(contentColor = Color.Black, containerColor = Color.White),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier
                    .size(21.dp),
            )
            Spacer(Modifier.width(15.dp))
            Text(
                text = stringResource(R.string.sign_in_with_google),
                textAlign = TextAlign.Center,
                fontFamily = RaleWay,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
    }
}
