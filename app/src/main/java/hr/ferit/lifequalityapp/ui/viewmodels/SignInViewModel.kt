package hr.ferit.lifequalityapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lifequalityapp.ui.authentication.SignInResult
import hr.ferit.lifequalityapp.ui.authentication.SignInState
import hr.ferit.lifequalityapp.ui.authentication.UserToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
            )
        }
        if (result.data != null) {
            if (result.data.isNewUser!!) {
                val userId = Firebase.auth.currentUser?.uid
                db.collection("users").document(userId!!).set(UserToken())
            }
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}
