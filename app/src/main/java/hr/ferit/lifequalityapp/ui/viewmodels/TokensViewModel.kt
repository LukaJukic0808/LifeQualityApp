package hr.ferit.lifequalityapp.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lifequalityapp.ui.authentication.UserToken

class TokensViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val userId = Firebase.auth.currentUser?.uid
    var tokens by mutableStateOf(0)

    init {
        getRealTimeTokensUpdate()
    }

    private fun getRealTimeTokensUpdate() {
        if (userId != null) {
            val docRef = db.collection("users").document(userId)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HomeScreen", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    tokens = snapshot.toObject(UserToken::class.java)?.tokens!!
                } else {
                    Log.d("HomeScreen", "Snapshot")
                }
            }
        }
    }
}
