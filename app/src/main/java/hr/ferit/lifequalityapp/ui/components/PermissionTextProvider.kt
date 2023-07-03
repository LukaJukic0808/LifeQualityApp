package hr.ferit.lifequalityapp.ui.components


interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}