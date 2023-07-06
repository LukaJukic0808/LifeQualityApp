package hr.ferit.lifequalityapp.ui.authentication

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
)

data class UserData(
    val userId: String,
    val username: String?,
    val isNewUser: Boolean?,
    val profilePictureUrl: String?,
)
