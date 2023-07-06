package hr.ferit.lifequalityapp.ui.measurements.manual

import android.content.Context
import android.location.Location
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lifequalityapp.R
import hr.ferit.lifequalityapp.ui.authentication.UserToken

fun saveManualInput(
    context: Context,
    userId: String,
    location: Location,
    noiseLevel: Int,
    doesThermometerExist: Boolean,
    doesBarometerExist: Boolean,
    doesHumiditySensorExist: Boolean,
    temperature: Float,
    pressure: Float,
    relativeHumidity: Float,
    currentTokenBalance: Int,
) {
    val db = Firebase.firestore
    val manualInput = ManualInput()
    var addedTokens = 0

    manualInput.latitude = location.latitude
    manualInput.longitude = location.longitude
    addedTokens += 10

    manualInput.noiseLevel = noiseLevel
    addedTokens += 10

    if (doesThermometerExist) {
        manualInput.temperature = temperature
        addedTokens += 5
    }

    if (doesBarometerExist) {
        manualInput.pressure = pressure
        addedTokens += 5
    }

    if (doesHumiditySensorExist) {
        manualInput.relativeHumidity = relativeHumidity
        addedTokens += 5
    }

    db.collection("manualInputs").add(manualInput)
        .addOnSuccessListener {
            db.collection("users")
                .document(userId)
                .set(UserToken(currentTokenBalance + addedTokens))
            Toast.makeText(
                context,
                String.format(
                    context.resources.getString(R.string.answer_stored),
                    addedTokens,
                ),
                Toast.LENGTH_SHORT,
            ).show()
        }
        .addOnFailureListener {
            Toast.makeText(
                context,
                R.string.answer_not_stored,
                Toast.LENGTH_SHORT,
            ).show()
        }
}
