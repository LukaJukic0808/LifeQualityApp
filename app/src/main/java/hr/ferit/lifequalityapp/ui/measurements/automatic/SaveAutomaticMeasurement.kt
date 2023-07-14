package hr.ferit.lifequalityapp.ui.measurements.automatic

import android.location.Location
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lifequalityapp.ui.authentication.UserToken
import kotlinx.coroutines.tasks.await

suspend fun saveAutomaticMeasurement(
    userId: String?,
    location: Location,
    noiseAmplitude: Int,
    doesThermometerExist: Boolean,
    doesBarometerExist: Boolean,
    doesHumiditySensorExist: Boolean,
    temperature: Float,
    pressure: Float,
    relativeHumidity: Float,
): Int {
    val db = Firebase.firestore
    var addedTokens = 0
    var currentTokenBalance = 0
    val automaticMeasurement = AutomaticMeasurement()

    automaticMeasurement.latitude = location.latitude
    automaticMeasurement.longitude = location.longitude
    addedTokens += 10

    automaticMeasurement.noiseAmplitude = noiseAmplitude
    if (noiseAmplitude != 0) {
        addedTokens += 10
    }

    if (doesThermometerExist) {
        automaticMeasurement.temperature = temperature
        addedTokens += 5
    }

    if (doesBarometerExist) {
        automaticMeasurement.pressure = pressure
        addedTokens += 5
    }

    if (doesHumiditySensorExist) {
        automaticMeasurement.relativeHumidity = relativeHumidity
        addedTokens += 5
    }

    if (userId != null) {
        try {
            val currentTokensSnapshot = db.collection("users").document(userId).get().await()
            if (currentTokensSnapshot != null && currentTokensSnapshot.exists()) {
                currentTokenBalance =
                    currentTokensSnapshot.toObject(UserToken::class.java)?.tokens!!
            }
        } catch (e: FirebaseFirestoreException) {
            Log.d("Fetching tokens", "Failed")
        }

        db.collection("automaticMeasurements").add(automaticMeasurement)
            .addOnSuccessListener {
                db.collection("users")
                    .document(userId)
                    .set(UserToken(currentTokenBalance + addedTokens))
            }
    }

    return addedTokens
}
