package hr.ferit.lifequalityapp.ui.authentication

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
)
