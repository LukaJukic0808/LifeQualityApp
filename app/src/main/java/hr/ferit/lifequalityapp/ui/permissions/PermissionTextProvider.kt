package hr.ferit.lifequalityapp.ui.permissions


interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}