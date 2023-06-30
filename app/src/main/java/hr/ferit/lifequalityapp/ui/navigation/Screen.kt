package hr.ferit.lifequalityapp.ui.navigation

sealed class Screen(val route: String) {
    object SignInScreen : Screen("sign_in")
    object HomeScreen : Screen("home")
    object ManualInputScreen : Screen("manual_input")
}
