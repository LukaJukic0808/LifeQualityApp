package hr.ferit.lifequalityapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.UserData

@Composable
fun StatusBar(
    userData: UserData?,
    onSignOut: () -> Unit,
    tokens: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter = painterResource(id = R.drawable.log_out),
            contentDescription = "Sign Out",
            modifier = Modifier
                .size(40.dp)
                .clickable { onSignOut.invoke() }
                .rotate(180f),
        )
        if (userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .padding(3.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Text(
            text = tokens.toString(),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Black,
        )
        Spacer(Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.token),
            contentDescription = "Tokens",
            modifier = Modifier
                .size(25.dp)
                .padding(
                    end = 5.dp,
                ),
        )
    }
}
